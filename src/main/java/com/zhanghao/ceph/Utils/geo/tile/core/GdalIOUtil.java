package com.zhanghao.ceph.Utils.geo.tile.core;

import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconstConstants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lenovo on 2019/8/24.
 */
public class GdalIOUtil {

    public final static String GTiff = "GTiff";

    public final static String strProjectionLngLat = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.2572235630016,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433],AUTHORITY[\"EPSG\",\"4326\"]]";

    public final static String strProjectionSinusoidal = "PROJCS[\"MODIS_Sinusoidal\",GEOGCS[\"GCS_Sphere\",DATUM[\"D_Sphere\",SPHEROID[\"Sphere\",6371007.181,0.0]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Sinusoidal\"],PARAMETER[\"False_Easting\",0.0],PARAMETER[\"False_Northing\",0.0],PARAMETER[\"Central_Meridian\",0.0],UNIT[\"Meter\",1.0]]')"; // ???

    public final static String strProjectionWebMercator = "PROJCS[\"WGS 84 / Pseudo-Mercator\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],EXTENSION[\"PROJ4\",\"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs\"],AUTHORITY[\"EPSG\",\"3857\"],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH]]";

    /**
     * 图像重采样
     *
     * @param srcFileName
     * @param dstFileName
     * @param dRatio      采样比，大于1放大，小于1为缩小
     * @return
     */
    public static void resamplingImg(String srcFileName, String dstFileName, double dRatio) {
        //Entry.initEnvir();
        Dataset srcDataset = gdal.Open(srcFileName, gdalconstConstants.GA_ReadOnly);
        if (srcDataset != null) {
            int srcWidth = srcDataset.getRasterXSize();
            int srcHeight = srcDataset.getRasterYSize();
            int srcBands = srcDataset.getRasterCount();
            int srcDType = srcDataset.GetRasterBand(1).GetRasterDataType();
            double srcTransform[] = srcDataset.GetGeoTransform();
            String srcProj = srcDataset.GetProjection();

            //计算输出图像的采样率和高度
            int dstWidth = (int) (srcWidth * dRatio);
            int dstHeight = (int) (srcHeight * dRatio);
            double[] dstTransform = {srcTransform[0], srcTransform[1] / dRatio, srcTransform[2], srcTransform[3], srcTransform[4], srcTransform[5] / dRatio};

            Driver driver = gdal.GetDriverByName(GdalIOUtil.GTiff);
            if (driver != null) {
                Dataset dstDataset = driver.Create(dstFileName, dstWidth, dstHeight, srcBands, srcDType);

                //分波段写数据
                for (int i = 0; i < srcBands; i++) {
                    Band readBand = srcDataset.GetRasterBand(i + 1);
                    Band writeBand = dstDataset.GetRasterBand(i + 1);
                    ColorTable srcColorTable = srcDataset.GetRasterBand(i + 1).GetColorTable();

                    if (srcDType == 1) {   //8位
                        byte[] pBuf = new byte[dstWidth * dstHeight];
                        readBand.ReadRaster(0, 0, srcWidth, srcHeight, dstWidth, dstHeight, srcDType, pBuf);
                        writeBand.WriteRaster(0, 0, dstWidth, dstHeight, pBuf);
                    } else if (srcDType == 2 || srcDType == 3) {  //16位
                        short[] pBuf = new short[dstWidth * dstHeight];
                        readBand.ReadRaster(0, 0, srcWidth, srcHeight, dstWidth, dstHeight, srcDType, pBuf);
                        writeBand.WriteRaster(0, 0, dstWidth, dstHeight, pBuf);
                    }

                    if (srcColorTable != null) {
                        writeBand.SetColorTable(srcColorTable);
                    }
                }
                dstDataset.SetGeoTransform(dstTransform);
                dstDataset.SetProjection(srcProj);
                dstDataset.FlushCache();
                dstDataset.delete();
            }
            srcDataset.delete();
        }
    }


    /**
     * 根据经纬度读取图像像素值（单波段）
     *
     * @param fileName
     * @param PixelLon
     * @param PixelLat
     * @return
     */
    public static Double readPixle(String fileName, Double PixelLon, Double PixelLat) {
        Dataset dataset = gdal.Open(fileName, gdalconstConstants.GA_ReadOnly);
        //判断打开状态
        if (dataset == null) {
            System.err.println("GDALOpen failed - " + gdal.GetLastErrorNo());
            return -9999d;
        }
        //geoTransform[0]是左上角像元的东坐标X+
        //geoTransform[3]是左上角像元的北坐标Y+
        //geoTransform[1]是影像宽度上的分辨率
        //geoTransform[5]是影像高度上的分辨率
        //geoTransform[2]是旋转，0表示上面为北方
        //geoTransform[4]是旋转，0表示上面为北方
        double[] geoTransform = dataset.GetGeoTransform();
        for (int i = 0; i < geoTransform.length; i++) {
            System.out.println(i + ":" + geoTransform[i]);
        }
        double a1, b1, c1, a2, b2, c2, Xp, Yp;
        //a1!=0,b2!=0
        a1 = geoTransform[1];
        b1 = geoTransform[2];
        c1 = PixelLon - geoTransform[0];
        a2 = geoTransform[4];
        b2 = geoTransform[5];
        c2 = PixelLat - geoTransform[3];
        //将经纬度转为图像中的行列号
        Yp = (a2 * c1 - a1 * c2) / (a2 * b1 - a1 * b2);
        Xp = (c1 - b1 * Yp) / a1;
        Xp = Math.round(Xp);
        Yp = Math.round(Yp);
        //得到行列号x,y
        int x = (int) Xp + 1;
        int y = (int) Yp + 1;
        System.out.println(x + "," + y);
        //获取波段信息
        Band band = dataset.GetRasterBand(1);
        //列数
        int xSize = dataset.getRasterXSize();
        double buf1[] = new double[xSize];
        //读取一行数据
        band.ReadRaster(0, y - 1, xSize, 1, buf1);
        System.out.print("line:" + y + ",pixel:" + x + ",buf1[pixel]:" + buf1[x - 1]);
        double dem = buf1[x - 1];
        //释放内存
        dataset.delete();
        return dem;
    }


    /**
     * 预置图像
     *
     * @param fileName
     * @param fileType 图像文件格式：geoiff..........
     * @param width
     * @param height
     * @param nBandss
     * @param dataType 图像数据类型：byte............
     * @return
     */
    public static Dataset preSetImage(String fileName, String fileType, int width, int height, int nBandss, int dataType) {
        Driver driver = gdal.GetDriverByName(fileType);
        if (driver != null) {
            Dataset dataset = driver.Create(fileName, width, height, nBandss, dataType);
            if (dataset != null) {
                return dataset;
            }
        }
        return null;
    }

    /**
     * 读取指定数据集的数据
     *
     * @param srcDataset
     * @param bandList
     * @param xoff
     * @param yoff
     * @param xsize
     * @param ysize
     * @param buf_xsize
     * @param buf_ysize
     * @return
     */
    public static List<byte[]> readBufferToByte(Dataset srcDataset, int[] bandList, int xoff, int yoff, int xsize, int ysize, int buf_xsize, int buf_ysize) {
        List<byte[]> bytes = new ArrayList<>();
        for (int i = 0; bandList != null && i < bandList.length; i++) {
            Band band = srcDataset.GetRasterBand(bandList[i]);
            byte[] bytes1 = null;
            if (band.getDataType() == 1) {
                bytes1 = GdalIOUtil.readBufferByte(band, xoff, yoff, xsize, ysize, buf_xsize, buf_ysize);
            } else {
                short[] shorts = GdalIOUtil.readBufferShort(band, xoff, yoff, xsize, ysize, buf_xsize, buf_ysize);
                bytes1 = transShortToByte(shorts);
            }
            bytes.add(bytes1);
        }
        return bytes;
    }


    /**
     * 读取指定波段的数据
     *
     * @param band
     * @param xoff
     * @param yoff
     * @param xsize
     * @param ysize
     * @param buf_xsize
     * @param buf_ysize
     * @return
     */
    public static byte[] readBufferByte(Band band, int xoff, int yoff, int xsize, int ysize, int buf_xsize, int buf_ysize) {
        byte[] buffer = new byte[buf_xsize * buf_ysize];
        int buf_type = band.getDataType();
        band.ReadRaster(xoff, yoff, xsize, ysize, buf_xsize, buf_ysize, buf_type, buffer);
        return buffer;
    }

    /**
     * 读取指定波段的数据
     *
     * @param band
     * @param xoff
     * @param yoff
     * @param xsize
     * @param ysize
     * @param buf_xsize
     * @param buf_ysize
     * @return
     */
    public static short[] readBufferShort(Band band, int xoff, int yoff, int xsize, int ysize, int buf_xsize, int buf_ysize) {
        short[] buffer = new short[buf_xsize * buf_ysize];
        int buf_type = band.getDataType();
        band.ReadRaster(xoff, yoff, xsize, ysize, buf_xsize, buf_ysize, buf_type, buffer);
        return buffer;
    }


    /**
     * 将short数组转换成byte数组
     *
     * @param buffer
     * @return
     */
    public static byte[] transShortToByte(short[] buffer) {
        byte[] bufferB = null;
        if (buffer != null) {
            short maxD = 10000;
            short minD = 0;
            bufferB = new byte[buffer.length];

//            for (int i = 0; i < buffer.length; i++) {
//                if (buffer[i] > maxD) {
//                    maxD = buffer[i];
//                }
//                if (buffer[i] < minD) {
//                    minD = buffer[i];
//                }
//            }

            for (int i = 0; i < buffer.length; i++) {
                bufferB[i] = (byte) (((double) buffer[i] - minD) / (maxD - minD) * 255);
            }
        }
        return bufferB;
    }


    /**
     * 测试
     *
     * @param fileName
     * @param fileType
     * @param width
     * @param height
     * @param nBandss
     * @param dataType
     * @param buffer
     */
    public static void writeImg(String fileName, String fileType, int width, int height, int nBandss, int dataType, byte[] buffer) {
        Dataset dataset = preSetImage(fileName, fileType, width, height, nBandss, dataType);
        int index = 0;
        for (int i = 0; i < nBandss; i++) {
            byte[] tmpbuffer = new byte[width * height];
            Band writeBand = dataset.GetRasterBand(i + 1);
            System.arraycopy(buffer, index, tmpbuffer, 0, width * height);
            writeBand.WriteRaster(0, 0, width, height, tmpbuffer);
            index = index + width * height;
        }
        dataset.delete();
    }


    /**
     * 将遥感影像的字节序转为BIP
     *
     * @param srcFileName
     * @param dstFileName
     */
    public static void transToBIP(String srcFileName, String dstFileName) {
//        gdal.Translate(dstFileName, srcFileName, "INTERLEAVE=BAND");
    }
}
