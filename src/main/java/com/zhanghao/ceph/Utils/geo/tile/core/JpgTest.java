package com.zhanghao.ceph.Utils.geo.tile.core;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;

public class JpgTest {

    public static void main(String[] args) throws Exception {
        String fileName = "D:\\tmpdata\\过亮图像\\re200_12_3355_553.jpg";
        fileName = "D:\\tmpdata\\过亮图像\\re200_12_3355_553_gray.jpg";
        BufferedImage bufferedImage = ImageIO.read(new File(fileName));

        String fileName2 = "D:\\tmpdata\\过亮图像\\re200_12_3355_553_a2_type_RGB4.png";
        {
            fileName2 = "D:\\tmpdata\\过亮图像\\hello001_gray.png";
            BufferedImage argb = transToPNG(bufferedImage);
//            saveByNoCompress(argb, fileName2, "jpg");
            ImageIO.write(argb, "png", new File(fileName2));
            System.out.println(bufferedImage.getType());
            System.out.println(argb.getType());
        }

//        {
//            BufferedImage argb = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
//            System.out.println(bufferedImage.getType());
//            System.out.println(argb.getType());
//            Graphics2D g = argb.createGraphics();
//            g.getDeviceConfiguration().createCompatibleImage(256, 256, Transparency.TRANSLUCENT);
//            g.drawImage(bufferedImage, 0, 0, null);
//            g.dispose();
//            ImageIO.write(argb, "png", new File(fileName2));
//            BufferedImage bufferedImage2 = ImageIO.read(new File(fileName2));
//            System.out.println(bufferedImage2.getType());
//        }

//        BufferedImage bufferedImageRe =   DownloadCoarseLevelImage.resizeImage(bufferedImage.getSubimage(0, 0, 128, 128), 256, 256);
//        ImageIO.write(bufferedImageRe, "jpg", new File(fileName2));

//        int[] bufferI2 = new int[TileConsts.tilesize * TileConsts.tilesize * 3];
//        int[] bufferI = bufferedImage.getData().getPixels(0, 0, TileConsts.tilesize, TileConsts.tilesize, bufferI2);
//        byte[] buffer1 = new byte[TileConsts.tilesize * TileConsts.tilesize];
//        for (int inner = 0; inner < TileConsts.tilesize * TileConsts.tilesize; inner++) {
//            buffer1[inner] = (byte) bufferI[inner * 3];
//        }
//        BufferedImage bufferedImage1 = createGrayImage(buffer1, TileConsts.tilesize, TileConsts.tilesize);
//        ImageIO.write(bufferedImage1, "jpg", new File(fileName2));
    }


    /**
     * JPG压缩试验
     */
    public static void compressImage() throws Exception {
        String fileName = "D:\\tmpdata\\过亮图像\\re404_12_3355_553.jpg";
        String fileName2 = "D:\\tmpdata\\过亮图像\\输出5.bmp";
        {
            // 一种方式：保存为bmp，默认不压缩
            BufferedImage buffer = ImageIO.read(new File(fileName));
            ImageIO.write(buffer, "bmp", new File(fileName2));
        }

        {
            // 另一种方式：保存为jpg，需要设置压缩质量
            BufferedImage buffer = ImageIO.read(new File(fileName));
            JPEGImageWriteParam jpegImageWriteParam = new JPEGImageWriteParam(null);
            jpegImageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegImageWriteParam.setCompressionQuality(0f);  // 无损压缩
            final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(new FileImageOutputStream(new File(fileName2)));
            writer.write(null, new IIOImage(buffer, null, null), jpegImageWriteParam);
        }
    }


    /**
     * 无损压缩保存jpg文件
     *
     * @param buffer
     * @param fileName
     */
    public static void saveNoCompressJPG(BufferedImage buffer, String fileName) throws Exception {
        JPEGImageWriteParam jpegImageWriteParam = new JPEGImageWriteParam(null);
        jpegImageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegImageWriteParam.setCompressionQuality(0f);  // 无损压缩
        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        writer.setOutput(new FileImageOutputStream(new File(fileName)));
        writer.write(null, new IIOImage(buffer, null, null), jpegImageWriteParam);
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


    public static BufferedImage toARGB(BufferedImage bufferedImage) {
        try {
            BufferedImage newImg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
            newImg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_BGR);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(bufferedImage, newImg);
            return newImg;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static BufferedImage transToPNG(BufferedImage bufferedImage) {
        BufferedImage result = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = result.createGraphics();
        result = graphics2D.getDeviceConfiguration().createCompatibleImage(bufferedImage.getWidth(), bufferedImage.getHeight(), Transparency.TRANSLUCENT);
        int alpha = 0;
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                int rgb = bufferedImage.getRGB(i, j);
                if (rgb == -1 * 256 * 256 * 256) {
                    System.out.println(rgb);
                    rgb = ((alpha * 256 / 10) << 24) | (rgb & 0x00ffffff);
//                    System.out.println(rgb);
                }else {
                    System.out.println(rgb);
                }
                result.setRGB(i, j, rgb);
            }
        }
//          graphics2D.drawImage(bufferedImage, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, 0, null);
//        graphics2D.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        return result;
    }
}
