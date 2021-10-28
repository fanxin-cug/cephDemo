package com.zhanghao.ceph.Utils.geo.tile.mem;


import com.zhanghao.ceph.Utils.geo.UtilImageInfo;
import com.zhanghao.ceph.Utils.geo.tile.core.ProgressHelper;

import java.awt.*;
import java.util.Date;

/**
 * Created by Lenovo on 2021/8/27.
 */
public class MemImageCutHelper {

    /**
     * 内存图像按2的倍率采样，最大支持2的8次方，最小支持2的8次方之一
     * 新生成的内存图像的其分块大小不改变
     * 该函数与resampleInitialLevel函数相比，获取内存中图像数据的方式不一样，是为了速度
     *
     * @param srcMemImage
     * @param ratio          采样倍率，必须为2的倍数(...1/8,1/4,1/2,2,4,8...)
     * @param resampleType   采样方式，1：最近邻；2：双线性
     * @param levelDeviation 层级偏差，为正数或者负数。每放大一倍，层级偏差加1，每减小1倍，层级偏差减1
     * @return
     */
    public static MemImage resampleBy2Times(MemImage srcMemImage, double ratio, int resampleType, int levelDeviation) {
        long time1 = new Date().getTime();
        int nImageWidth = (int) (0.5 + srcMemImage.getnWidth() * ratio);
        int nImageHeight = (int) (0.5 + srcMemImage.getnHeight() * ratio);
        System.out.println("内存图像按2的倍率采样，图像宽：" + nImageWidth + "，图像高：" + nImageHeight);

        // 重新计算新图像的分块参数
        int nPerChunk = (int) (srcMemImage.getnPerChunk() * ratio);
        int nChunkCount = srcMemImage.getnChunkCount();
        int nLastChunk = nImageHeight;
        if (nImageHeight > nPerChunk) {
            nLastChunk = nPerChunk + nImageHeight % nPerChunk;
        }
        int level = srcMemImage.getLevel() + levelDeviation;
        MemImage dstMemImage = new MemImage(nImageWidth, nImageHeight, srcMemImage.getnBand(), srcMemImage.getOnlyOneMemBand(), nChunkCount, nPerChunk, level, srcMemImage.getDataType());
        for (int iBand = 0; iBand < dstMemImage.getnBand(); iBand++) {
            // 只读实际需要读取的波段
            if (iBand == 0 || !srcMemImage.getOnlyOneMemBand()) {
                for (int iChunk = 0; iChunk < nChunkCount; iChunk++) {
                    if (ProgressHelper.isPrintDetailedLog) {
                        long time2 = new Date().getTime();
                        System.out.println("内存图像按2的倍率采样，当前采样到的波段号：" + iBand + "，分块号：" + iChunk + "，总块数：" + nChunkCount + ", 已耗时" + (time2 - time1) + "毫秒");
                    }

                    MemCell srcMemCell = srcMemImage.getMemCell(iBand, iChunk);
                    int nChunkHeight = nPerChunk;
                    if (nChunkCount == 1 || (iChunk == nChunkCount - 1)) {
                        // 只有一块或者当前为最后一块
                        nChunkHeight = nLastChunk;
                    }
                    int nChunkWidth = nImageWidth;

                    if (srcMemImage.getDataType() == 1) {
                        byte[] bytes = MemImageHelper.resample(srcMemCell.getByteBufferArray(), new Size(srcMemCell.getnWidth(), srcMemCell.getnHeight()), new Size(nChunkWidth, nChunkHeight), ratio, resampleType);
                        MemCell dstMemCell = new MemCell(nChunkWidth, nChunkHeight, iBand, iChunk, bytes);
                        dstMemImage.setMemCell(dstMemCell);
                    } else {
                        short[] shorts = MemImageHelper.resample(srcMemCell.getShortBufferArray(), new Size(srcMemCell.getnWidth(), srcMemCell.getnHeight()), new Size(nChunkWidth, nChunkHeight), ratio, resampleType);
                        MemCell dstMemCell = new MemCell(nChunkWidth, nChunkHeight, iBand, iChunk, shorts);
                        dstMemImage.setMemCell(dstMemCell);
                    }
                }
            }
        }

        dstMemImage.setUtilImageInfo(new UtilImageInfo(srcMemImage.getUtilImageInfo(), ratio));

//        dstMemImage.setUtilImageInfo(new UtilImageInfo(srcMemImage.getUtilImageInfo(), ratio, Lookup4326.Epsg4326DegreeList.get(srcMemImage.getLevel())));
        long time2 = new Date().getTime();
        System.out.println("内存图像按2的倍率采样耗费" + (time2 - time1) + "毫秒");
        return dstMemImage;
    }


    /**
     * 内存图像任意倍率采样（用于切片时的第一次采样）
     *
     * @param srcMemImage
     * @param ratio
     * @param resampleType 采样方式，1：最近邻；2：双线性
     * @return
     */
    public static MemImage resampleInitialLevel(MemImage srcMemImage, double ratio, int resampleType) {
        long time1 = new Date().getTime();

        int nImageWidth = (int) (srcMemImage.getnWidth() * ratio);
        int nImageHeight = (int) (srcMemImage.getnHeight() * ratio);
        System.out.println("图像任意倍率采样，图像宽：" + nImageWidth + "，图像高：" + nImageHeight);

        // 重新计算新图像的分块参数
        int nPerChunk = ChunkHelper.getPerChunkHeight(nImageWidth, nImageHeight, srcMemImage.getDataType());
        int nChunkCount = 1;
        int nLastChunk = nImageHeight;
        if (nImageHeight > nPerChunk) {
            nChunkCount = nImageHeight / nPerChunk;
            nLastChunk = nPerChunk + nImageHeight % nPerChunk;
        }
        // 初始采样时，继续使用源图像的最大剖分层级
        int level = srcMemImage.getLevel();
        MemImage dstMemImage = new MemImage(nImageWidth, nImageHeight, srcMemImage.getnBand(), srcMemImage.getOnlyOneMemBand(), nChunkCount, nPerChunk, level, srcMemImage.getDataType());
        for (int iBand = 0; iBand < dstMemImage.getnBand(); iBand++) {
            // 只读实际需要读取的波段
            if (iBand == 0 || !srcMemImage.getOnlyOneMemBand()) {
                // 以结果图像为导向
                for (int iChunk = 0; iChunk < nChunkCount; iChunk++) {
                    if (ProgressHelper.isPrintDetailedLog) {
                        long time2 = new Date().getTime();
                        System.out.println("任意倍率采样，当前采样到的波段号：" + iBand + "，分块号：" + iChunk + "，总块数：" + nChunkCount + ", 已耗时" + (time2 - time1) + "毫秒");
                    }

                    // 计算源图像大小，结果图像大小
                    int nChunkHeight = nPerChunk;
                    if (nChunkCount == 1 || iChunk == (nChunkCount - 1))  // 只有一块或者当前为最后一块
                    {
                        nChunkHeight = nLastChunk;
                    }
                    int nChunkWidth = nImageWidth;

                    Size dstSize = new Size(nChunkWidth, nChunkHeight);
                    int srcChunkHeight = (int) (nChunkHeight / ratio);
                    int srcChunkHeightBegin = (int) (iChunk * nPerChunk / ratio);
                    Rectangle srcRectangle = new Rectangle(0, srcChunkHeightBegin, srcMemImage.getnWidth(), srcChunkHeight);

                    if (srcMemImage.getDataType() == 1) {
                        byte[] dstBytes = srcMemImage.getMemByteBuffer(iBand, srcRectangle, dstSize, ratio, resampleType);
                        MemCell dstMemCell = new MemCell(nChunkWidth, nChunkHeight, iBand, iChunk, dstBytes);
                        dstMemImage.setMemCell(dstMemCell);
                    } else {
                        short[] dstShorts = srcMemImage.getMemShortBuffer(iBand, srcRectangle, dstSize, ratio, resampleType);
                        MemCell dstMemCell = new MemCell(nChunkWidth, nChunkHeight, iBand, iChunk, dstShorts);
                        dstMemImage.setMemCell(dstMemCell);
                    }
                }
            }
        }

        dstMemImage.setUtilImageInfo(new UtilImageInfo(srcMemImage.getUtilImageInfo(), ratio));
        long time3 = new Date().getTime();
        System.out.println("内存图像任意倍率采样耗费" + (time3 - time1) + "毫秒");
        return dstMemImage;
    }


}
