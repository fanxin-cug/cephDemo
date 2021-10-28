package com.zhanghao.ceph.Utils.geo.tile.mem;


import com.zhanghao.ceph.Utils.geo.UtilImageInfo;
import com.zhanghao.ceph.Utils.geo.tile.core.ArrayHelper;
import com.zhanghao.ceph.Utils.geo.tile.core.GdalIOUtil;
import com.zhanghao.ceph.Utils.geo.tile.core.Lookup4326;
import com.zhanghao.ceph.Utils.geo.tile.core.ProgressHelper;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.awt.*;
import java.util.Date;

/**
 * Created by Lenovo on 2021/8/27.
 */
public class MemImageHelper {

    public static void main(String[] args) {

        //calNDVI();
    }

    /**
     * 大图像读入内存
     *
     * @param fileName 图像文件名
     * @return
     */
    public static MemImage readToMemImage(String fileName) {
        UtilImageInfo utilImageInfo = new UtilImageInfo(fileName);
        int bandNum = utilImageInfo.getBandNumb();
        return readToMemImage(fileName, bandNum, ArrayHelper.getArrayByNum(bandNum));
    }


    /**
     * 大图像读入内存
     *
     * @param fileName 图像文件名
     * @param bandNum  读取的波段数量
     * @param bands    读取的波段序号(下标从0开始计数)
     * @return 内存图像的波段下标从0开始，连续编号。例如读取3个波段，读取后内存图像的波段号为{0,1,2}
     */
    public static MemImage readToMemImage(String fileName, Integer bandNum, Integer[] bands) {
        long time1 = new Date().getTime();
        UtilImageInfo utilImageInfo = new UtilImageInfo(fileName);
        if (utilImageInfo.getBandNumb() < ArrayHelper.getMax(bands) ||
                bandNum != bands.length) {
            System.out.println("要读取的某个波段编号大于影像实际波段编号\n或者读取的波段数量与指定波段列表的数量不符");
            return null;
        }

        Boolean isOnlyOneMemBand = false;
        if (bandNum == 3 && ArrayHelper.getMax(bands) == 0) {
            isOnlyOneMemBand = true;
        }

        int dataType = utilImageInfo.isByte() ? 1 : 2;

        // 分块读写参数
        // 分块的行数（高度）
        int nPerChunk = ChunkHelper.getPerChunkHeight(utilImageInfo.getnWidth(), utilImageInfo.getnHeight(), dataType);
        // 分块数
        int nChunkCount = 1;
        // 最后一块的大小
        int nLastChunk = utilImageInfo.getnHeight();
        // 如果需要读取的图像行数太多，则需要进行分块读取
        if (utilImageInfo.getnHeight() > nPerChunk) {
            nChunkCount = utilImageInfo.getnHeight() / nPerChunk;
            nLastChunk = nPerChunk + utilImageInfo.getnHeight() % nPerChunk;
        }

        int level = Lookup4326.getMaxLevel(utilImageInfo.getDuResolution());
        MemImage memImage = new MemImage(utilImageInfo.getnWidth(), utilImageInfo.getnHeight(), bandNum, isOnlyOneMemBand, nChunkCount, nPerChunk, level, dataType);
        memImage.setUtilImageInfo(utilImageInfo);

        Rectangle srcChunkRect = new Rectangle();
        Dataset dataset = gdal.Open(fileName, gdalconstConstants.GA_ReadOnly);
        for (int iBand = 0; iBand < bandNum; iBand++) {
            // 只读实际需要读取的波段
            if (iBand == 0 || !isOnlyOneMemBand) {
                Band readBand = dataset.GetRasterBand(bands[iBand] + 1);
                for (int iChunk = 0; iChunk < nChunkCount; iChunk++) {
                    // 分块读
                    int yPerSize = nPerChunk;
                    srcChunkRect.y = iChunk * nPerChunk;
                    if (ProgressHelper.isPrintDetailedLog) {
                        long time2 = new Date().getTime();
                        System.out.println("当前读到的行号：" + srcChunkRect.y + ",波段号：" + iBand + "，分块号：" + iChunk + "，总块数：" + nChunkCount + ",已耗时" + (time2 - time1) + "毫秒");
                    }
                    if (nChunkCount == 1 || iChunk == (nChunkCount - 1))  // 只有一块 或者 最后一块
                    {
                        yPerSize = nLastChunk;
                    }
                    srcChunkRect.x = 0;
                    srcChunkRect.width = utilImageInfo.getnWidth();
                    srcChunkRect.height = yPerSize;
                    if (dataType != 1) {
                        short[] shorts = new short[srcChunkRect.width * srcChunkRect.height];
                        readBand.ReadRaster(srcChunkRect.x, srcChunkRect.y, srcChunkRect.width, srcChunkRect.height, shorts);
                        MemCell memCell = new MemCell(srcChunkRect.width, srcChunkRect.height, iBand, iChunk, shorts);
                        memImage.setMemCell(memCell);
                    } else {
                        byte[] bytes = new byte[srcChunkRect.width * srcChunkRect.height];
                        readBand.ReadRaster(srcChunkRect.x, srcChunkRect.y, srcChunkRect.width, srcChunkRect.height, bytes);
                        MemCell memCell = new MemCell(srcChunkRect.width, srcChunkRect.height, iBand, iChunk, bytes);
                        memImage.setMemCell(memCell);
                    }
                }
            }
        }
        long time3 = new Date().getTime();
        System.out.println("影像" + fileName + "，ReadToMemImage耗费" + (time3 - time1) + "毫秒");
        return memImage;
    }


    /**
     * 内存中的大图像写入磁盘
     *
     * @param memImage
     * @param fileName
     */
    public static void writeToDisk(MemImage memImage, String fileName) {
        long time1 = new Date().getTime();
        Dataset dataset = GdalIOUtil.preSetImage(fileName, GdalIOUtil.GTiff, memImage.getnWidth(), memImage.getnHeight(), memImage.getnBand(), memImage.getDataType());
        writeToDisk(memImage, dataset);
        if (memImage.getUtilImageInfo() != null) {
            dataset.SetProjection(memImage.getUtilImageInfo().getProj());
            dataset.SetGeoTransform(memImage.getUtilImageInfo().getImgTransform());
        }
        dataset.delete();
        long time3 = new Date().getTime();
        System.out.println("影像" + fileName + "，WriteToDisk耗费" + (time3 - time1) + "毫秒");
    }


    /**
     * 内存中的大图像写入磁盘
     *
     * @param memImage
     * @param dataset
     */
    public static void writeToDisk(MemImage memImage, Dataset dataset) {
        long time1 = new Date().getTime();

        for (int iBand = 0; iBand < memImage.getnBand(); iBand++) {
            int iMemBand = memImage.getMemBandIndex(iBand);
            Band writeBand = dataset.GetRasterBand(iBand + 1);
            int yOffSize = 0;
            for (int iChunk = 0; iChunk < memImage.getnChunkCount(); iChunk++) {
                MemCell memCell = memImage.getMemCell(iMemBand, iChunk);
                if (ProgressHelper.isPrintDetailedLog) {
                    long time2 = new Date().getTime();
                    System.out.println("当前写到的波段号：" + iBand + "，分块号：" + iChunk + "，总块数：" + memImage.getnChunkCount() + ", 已耗时" + (time2 - time1) + "毫秒");
                }

                if (memImage.getDataType() == 1) {
                    writeBand.WriteRaster(0, yOffSize, memCell.getnWidth(), memCell.getnHeight(), memCell.getByteBufferArray());
                } else {
                    writeBand.WriteRaster(0, yOffSize, memCell.getnWidth(), memCell.getnHeight(), memCell.getShortBufferArray());
                }

                yOffSize += memCell.getnHeight();
            }
        }
    }


    /**
     * 内存中的大图像写入磁盘
     * 非全部输出，只输出rect指定的范围
     *
     * @param memImage
     * @param dataset
     * @param rect
     */
    public static void writeToDisk(MemImage memImage, Dataset dataset, Rectangle rect) {
        long time1 = new Date().getTime();

        for (int iBand = 0; iBand < memImage.getnBand(); iBand++) {
            int iMemBand = memImage.getMemBandIndex(iBand);
            Band writeBand = dataset.GetRasterBand(iBand + 1);
            int yOffSizeRead = 0;
            int yOffSizeWrite = 0;
            for (int iChunk = 0; iChunk < memImage.getnChunkCount(); iChunk++) {
                MemCell memCell = memImage.getMemCell(iMemBand, iChunk);
                if (ProgressHelper.isPrintDetailedLog) {
                    long time2 = new Date().getTime();
                    System.out.println("当前写到的波段号：" + iBand + "，分块号：" + iChunk + "，总块数：" + memImage.getnChunkCount() + ", 已耗时" + (time2 - time1) + "毫秒");
                }

                // memCell区域
                Rectangle rectangle = new Rectangle(0, yOffSizeRead, memCell.getnWidth(), memCell.getnHeight());
                // 实际需要读取的memImage图像区域
                Rectangle realityRect = rectangle.intersection(rect);

                if (memImage.getDataType() == 1) {
                    byte[] buffer = memImage.getMemByteBuffer(iBand, realityRect);
                    writeBand.WriteRaster(0, yOffSizeWrite, realityRect.width, realityRect.height, buffer);
                } else {
                    short[] buffer = memImage.getMemShortBuffer(iBand, realityRect);
                    writeBand.WriteRaster(0, yOffSizeWrite, realityRect.width, realityRect.height, buffer);
                }

                yOffSizeRead += memCell.getnHeight();
                yOffSizeWrite += realityRect.height;
            }
        }
    }

    /**
     * 创建一个新的内存图像
     *
     * @param nWidth
     * @param nHeight
     * @param nBand
     * @param isOnlyOneMemBand
     * @param level
     * @param dataType
     * @return
     */
    public static MemImage createNewMemImage(int nWidth, int nHeight, int nBand, Boolean isOnlyOneMemBand, int level, int dataType) {
        long time1 = new Date().getTime();
        // 分块的行数（高度）
        int nPerChunk = ChunkHelper.getPerChunkHeight(nWidth, nHeight, dataType);
        // 分块数
        int nChunkCount = 1;
        // 最后一块的大小
        int nLastChunk = nHeight;
        // 如果需要读取的图像行数太多，则需要进行分块读取
        if (nHeight > nPerChunk) {
            nChunkCount = nHeight / nPerChunk;
            nLastChunk = nPerChunk + nHeight % nPerChunk;
        }
        MemImage memImage = new MemImage(nWidth, nHeight, nBand, isOnlyOneMemBand, nChunkCount, nPerChunk, level, dataType);

        Size size = new Size();

        for (int iBand = 0; iBand < nBand; iBand++) {
            // 实际需要创建的波段数
            if (iBand == 0 || !isOnlyOneMemBand) {
                for (int iChunk = 0; iChunk < nChunkCount; iChunk++) {
                    // 分块读
                    int yPerSize = nPerChunk;
                    if (ProgressHelper.isPrintDetailedLog) {
                        long time2 = new Date().getTime();
                        System.out.println("当前创建到的行号：" + iChunk * nPerChunk + ",波段号：" + iBand + "，分块号：" + iChunk + "，总块数：" + nChunkCount + ",已耗时" + (time2 - time1) + "毫秒");
                    }
                    if (nChunkCount == 1 || iChunk == (nChunkCount - 1))  // 只有一块 或者 最后一块
                    {
                        yPerSize = nLastChunk;
                    }
                    size.width = nWidth;
                    size.height = yPerSize;
                    if (dataType != 1) {
                        short[] shorts = new short[size.width * size.height];
                        MemCell memCell = new MemCell(size.width, size.height, iBand, iChunk, shorts);
                        memImage.setMemCell(memCell);
                    } else {
                        byte[] bytes = new byte[size.width * size.height];
                        MemCell memCell = new MemCell(size.width, size.height, iBand, iChunk, bytes);
                        memImage.setMemCell(memCell);
                    }
                }
            }
        }

        return memImage;
    }


    /**
     * 内存图像多个内存块合并成一个内存块
     *
     * @param srcMemImage
     * @return
     */
    public static MemImage merge(MemImage srcMemImage) {
        long time1 = new Date().getTime();

        MemImage dstMemImage = new MemImage(srcMemImage.getnWidth(), srcMemImage.getnHeight(), srcMemImage.getnBand(), srcMemImage.getOnlyOneMemBand(), 1, srcMemImage.getnHeight(), srcMemImage.getLevel(), srcMemImage.getDataType());

        if (srcMemImage.getDataType() == 1) {
            for (int iBand = 0; iBand < srcMemImage.getnBand(); iBand++) {
                // 只读实际需要读取的波段
                if (iBand == 0 || !srcMemImage.getOnlyOneMemBand()) {
                    byte[] bytes = new byte[dstMemImage.getnWidth() * dstMemImage.getnHeight()];
                    int nDstIndex = 0;
                    for (int iChunk = 0; iChunk < srcMemImage.getnChunkCount(); iChunk++) {
                        if (ProgressHelper.isPrintDetailedLog) {
                            long time2 = new Date().getTime();
                            System.out.println("当前合并到的波段号：" + iBand + "，分块号：" + iChunk + "，总块数：" + srcMemImage.getnChunkCount() + ", 已耗时" + (time2 - time1) + "毫秒");
                        }
                        MemCell srcMemCell = srcMemImage.getMemCell(iBand, iChunk);
                        System.arraycopy(srcMemCell.getByteBufferArray(), 0, bytes, nDstIndex, srcMemCell.getnWidth() * srcMemCell.getnHeight());
                        nDstIndex += srcMemCell.getnWidth() * srcMemCell.getnHeight();
                    }
                    MemCell dstMemCell = new MemCell(dstMemImage.getnWidth(), dstMemImage.getnHeight(), iBand, 0, bytes);
                    dstMemImage.setMemCell(dstMemCell);
                }
            }
        } else {
            for (int iBand = 0; iBand < srcMemImage.getnBand(); iBand++) {
                // 只读实际需要读取的波段
                if (iBand == 0 || !srcMemImage.getOnlyOneMemBand()) {
                    short[] shorts = new short[dstMemImage.getnWidth() * dstMemImage.getnHeight()];
                    int nDstIndex = 0;
                    for (int iChunk = 0; iChunk < srcMemImage.getnChunkCount(); iChunk++) {
                        long time2 = new Date().getTime();
                        System.out.println("当前合并到的波段号：" + iBand + "，分块号：" + iChunk + ",已耗时" + (time2 - time1) + "毫秒");
                        MemCell srcMemCell = srcMemImage.getMemCell(iBand, iChunk);
                        System.arraycopy(srcMemCell.getShortBufferArray(), 0, shorts, nDstIndex, srcMemCell.getnWidth() * srcMemCell.getnHeight());
                        nDstIndex += srcMemCell.getnWidth() * srcMemCell.getnHeight();
                    }
                    MemCell dstMemCell = new MemCell(dstMemImage.getnWidth(), dstMemImage.getnHeight(), iBand, 0, shorts);
                    dstMemImage.setMemCell(dstMemCell);
                }
            }
        }


        long time3 = new Date().getTime();
        System.out.println("内存图像多个内存块合并成一个内存块耗费" + (time3 - time1) + "毫秒");
        return dstMemImage;
    }


    /**
     * 重采样,长宽缩放比相同的采样
     *
     * @param srcShorts
     * @param srcSize
     * @param dstSize
     * @param ratio        采样倍率，使用原始由浮点数计算的倍率，比之后由srcSize和dstSize重新计算的倍数要精确一些
     * @param resampleType 采样方式，1：最近邻；2：双线性
     */
    public static short[] resample(short[] srcShorts, Size srcSize, Size dstSize, double ratio, int resampleType) {
        short[] dstBuffer = new short[dstSize.width * dstSize.height];
        if (resampleType == 1) {
            // 最近邻
            for (int i = 0; i < dstSize.height; i++) {
                for (int j = 0; j < dstSize.width; j++) {
//                    int srcY = (int) (i / ratio + 0.5);
//                    int srcX = (int) (j / ratio + 0.5);
                    int srcY = (int) (i / ratio);
                    int srcX = (int) (j / ratio);
                    if ((srcX > srcSize.width - 1) || (srcY > srcSize.height - 1)) {
                        dstBuffer[i * dstSize.width + j] = 0;
                    } else {
                        dstBuffer[i * dstSize.width + j] = srcShorts[srcY * srcSize.width + srcX];
                    }
                }
            }
        } else if (resampleType == 2) {
            // 双线性
            for (int i = 0; i < dstSize.height; i++) {
                for (int j = 0; j < dstSize.width; j++) {
                    double srcY = i / ratio;
                    double srcX = j / ratio;
                    dstBuffer[i * dstSize.width + j] = ResampleBilinear(srcX, srcY, srcShorts, srcSize.width, srcSize.height);
                }
            }
        }

        return dstBuffer;
    }


    /**
     * 重采样，长宽缩放比相同的采样
     *
     * @param srcBytes
     * @param srcSize
     * @param dstSize
     * @param ratio        采样倍率，使用原始由浮点数计算的倍率，比之后由srcSize和dstSize重新计算的倍数要精确一些
     * @param resampleType 采样方式，1：最近邻；2：双线性
     */
    public static byte[] resample(byte[] srcBytes, Size srcSize, Size dstSize, double ratio, int resampleType) {
        byte[] dstBuffer = new byte[dstSize.width * dstSize.height];
        if (resampleType == 1) {
            // 最近邻
            for (int i = 0; i < dstSize.height; i++) {
                for (int j = 0; j < dstSize.width; j++) {
                    int srcY = (int) (i / ratio + 0.5);
                    int srcX = (int) (j / ratio + 0.5);
//                    int srcY = (int) (i / ratio);
//                    int srcX = (int) (j / ratio);
                    if ((srcX > srcSize.width - 1) || (srcY > srcSize.height - 1)) {
                        dstBuffer[i * dstSize.width + j] = 0;
                    } else {
                        dstBuffer[i * dstSize.width + j] = srcBytes[srcY * srcSize.width + srcX];
                    }
                }
            }
        } else if (resampleType == 2) {
            // 双线性
            for (int i = 0; i < dstSize.height; i++) {
                for (int j = 0; j < dstSize.width; j++) {
                    double srcY = i / ratio;
                    double srcX = j / ratio;
                    dstBuffer[i * dstSize.width + j] = ResampleBilinear(srcX, srcY, srcBytes, srcSize.width, srcSize.height);
                }
            }
        }

        return dstBuffer;
    }


    /**
     * 双线性法重采样
     *
     * @param X       输入的点的x坐标
     * @param Y       输入的点的y坐标
     * @param pBuffer 图像内存
     * @param nWidth  图像的宽(用来读取数据，同时防止出界)
     * @param nHeight 图像的高(用来读取数据，同时防止出界)
     * @return
     */
    public static byte ResampleBilinear(double X, double Y,
                                        byte[] pBuffer,
                                        int nWidth, int nHeight) {
        byte GrayLevel = 0;// (X,Y)灰度值
        double InputX, InputY; // 接收输入的(X,Y)
        InputX = X;
        InputY = Y;

        // 获得(X,Y)的相邻点
        int x1, y1, x2, y2;
        x1 = (int) InputX;
        y1 = (int) InputY;
        x2 = x1 + 1;
        y2 = y1 + 1;

        if (x1 < 0 || y1 < 0) {
            GrayLevel = 0;
            return GrayLevel;
        }

        if (x2 > nWidth - 1 || y2 > nHeight - 1) {
            GrayLevel = 0;
            return GrayLevel;
        }

        double DeltaX, DeltaX1;
        double DeltaY, DeltaY1;
        DeltaX = (InputX - x1);
        DeltaY = (InputY - y1);
        DeltaX1 = (1 - DeltaX);
        DeltaY1 = (1 - DeltaY);

        // 计算(X,Y)的灰度值
        double GrayTemp;
        GrayTemp = (double) (pBuffer[y1 * nWidth + x1]) * DeltaX1 * DeltaY1
                + (double) pBuffer[y1 * nWidth + x2] * DeltaX * DeltaY1
                + (double) pBuffer[y2 * nWidth + x1] * DeltaX1 * DeltaY
                + (double) pBuffer[y2 * nWidth + x2] * DeltaX * DeltaY;
        GrayLevel = (byte) (GrayTemp + 0.5);

        return GrayLevel; // 返回值
    }


    /**
     * 双线性法重采样
     *
     * @param X       输入的点的x坐标
     * @param Y       输入的点的y坐标
     * @param pBuffer 图像内存
     * @param nWidth  图像的宽(用来读取数据，同时防止出界)
     * @param nHeight 图像的高(用来读取数据，同时防止出界)
     * @return
     */
    public static short ResampleBilinear(double X, double Y,
                                         short[] pBuffer,
                                         int nWidth, int nHeight) {
        short GrayLevel = 0;// (X,Y)灰度值
        double InputX, InputY; // 接收输入的(X,Y)
        InputX = X;
        InputY = Y;

        // 获得(X,Y)的相邻点
        int x1, y1, x2, y2;
        x1 = (int) InputX;
        y1 = (int) InputY;
        x2 = x1 + 1;
        y2 = y1 + 1;

        if (x1 < 0 || y1 < 0) {
            GrayLevel = 0;
            return GrayLevel;
        }

        if (x2 > nWidth - 1 || y2 > nHeight - 1) {
            GrayLevel = 0;
            return GrayLevel;
        }

        double DeltaX, DeltaX1;
        double DeltaY, DeltaY1;
        DeltaX = (InputX - x1);
        DeltaY = (InputY - y1);
        DeltaX1 = (1 - DeltaX);
        DeltaY1 = (1 - DeltaY);

        // 计算(X,Y)的灰度值
        double GrayTemp;
        GrayTemp = (double) (pBuffer[y1 * nWidth + x1]) * DeltaX1 * DeltaY1
                + (double) pBuffer[y1 * nWidth + x2] * DeltaX * DeltaY1
                + (double) pBuffer[y2 * nWidth + x1] * DeltaX1 * DeltaY
                + (double) pBuffer[y2 * nWidth + x2] * DeltaX * DeltaY;
        GrayLevel = (byte) (GrayTemp + 0.5);

        return GrayLevel; // 返回值
    }

    /**
     * NDVI计算
     */
    private static void calNDVI(String fileName) {
        MemImage memImage = readToMemImage(fileName);
        MemImage memImage1 = new MemImage(memImage.getnWidth(), memImage.getnHeight(), 1, true, memImage.getnChunkCount(), memImage.getnPerChunk(), 1, memImage.getDataType());

        long time1 = new Date().getTime();
        for (int iChunk = 0; iChunk < memImage.getnChunkCount(); iChunk++) {
            MemCell srcMemCell0 = memImage.getMemCell(0, iChunk);
            MemCell srcMemCell1 = memImage.getMemCell(1, iChunk);

            short[] buffer0 = srcMemCell0.getShortBufferArray();
            short[] buffer1 = srcMemCell1.getShortBufferArray();

            short[] bytesW = new short[srcMemCell0.getnWidth() * srcMemCell0.getnHeight()];

            for (int iHeight = 0; iHeight < srcMemCell1.getnHeight(); iHeight++) {
                for (int jWidth = 0; jWidth < srcMemCell1.getnWidth(); jWidth++) {
                    if ((buffer0[iHeight * srcMemCell1.getnWidth() + jWidth] - buffer1[iHeight * srcMemCell1.getnWidth() + jWidth]) != 0) {
                        bytesW[iHeight * srcMemCell1.getnWidth() + jWidth] = (short) (0 + (buffer0[iHeight * srcMemCell1.getnWidth() + jWidth] + buffer1[iHeight * srcMemCell1.getnWidth() + jWidth]) / (buffer0[iHeight * srcMemCell1.getnWidth() + jWidth] - buffer1[iHeight * srcMemCell1.getnWidth() + jWidth]));
                    }
                }
            }

            MemCell srcMemCellW = new MemCell(srcMemCell0.getnWidth(), srcMemCell0.getnHeight(), 0, iChunk, bytesW);
            memImage1.setMemCell(srcMemCellW);
            memImage1.setUtilImageInfo(memImage.getUtilImageInfo());
        }

        long time2 = new Date().getTime();
        System.out.println("处理耗费" + (time2 - time1) + "毫秒");
        writeToDisk(memImage1, "D:\\workspace\\testdata\\google_rw_ndvi.tif");
    }


    /**
     * 计算两个向量逐元素乘积，并把结果向量求和返回
     *
     * @param Matrix1 向量1
     * @param Matrix2 向量2
     * @param size    向量的元素个数
     * @return
     */
    private static double computerMatrix(double[] Matrix1, double[] Matrix2, int size) {
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += Matrix1[i] * Matrix2[i];
        }
        return sum;
    }
}
