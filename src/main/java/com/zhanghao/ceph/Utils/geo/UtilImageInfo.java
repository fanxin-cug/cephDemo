package com.zhanghao.ceph.Utils.geo;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhanghao.ceph.Utils.geo.tile.RegistrationUtility;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by radi2 on 2017/7/27.
 */

public class UtilImageInfo {

    private static int N_TRANSFORM_PARA_NUM = 6;
    private static String StrProjWGS84 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223560493,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433],AUTHORITY[\"EPSG\",\"4326\"]]";
    private double[] AD_DEFAULT_GEO_TRANSFORM = {0.0, 1.0, 0.0, 0.0, 0.0, 1.0}; //用于判断图像是否有地理坐标

    //文件名
    private String fileName;
    //@JSONField(name="图像路径")
    private String imgPath;
    //@JSONField(name="波段数量")
    private int bandNumb;

    @JSONField(serialize = false)
    private double imgTransform[];

    @JSONField(serialize = false)
    private String proj;

    private int nWidth;

    private int nHeight;

    private String projName;

    @JSONField(serialize = false)
    private boolean bReadSucess;

    private String datumName;

    private double miResolution;

    private double duResolution;

    //图像是投影是否为4326
    private boolean bSame4326;

    // 图像经纬度范围，顺序为最小经度、最小纬度、最大经度、最大纬度
    private double[] bboxLonLat;

    // 图像是否为8位图像
    private boolean IsByte;

    //图像是否包含地理坐标
    private boolean bContainGeoInfo;

    public String getGeoFilePath() {
        return geoFilePath;
    }

    public void setGeoFilePath(String geoFilePath) {
        this.geoFilePath = geoFilePath;
    }

    //对于没有投影的图像，加投影后的图像路径
    private String geoFilePath;

    public boolean isByte() {
        return IsByte;
    }

    public void setByte(boolean aByte) {
        IsByte = aByte;
    }

    // 左上经度、右下纬度、右下经度、左上纬度
    public double[] getBboxLonLat() {
        return bboxLonLat;
        //Collections.min(minX), Collections.min(minY), Collections.max(maxX), Collections.max(maxY)
    }


    public void setBboxLonLat(double[] bboxLonLat) {
        this.bboxLonLat = bboxLonLat;
    }

    public int getBandNumb() {
        return bandNumb;
    }

    public void setBandNumb(int bandNumb) {
        bandNumb = bandNumb;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getnWidth() {
        return nWidth;
    }

    public void setnWidth(int nWidth) {
        this.nWidth = nWidth;
    }

    public int getnHeight() {
        return nHeight;
    }

    public void setnHeight(int nHeight) {
        this.nHeight = nHeight;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getDatumName() {
        return datumName;
    }

    public void setDatumName(String datumName) {
        this.datumName = datumName;
    }

    public double[] getImgTransform() {
        return imgTransform;
    }

    public void setImgTransform(double[] imgTransform) {
        this.imgTransform = imgTransform;
    }

    public boolean isbReadSucess() {
        return bReadSucess;
    }

    public double getDuResolution() {
        if (bSame4326) {
//            System.out.println("分辨率：" + duResolution + "，图像宽：" + this.nWidth + "，图像高：" + this.nHeight);
            return duResolution;
        } else {
            return RegistrationUtility.ConvertMetteToDgree(miResolution);
        }
    }

    public void setDuResolution(double duResolution) {
        this.duResolution = duResolution;
    }

    public double getMiResolution() {
        if (bSame4326) {
            return this.ConvertDgreeToMette(miResolution);
        }
        return miResolution;
    }

    /**
     * 将点度的分辨率转换为米
     *
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


    public void setMiResolution(double miResolution) {
        this.miResolution = miResolution;
    }

    public String getProj() {
        return proj;
    }

    public void setProj(String proj) {
        this.proj = proj;
    }

    public boolean isbSame4326() {
        return bSame4326;
    }

    public void setbSame4326(boolean bSame4326) {
        this.bSame4326 = bSame4326;
    }

    public boolean isbContainGeoInfo() {
        return bContainGeoInfo;
    }

    public void setbContainGeoInfo(boolean bContainGeoInfo) {
        this.bContainGeoInfo = bContainGeoInfo;
    }

    public String getFileNameNoExtensions() {
        int len = fileName.lastIndexOf(".");
        return fileName.substring(0, len);
    }

    /**
     * 构造函数
     *
     * @param utilImageInfo 已有图像参数
     */
    public UtilImageInfo(UtilImageInfo utilImageInfo, double ratio ) {
        try {
            this.proj = utilImageInfo.getProj();
            this.imgTransform = new double[6];
            for (int i = 0; i < 6; i++) {
                this.imgTransform[i] = utilImageInfo.getImgTransform()[i];
            }
            this.imgTransform[1] = this.imgTransform[1] / ratio;
            this.imgTransform[5] = this.imgTransform[5] / ratio;

            // 左上经度、右下纬度、右下经度、左上纬度
            this.bboxLonLat = new double[4];
            for (int i = 0; i < 4; i++) {
                this.bboxLonLat[i] = utilImageInfo.getBboxLonLat()[i];
            }
        } catch (Exception ex) {

        }
    }

    /**
     * 构造函数
     *
     * @param utilImageInfo 已有图像参数
     */
    public UtilImageInfo(UtilImageInfo utilImageInfo, double ratio, double resolution) {
        try {
            this.proj = utilImageInfo.getProj();
            this.imgTransform = new double[6];
            for (int i = 0; i < 6; i++) {
                this.imgTransform[i] = utilImageInfo.getImgTransform()[i];
            }
            this.imgTransform[1] = this.imgTransform[1] / ratio;
            this.imgTransform[5] = this.imgTransform[5] / ratio;

            // 左上经度、右下纬度、右下经度、左上纬度
            this.bboxLonLat = new double[4];
            this.bboxLonLat[0] = utilImageInfo.getBboxLonLat()[0] + resolution / 2;
            this.bboxLonLat[1] = utilImageInfo.getBboxLonLat()[1] - resolution / 2;
            this.bboxLonLat[2] = utilImageInfo.getBboxLonLat()[2];
            this.bboxLonLat[3] = utilImageInfo.getBboxLonLat()[3];
        } catch (Exception ex) {

        }
    }


    /**
     * 构造函数
     *
     * @param filePath 图像路径
     */
    public UtilImageInfo(String filePath) {
        try {
            bContainGeoInfo = false;

            bReadSucess = ReadImgInfo(filePath);

            if (bReadSucess) {
                //fileName = filePath.substring()
                //读取文件名和路径
                File file = new File(filePath);
                if (file != null) {
                    fileName = file.getName();
                    imgPath = file.getParent();
                }
            }
        } catch (Exception ex) {
            bReadSucess = false;
        }
    }

    /**
     * 读取图像信息
     *
     * @param fileName
     * @return
     */
    private boolean ReadImgInfo(String fileName) {
        gdal.AllRegister();
        File tifFile = new File(fileName);
        Dataset dataset = gdal.Open(tifFile.toString(), gdalconstConstants.GA_ReadOnly);
        if (dataset == null) {
            return false;
        }
        this.nWidth = dataset.getRasterXSize();
        this.nHeight = dataset.getRasterYSize();
        this.bandNumb = dataset.getRasterCount();
        this.imgTransform = dataset.GetGeoTransform();

        //通过坐标转换参数判断图像是否有地理坐标
        for (int i = 0; i < N_TRANSFORM_PARA_NUM; i++) {
            if ((int) AD_DEFAULT_GEO_TRANSFORM[i] != (int) imgTransform[i]) {
                bContainGeoInfo = true;
                break;
            }
        }

        //如果有投影
        if (bContainGeoInfo) {
            this.proj = dataset.GetProjection();
            if (this.proj.equals("")) { //如果图像有坐标转换参数，则判断是否有投影，
                //没有投影，则判断坐标转换参数的坐标是否为经纬度坐标，如果是，则添加默认投影
//                if (imgTransform[0]>-180.0&imgTransform[0]<180.0&imgTransform[3]>-90.0&imgTransform[3]<90.0){
//                    this.proj=StrProjWGS84;
//                }
//                else {
                bContainGeoInfo = false;
            }

            if (bContainGeoInfo) {

                this.bboxLonLat = this.getBBOXLonLat(dataset);
                this.projName = GetProjName(proj);
                this.datumName = GetDatumName(proj);

                //判断输入图像的投影是否为4326投影
                String wgs84 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.2572235630016,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433],AUTHORITY[\"EPSG\",\"4326\"]]";

                SpatialReference sp1 = new SpatialReference(wgs84);
                //sp1.ImportFromEPSG(4326);
                SpatialReference sp2 = new SpatialReference();
                sp2.ImportFromWkt(proj);
                if (sp1.IsSame(sp2) > 0) {
                    this.bSame4326 = true;
                } else {
                    this.bSame4326 = false;
                }
            }
        }

        this.IsByte = this.IsByteImage(dataset);
        dataset.delete();

        this.miResolution = imgTransform[1];
        this.duResolution = imgTransform[1];
        return true;
    }

    /// <summary>
    /// 获取投影名称
    /// </summary>
    /// <param name="proj"></param>
    /// <returns></returns>
    private String GetProjName(String proj) {
        try {
            SpatialReference srProj = new SpatialReference(proj);
            if (srProj.IsGeographic() == 1) {
                return "longlat";
            } else if (srProj.IsProjected() == 1) {
                return srProj.GetAttrValue("PROJECTION", 0);
            } else if (srProj.IsLocal() == 1) {
                //不显示地理信息，但图像是地理坐标
                return "不支持的投影";
            } else {
                //无任何地理信息
                return "无投影";
            }
        } catch (Exception ex) {
            return "不支持的投影";
        }
    }

    /// <summary>
    /// 获取大地基准名称
    /// </summary>
    /// <param name="proj"></param>
    /// <returns></returns>
    private String GetDatumName(String proj) {
        try {
            SpatialReference srProj = new SpatialReference(proj);
            if (srProj.IsGeographic() == 1) {
                return srProj.GetAttrValue("DATUM", 0);
            } else if (srProj.IsProjected() == 1) {
                return srProj.GetAttrValue("DATUM", 0);
            } else if (srProj.IsLocal() == 1) {
                //不显示地理信息，但图像是地理坐标
                return "不支持的大地基准";
            } else {
                //无任何地理信息
                return "未知";
            }
        } catch (Exception ex) {
            return "不支持的大地基准";
        }
    }


    /**
     * 获取图像的经纬度范围
     *
     * @param dataset
     * @return
     */
    private double[] getBBOXLonLat(Dataset dataset) {
        double[] tr = dataset.GetGeoTransform();
        String wgs84 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.2572235630016,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433],AUTHORITY[\"EPSG\",\"4326\"]]";
        SpatialReference sp1 = new SpatialReference(wgs84);
        //sp1.ImportFromEPSG(4326);
        SpatialReference sp2 = new SpatialReference();
        sp2.ImportFromWkt(this.proj);

        CoordinateTransformation ct = new CoordinateTransformation(sp2, sp1);

        List<Double> coordinateTopX = new ArrayList<Double>();
        List<Double> coordinateTopY = new ArrayList<Double>();

        List<Double> coordinateBottomX = new ArrayList<Double>();
        List<Double> coordinateBottomY = new ArrayList<Double>();

        List<Double> coordinateLeftX = new ArrayList<Double>();
        List<Double> coordinateLeftY = new ArrayList<Double>();

        List<Double> coordinateRightX = new ArrayList<Double>();
        List<Double> coordinateRightY = new ArrayList<Double>();

        for (int i = 0; i <= this.nWidth; i++) {
            double tempTopX = tr[0] + tr[1] * i;
            double tempTopY = tr[3];
            double tempBottomX = tr[0] + tr[1] * i;
            double tempBottomY = tr[3] + this.nHeight * tr[5];

            double[] top = ct.TransformPoint(tempTopX, tempTopY);
            double[] bottom = ct.TransformPoint(tempBottomX, tempBottomY);

            coordinateTopX.add(top[0]);
            coordinateTopY.add(top[1]);
            coordinateBottomX.add(bottom[0]);
            coordinateBottomY.add(bottom[1]);
        }
        for (int j = 0; j <= this.nHeight; j++) {
            double tempLeftX = tr[0];
            double tempLeftY = tr[3] + tr[5] * j;
            double tempRightX = tr[0] + this.nWidth * tr[1];
            double tempRightY = tr[3] + tr[5] * j;

            double[] left = ct.TransformPoint(tempLeftX, tempLeftY);
            double[] right = ct.TransformPoint(tempRightX, tempRightY);

            coordinateLeftX.add(left[0]);
            coordinateLeftY.add(left[1]);
            coordinateRightX.add(right[0]);
            coordinateRightY.add(right[1]);
        }
        List<Double> maxX = new ArrayList<Double>();
        maxX.add(Collections.max(coordinateTopX));
        maxX.add(Collections.max(coordinateBottomX));
        maxX.add(Collections.max(coordinateLeftX));
        maxX.add(Collections.max(coordinateRightX));

        List<Double> minX = new ArrayList<Double>();
        minX.add(Collections.min(coordinateTopX));
        minX.add(Collections.min(coordinateBottomX));
        minX.add(Collections.min(coordinateLeftX));
        minX.add(Collections.min(coordinateRightX));

        List<Double> maxY = new ArrayList<Double>();
        maxY.add(Collections.max(coordinateTopY));
        maxY.add(Collections.max(coordinateBottomY));
        maxY.add(Collections.max(coordinateLeftY));
        maxY.add(Collections.max(coordinateRightY));

        List<Double> minY = new ArrayList<Double>();
        minY.add(Collections.min(coordinateTopY));
        minY.add(Collections.min(coordinateBottomY));
        minY.add(Collections.min(coordinateLeftY));
        minY.add(Collections.min(coordinateRightY));

        double[] BBox = {Collections.min(minX), Collections.min(minY), Collections.max(maxX), Collections.max(maxY)};
        return BBox;
    }


    /**
     * 判断是否为8位影像
     *
     * @param dataset
     * @return
     */
    private boolean IsByteImage(Dataset dataset) {
        Band band = dataset.GetRasterBand(1);
        int number = band.GetRasterDataType();
        if (number == 1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 是否是有地理坐标的图像
     *
     * @param fileName
     */
    public static Boolean IsGeoImage(String fileName) {
        Boolean isGeo = true;
        try {
            UtilImageInfo utilImageInfo = new UtilImageInfo(fileName);
            if (utilImageInfo.getProj() == null ||
                    utilImageInfo.getProj() == "" ||
                    utilImageInfo.getBandNumb() < 1) {
                isGeo = false;
            }
        } catch (Exception ex) {
            isGeo = false;
        }
        return isGeo;
    }

    /**
     * 是否是图像
     *
     * @param fileName
     */
    public static Boolean IsImage(String fileName) {
        Boolean isImg = true;
        try {
            UtilImageInfo utilImageInfo = new UtilImageInfo(fileName);
            if (utilImageInfo.getBandNumb() < 1) {
                isImg = false;
            }
        } catch (Exception ex) {
            isImg = false;
        }
        return isImg;
    }
}
