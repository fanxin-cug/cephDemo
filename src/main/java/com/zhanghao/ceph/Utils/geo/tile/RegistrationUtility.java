package com.zhanghao.ceph.Utils.geo.tile;


import com.zhanghao.ceph.Utils.geo.tile.core.GdalIOUtil;
import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmj on 2017/5/25.
 */
public class RegistrationUtility {

    public static final double GCP_RMSE = 0.0;

    /**
     * //从文件全路径中获取文件名,不包括后缀
     * @param imgPath
     * @return
     */
    public static String GetFileName(String imgPath) {
        String fileName = "";
        File file = new File(imgPath);
        if (file != null) {
            fileName = file.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     *  //从文件全路径中获取文件名,不包括后缀
     * @param imgPath
     * @return
     */
    public static String GetFileExt(String imgPath) {
        String fileExt = "";
        String fileName = "";
        File file = new File(imgPath);
        if (file != null) {
            fileName = file.getName();
            int startIndex=fileName.lastIndexOf(".");
            int fileLength=fileName.length();
            fileExt = fileName.substring(startIndex,fileLength);
        }
        return fileExt;
    }

    /**
     * //判断路径是否存在，如果没有，则创建
     * @param path
     * @return
     */
    public static boolean DirectoryExistAndCreate(String path) {
        File pathFile = new File(path);
        if (!pathFile.exists() && !pathFile.isDirectory()) {
            boolean bSucess = pathFile.mkdirs();
            if (!bSucess) {
                return false;
            }
        }
        return true;
    }

    /**
     * ///删除文件或者路径
     * @param strFile
     * @return
     */
    public static boolean DeleteFilePath(String strFile) {
        File file = new File(strFile);
        if (file.exists()) {
            boolean bSucess = file.delete();
            if (!bSucess) {
                return false;
            }
        }
        return true;
    }

    /**
     * ///文件或路径是否存在
     * @param strFile
     * @return
     */
    public static boolean FilePathExist(String strFile) {
        File file = new File(strFile);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    /*@brief	反求图像坐标
     *
     *	给定坐标转换参数,由地理坐标反推像素坐标.
     *
     *	@param	dXGeo			[in] 地理X坐标
     *	@param	dYGeo			[in] 地理Y坐标
     *  返回转换后的X和Y坐标
     */
    public static double[] GetPixByTransform(double[] imgTransform, double dXGeo, double dYGeo) {
        double[] result = new double[2];
        double temp = imgTransform[1] * imgTransform[5] - imgTransform[2] * imgTransform[4];
        result[0] = (imgTransform[5] * (dXGeo - imgTransform[0]) - imgTransform[2] * (dYGeo - imgTransform[3])) / temp + 0.5;
        result[1] = (imgTransform[1] * (dYGeo - imgTransform[3]) - imgTransform[4] * (dXGeo - imgTransform[0])) / temp + 0.5;
        return result;
    }

    /*@brief	反求地理坐标
     *
     *	给定坐标转换参数,由像素坐标反推地理坐标.
     *
     *	@param	dGeoTransform	[in] 坐标转换参数
     *	@param	dXPix			[in] 像素X坐标
     *	@param	dYPix			[in] 像素Y坐标
     */
    public static double[] GetGeoByTransform(double[] imgTransform, double dXPix, double dYPix) {
        double[] result = new double[2];
        result[0] = imgTransform[0] + dXPix * imgTransform[1] + dYPix * imgTransform[2];
        result[1] = imgTransform[3] + dXPix * imgTransform[4] + dYPix * imgTransform[5];
        return result;
    }

    //完成投影坐标和经纬度坐标之间的相互转换
    //dX 待转换的X坐标
    //dY 待转换的Y坐标
    //bConvertToGeo  true:将投影坐标转换为经纬度坐标， false:将经纬度坐标转换为投影坐标
    public static boolean ConvertMapBettewnGeo(double[] dX, double[] dY, String srcWkt,String dstWkt) {
        SpatialReference sp1 = new SpatialReference(srcWkt);
        SpatialReference sp2 = new SpatialReference();
        sp2.ImportFromWkt(dstWkt);

        CoordinateTransformation ct = new CoordinateTransformation(sp1, sp2);

        for (int i = 0; i < dX.length; i++) {
            double[] result = ct.TransformPoint(dX[i], dY[i]);
            dX[i] = result[0];
            dY[i] = result[1];
        }

        return true;
    }

    //完成投影坐标和经纬度坐标之间的相互转换
    //dX 待转换的X坐标
    //dY 待转换的Y坐标
    //bConvertToGeo  true:将投影坐标转换为经纬度坐标， false:将经纬度坐标转换为投影坐标
    public static boolean ConvertMapBettewnGeo(double[] dX, double[] dY, String proj, boolean bConvertToGeo) {
        SpatialReference sp1 = new SpatialReference(GdalIOUtil.strProjectionLngLat);
        //sp1.ImportFromEPSG(4326);
        SpatialReference sp2 = new SpatialReference();
        sp2.ImportFromWkt(proj);

        CoordinateTransformation ct = null;
        if (bConvertToGeo) {
            ct = new CoordinateTransformation(sp2, sp1);
        } else {
            ct = new CoordinateTransformation(sp1, sp2);
        }

        for (int i = 0; i < dX.length; i++) {
            double[] result = ct.TransformPoint(dX[i], dY[i]);
            dX[i] = result[0];
            dY[i] = result[1];
        }

        return true;
    }

    //完成投影坐标和经纬度坐标之间的相互转换
    //dX 待转换的X坐标
    //dY 待转换的Y坐标
    //bConvertToGeo  true:将投影坐标转换为经纬度坐标， false:将经纬度坐标转换为投影坐标
    public static double[] ConvertMapBettewnGeo(double dX, double dY, String proj, boolean bConvertToGeo) {
        SpatialReference sp1 = new SpatialReference(GdalIOUtil.strProjectionLngLat);
        //sp1.ImportFromEPSG(4326);
        SpatialReference sp2 = new SpatialReference();
        sp2.ImportFromWkt(proj);

        CoordinateTransformation ct = null;
        if (bConvertToGeo) {
            ct = new CoordinateTransformation(sp2, sp1);
        } else {
            ct = new CoordinateTransformation(sp1, sp2);
        }

        double[] result = ct.TransformPoint(dX, dY);

        return result;
    }

    /**
     * 将点度的分辨率转换为米
     * @param resolution
     * @return
     */
    public static double ConvertDgreeToMette(double resolution) {
        resolution = resolution * 111130;
        DecimalFormat df = new DecimalFormat("#.#");
        String strResolution = df.format(resolution);

        resolution = Double.valueOf(strResolution);
        return resolution;
    }

    /**
     * 将米的分辨率转换为点度
     * @param resolution
     * @return
     */
    public static double ConvertMetteToDgree(double resolution) {
        resolution = resolution / 111130.0;
        return resolution;
    }

    //给没有地理坐标的图像加人为增加投影信息
    //inputImg输入图像
    //resultImg 加投影后的结果图像
    //strProj 投影字符串
    //imgTransform 坐标转换参数
    public static boolean AddProjToImg(String inputImg, String resultImg, String strProj, double[] imgTransform) {
        try {
            File tifFile = new File(inputImg);

            Dataset datasetInput = gdal.Open(tifFile.toString(), gdalconstConstants.GA_ReadOnly);
            if (datasetInput == null) {
                return false;
            }

            Band inputBand = datasetInput.GetRasterBand(1);

            int nWidth = datasetInput.getRasterXSize();
            int nHeight = datasetInput.getRasterYSize();
            int bandNumb = datasetInput.getRasterCount();
            int dataType = inputBand.GetRasterDataType();
            ColorTable colorTable = inputBand.GetColorTable();

            //创建一个新的tif图像
            String pszFormat = "GTiff";
            Driver driver = gdal.GetDriverByName(pszFormat);
            Dataset datasetResult = driver.Create(resultImg, nWidth, nHeight, bandNumb, dataType);
            if (driver == null) {
                datasetInput.delete();
                return false;
            }

//            driver.delete();

//            //打开结果图像，写入数据
//            File resultFile = new File(resultImg);
//            Dataset datasetResult = gdal.Open(resultFile.toString(), gdalconstConstants.GF_Write);
            if (datasetResult == null) {
                return false;
            }

            //计算分块个数
            int blockSize = 1024;
            int blockNumber = nHeight / blockSize;
            int lastSize = nHeight % blockSize;
            if (lastSize > 0) {
                blockNumber += 1;
            }
            //分波段、分块写数据
            for (int j = 0; j < bandNumb; j++) {
                for (int i = 0; i < blockNumber; i++) {
                    Band readBand = datasetInput.GetRasterBand(j + 1);
                    Band writeBand = datasetResult.GetRasterBand(j + 1);

                    int realSize = blockSize;
                    if (lastSize > 0 && i == blockNumber - 1) {
                        realSize = lastSize;
                    }
                    if (dataType == 1) {   //8位
                        byte[] pBuf = new byte[realSize * nWidth];
                        readBand.ReadRaster(0, i * blockSize, nWidth, realSize, pBuf);
                        writeBand.WriteRaster(0, i * blockSize, nWidth, realSize, pBuf);
                        pBuf = null;
                    } else if (dataType == 2) {  //16位
                        int[] pBuf = new int[realSize * nWidth];
                        readBand.ReadRaster(0, i * blockSize, nWidth, realSize, 5,pBuf);
                        writeBand.WriteRaster(0, i * blockSize, nWidth, realSize, 5,pBuf);
                        pBuf = null;
                    }


                    if (colorTable != null) {
                        writeBand.SetColorTable(colorTable);
                    }
                }
            }

            //写投影字符串和坐标转换参数
            datasetResult.SetProjection(strProj);
            datasetResult.FlushCache();
            datasetResult.SetGeoTransform(imgTransform);

            //关闭
            datasetInput.delete();
            datasetResult.delete();
        } catch (Exception ex) {

        }
        return true;
    }

    /**
     * 将图像保存为BMP格式
     * @param inputFile
     * @param outFile
     * @return
     */
    public static double SaveImgToBMP(String inputFile, String outFile) {
        File tifFile = new File(inputFile);

        Dataset datasetInput = gdal.Open(tifFile.toString(), gdalconstConstants.GA_ReadOnly);
        if (datasetInput == null) {
            return -1;
        }

        Band inputBand = datasetInput.GetRasterBand(1);

        int nWidth = datasetInput.getRasterXSize();
        int nHeight = datasetInput.getRasterYSize();
        int bandNumb = datasetInput.getRasterCount();
        int dataType = inputBand.GetRasterDataType();
        ColorTable colorTable = inputBand.GetColorTable();

        int[] bandIndex;
        if (bandNumb>3){
            bandNumb=3;
            bandIndex=new int[bandNumb];
            bandIndex[0]=3;
            bandIndex[1]=2;
            bandIndex[2]=1;
        }
        else if (bandNumb>2){
            bandIndex=new int[bandNumb];
            for (int i=0;i<bandNumb;i++){
                bandIndex[i]=i+1;
            }
        }
        else{
            bandIndex=new int[3];
            for (int i=0;i<3;i++){
                bandIndex[i]=1;
            }
        }

        //计算输出图像的采样率和高度
        int outWidth = 1024;
        int outHeight = nHeight;
        double resampleRate = outWidth * 1.0 / nWidth;
        if (resampleRate < 1.0) {
            outHeight = (int) (outHeight * resampleRate);
        } else {
            outWidth = nWidth;
        }

        //创建输出图像
        //创建一个新的tif图像
        String pszFormat = "BMP";
        Driver driver = gdal.GetDriverByName(pszFormat);
        Dataset datasetResult = driver.Create(outFile, outWidth, outHeight, 3, 1);
        if (driver == null) {
            datasetInput.delete();
            return -1;
        }

        if (datasetResult == null) {
            return -1;
        }

        //分波段写数据
        for (int j = 0; j < 3; j++) {
            Band readBand = datasetInput.GetRasterBand(bandIndex[j]);
            Band writeBand = datasetResult.GetRasterBand(j + 1);

            if (dataType == 1) {   //8位
                byte[] pBuf = new byte[outWidth * outHeight];
                readBand.ReadRaster(0, 0, nWidth, nHeight, outWidth, outHeight, dataType, pBuf);
                writeBand.WriteRaster(0, 0, outWidth, outHeight, pBuf);
                pBuf = null;
            } else if (dataType == 2) {  //16位
                int[] pBuf = new int[outWidth * outHeight];
                byte[] pDst= new byte[outWidth * outHeight];
                readBand.ReadRaster(0, 0, nWidth, nHeight, outWidth, outHeight, 5, pBuf);
                //16位转8位
                //读取直方图
                int nBuckets = 65535;
                int[] pHist = new int[65535];
                readBand.GetHistogram(0.5, nBuckets + 0.5, pHist);
                //进行转换
                StretchDataUShort(pBuf,pDst,pHist,nBuckets,outWidth * outHeight,1,0.02);

                writeBand.WriteRaster(0, 0, outWidth, outHeight, pDst);
                pBuf = null;
            }

            if (colorTable != null) {
                writeBand.SetColorTable(colorTable);
            }
        }

        datasetResult.FlushCache();

        //关闭
        datasetInput.delete();
        datasetResult.delete();

        return resampleRate;
    }

    /**
     * 将16位图像转换为8位
     * @param pSrc
     * @param pDst
     * @param pHist
     * @param nBucket
     * @param pxlNum
     * @param dnStart
     * @param coef
     */
    public static  void StretchDataUShort(int[] pSrc, byte[] pDst,int[] pHist, int nBucket, int pxlNum, int dnStart, double coef) {
       //求和
        long nSum=0;
        for (int i=0;i<nBucket;i++){
            nSum+=pHist[i];
        }

        long thresh = (long) (nSum * coef);

        int countMin = 0, dnMin = 0;
        for (int i = 0; i < nBucket; ++i)
        {
            countMin += pHist[i];
            if (countMin >= thresh)
            {
                dnMin = i + dnStart;
                break;
            }
        }

        int countMax = 0, dnMax = 0;
        for (int i = nBucket - 1; i > -1; --i)
        {
            countMax += pHist[i];
            if (countMax >= thresh)
            {
                dnMax = i + dnStart;
                break;
            }
        }

        for (int i = 0; i < pxlNum; ++i)
        {
            if (pSrc[i] < dnMin)	pDst[i] = 0;
            else if (pSrc[i] > dnMax)	pDst[i] = (byte)(255);
            else	pDst[i] = (byte)(255 * (pSrc[i] - dnMin) / (dnMax - dnMin));
        }
    }

    /**
     * 根据分辨率，获得对应的瓦片数据层级
     * @param dResolution 分辨率
     * @return
     */
    public static String GetTileLevel(double dResolution){
        String strTileLevel="";

        double [] levelResolution={0.5972,1.194,2.38866,4.777,9.555,19,35};
        String[] levelNumber={"18","17","16","15","14","13","12"};
        int index=0;
        double minDiff=9999.0;
        for (int i=0;i<levelResolution.length;i++){
            double diff=dResolution-levelResolution[i];
            if (diff<0.2){
                index=i;
                break;
            }
            else if (diff<minDiff){
                index=i;
            }
        }

        return levelNumber[index];
    }

    /**
     * 获取一个目录下的所有图像文件，支持的后缀包括：tif，tiff，img，dat，TIF，TIFF
     * @param directoryPath
     * @return
     */
    public static List<String> GetAllImgFile(String directoryPath){
        File baseFile =new File(directoryPath);
        List<String> fileList = new ArrayList<String>();

        if (baseFile.isFile() || !baseFile.exists()){
            return fileList;
        }

        File[] files =baseFile.listFiles();
        for(File file:files){
            if (file.isDirectory()){
                fileList.addAll(GetAllImgFile(file.getAbsolutePath()));
            }
            else {
                if (file.getName().endsWith("tif") || file.getName().endsWith("tiff")
                        || file.getName().endsWith("img")|| file.getName().endsWith("dat")
                        ||file.getName().endsWith("TIF") || file.getName().endsWith("TIFF")){
                    fileList.add(file.getAbsolutePath());
                }
            }
        }

        return fileList;
    }

    /**
     * 获取一个目录下的所有压缩文件，支持的格式包括：gz
     * @param directoryPath
     * @return
     */
    public static List<String> GetAllZIPFile(String directoryPath){
        File baseFile =new File(directoryPath);
        List<String> fileList = new ArrayList<String>();

        if (baseFile.isFile() || !baseFile.exists()){
            return fileList;
        }

        File[] files =baseFile.listFiles();
        for(File file:files){
            if (file.isDirectory()){
                fileList.addAll(GetAllImgFile(file.getAbsolutePath()));
            }
            else {
                if (file.getName().endsWith("gz") || file.getName().endsWith("GZ")){
                    fileList.add(file.getAbsolutePath());
                }
            }
        }

        return fileList;
    }
}
