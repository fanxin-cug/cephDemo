package com.zhanghao.ceph.Utils.geo.tile.core;


import org.apache.log4j.Logger;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2021/4/26.
 */
public class BufferedImageHelper<T> {
    private static final Logger log = Logger.getLogger(BufferedImageHelper.class);


    public static BufferedImage createImage(List<byte[]> bytes, int bandNum, int width, int height) {
        if (bandNum == 1) {
            return createGrayImage(bytes.get(0), width, height);
        } else if (bandNum == 3) {
            return createRGBImage(bytes.get(0), bytes.get(1), bytes.get(2), width, height);
        }
        return null;
    }

    /**
     * 根据指定的参数创建一个RGB格式的BufferedImage
     *
     * @param arrayR
     * @param width  图像宽度
     * @param height 图像高度
     * @return
     */
    public static BufferedImage createRGBImage(byte[] arrayR, byte[] arrayG, byte[] arrayB, int width, int height) {
        try {
            byte[] bufWrite = new byte[width * height * 3];
            for (int i = 0; i < width * height; i++) {
                bufWrite[i * 3] = arrayR[i];
                bufWrite[i * 3 + 1] = arrayG[i];
                bufWrite[i * 3 + 2] = arrayB[i];
            }
//            if (!ArrayHelper.isZeroBuffer(bufWrite)) {
            return BufferedImageHelper.createRGBImage(bufWrite, width, height);
//            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return null;
    }


    /**
     * 根据指定的参数创建一个RGB格式的BufferedImage，考虑合并数据的情况
     *
     * @param arrayR
     * @param arrayG
     * @param arrayB
     * @param width   图像宽度
     * @param height  图像高度
     * @param bufferI 待合并的图像数据
     * @return
     */
    public static BufferedImage createRGBImage(byte[] arrayR, byte[] arrayG, byte[] arrayB, int width, int height, int[] bufferI) {
        try {
            byte[] bufWrite = new byte[width * height * 3];
            for (int i = 0; i < width * height; i++) {
                if (arrayR[i] == 0 && arrayG[i] == 0 && arrayB[i] == 0) {
                    bufWrite[i * 3] = (byte) bufferI[i * 3];
                    bufWrite[i * 3 + 1] = (byte) bufferI[i * 3 + 1];
                    bufWrite[i * 3 + 2] = (byte) bufferI[i * 3 + 2];
                } else {
                    bufWrite[i * 3] = arrayR[i];
                    bufWrite[i * 3 + 1] = arrayG[i];
                    bufWrite[i * 3 + 2] = arrayB[i];
                }
            }
            return BufferedImageHelper.createRGBImage(bufWrite, width, height);
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return null;
    }

    /**
     * 根据指定的参数创建一个RGB格式的BufferedImage
     *
     * @param arrayR
     * @param width  图像宽度
     * @param height 图像高度
     * @return
     */
    public static BufferedImage createRGBImage(short[] arrayR, short[] arrayG, short[] arrayB, int width, int height) {
        try {
            byte[] bufWrite = new byte[width * height * 3];
            for (int i = 0; i < width * height; i++) {
                bufWrite[i * 3] = (byte) arrayR[i];
                bufWrite[i * 3 + 1] = (byte) arrayG[i];
                bufWrite[i * 3 + 2] = (byte) arrayB[i];
            }
//            if (!ArrayHelper.isZeroBuffer(bufWrite)) {
            return BufferedImageHelper.createRGBImage(bufWrite, width, height);
//            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return null;
    }

    /**
     * 根据指定的参数创建一个RGB格式的BufferedImage
     * 数据集模式（RGB、RGB......)
     *
     * @param matrixRGB RGB格式的图像矩阵
     * @param width     图像宽度
     * @param height    图像高度
     * @return
     */
    public static BufferedImage createRGBImage(byte[] matrixRGB, int width, int height) {
        try {
            // 检测参数合法性
            if (null == matrixRGB || matrixRGB.length != width * height * 3)
                throw new IllegalArgumentException("invalid image description");
            // 将byte[]转为DataBufferByte用于后续创建BufferedImage对象
            DataBufferByte dataBuffer = new DataBufferByte(matrixRGB, matrixRGB.length);
            // sRGB色彩空间对象
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            int[] nBits = {8, 8, 8};
            int[] bOffs = {0, 1, 2};
            ComponentColorModel colorModel = new ComponentColorModel(cs, nBits, false, false,
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE);
            WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, width, height, width * 3, 3, bOffs, null);
            BufferedImage newImg = new BufferedImage(colorModel, raster, false, null);

            return newImg;
        } catch (Exception ex) {
            log.error(ex, ex);
            return null;
        }
    }

    /**
     * 根据指定的参数创建一个灰度图格式的BufferedImage
     *
     * @param matrixGray 灰度图的图像矩阵
     * @param width      图像宽度
     * @param height     图像高度
     * @return
     */
    public static BufferedImage createGrayImage(byte[] matrixGray, int width, int height) {
        try {
            // 检测参数合法性
            if (null == matrixGray || matrixGray.length != width * height)
                throw new IllegalArgumentException("invalid image description");
            // 将byte[]转为DataBufferByte用于后续创建BufferedImage对象
            DataBufferByte dataBuffer = new DataBufferByte(matrixGray, matrixGray.length);
            // sRGB色彩空间对象
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            int[] nBits = {8};
            int[] bOffs = {0};
            ComponentColorModel colorModel = new ComponentColorModel(cs, nBits, false, false,
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE);
            WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, width, height, width, 1, bOffs, null);
            BufferedImage newImg = new BufferedImage(colorModel, raster, false, null);

            return newImg;
        } catch (Exception ex) {

            return null;
        }
    }


    /**
     * 克隆BufferedImage
     *
     * @param src
     * @return
     */
    public static BufferedImage cloneBufferedImage(BufferedImage src) {
        ColorModel cm = src.getColorModel();
        BufferedImage image = new BufferedImage(cm,
                cm.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), cm.isAlphaPremultiplied(), null);
        return image;
    }


    /**
     * 通过文件名获取ImageIO
     *
     * @param fileName
     * @return
     */
    public static BufferedImage getImageIO(String fileName) {
        try {
            BufferedImage bi = ImageIO.read(new File(fileName));
            return bi;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * BufferedImage转流
     *
     * @param image
     * @return
     */
    public static ByteArrayOutputStream transImageToStream(BufferedImage image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, TileConsts.jpgImageFormat, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    /**
     * BufferedImage全部数据（含头文件）转byte[]
     *
     * @param image
     * @return
     */
    public static byte[] transImageToByte(BufferedImage image) {
        if (image != null) {
            ByteArrayOutputStream out = transImageToStream(image);
            byte[] outToByteArray = out.toByteArray();
            return outToByteArray;
        } else {
            return null;
        }
    }


    /**
     * 将瓦片转换为PNG格式
     *
     * @param image
     * @return
     */
    public static BufferedImage transJPGToPNG(BufferedImage image) {

        return null;
    }


    /**
     * 按需求返回瓦片图像内存数据
     *
     * @param bufferedImage 瓦片数据
     * @param bandNum       需要的波段数量
     * @return
     */
    public static List<byte[]> getBufferedImageData(BufferedImage bufferedImage, int bandNum) {
        List<byte[]> arrayList = new ArrayList<>();
        if (bufferedImage != null) {
//            System.out.println(bufferedImage.getType());
            if (bufferedImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {
                int[] bufferI2 = new int[TileConsts.tilesize * TileConsts.tilesize * 3];
                int[] bufferI = bufferedImage.getData().getPixels(0, 0, TileConsts.tilesize, TileConsts.tilesize, bufferI2);
                for (int iBand = 0; iBand < bandNum; iBand++) {
                    byte[] buffer = new byte[TileConsts.tilesize * TileConsts.tilesize];
                    for (int inner = 0; inner < TileConsts.tilesize * TileConsts.tilesize; inner++) {
                        buffer[inner] = (byte) bufferI[inner * 3 + iBand];
                    }
                    arrayList.add(buffer);
                }
            } else if (bufferedImage.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
                int[] bufferI2 = new int[TileConsts.tilesize * TileConsts.tilesize * 4];
                int[] bufferI = bufferedImage.getData().getPixels(0, 0, TileConsts.tilesize, TileConsts.tilesize, bufferI2);
                for (int iBand = 0; iBand < bandNum; iBand++) {
                    byte[] buffer = new byte[TileConsts.tilesize * TileConsts.tilesize];
                    for (int inner = 0; inner < TileConsts.tilesize * TileConsts.tilesize; inner++) {
                        buffer[inner] = (byte) bufferI[inner * 4 + iBand + 1];
                    }
                    arrayList.add(buffer);
                }
            } else if (bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY) {
                int[] bufferI2 = new int[TileConsts.tilesize * TileConsts.tilesize];
                int[] bufferI = bufferedImage.getData().getPixels(0, 0, TileConsts.tilesize, TileConsts.tilesize, bufferI2);
                byte[] buffer = new byte[TileConsts.tilesize * TileConsts.tilesize];
                for (int inner = 0; inner < TileConsts.tilesize * TileConsts.tilesize; inner++) {
                    buffer[inner] = (byte) bufferI[inner];
                }
                for (int iBand = 0; iBand < bandNum; iBand++) {
                    if (iBand > 0) {
                        byte[] bufferTemp = new byte[TileConsts.tilesize * TileConsts.tilesize];
                        System.arraycopy(buffer, 0, bufferTemp, 0, TileConsts.tilesize * TileConsts.tilesize);
                        arrayList.add(bufferTemp);
                    } else {
                        arrayList.add(buffer);
                    }
                }
            }
        }
        return arrayList;
    }

    public static void main(String[] args) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new URL("http://www.pptok.com/wp-content/uploads/2012/08/xunguang-9.jpg"));
            ImageIO.write(bufferedImage, TileConsts.jpgImageFormat, new File("D://dncode.jpg"));
        } catch (Exception ex) {

        }
    }
}
