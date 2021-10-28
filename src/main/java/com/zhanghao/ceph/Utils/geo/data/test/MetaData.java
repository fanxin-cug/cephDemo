package com.zhanghao.ceph.Utils.geo.data.test;

import lombok.Data;


/**
 * Created by Lenovo on 2021/3/29.
 *
 */
@Data
public class MetaData {

    protected String fileName;

    protected String md5;

    protected String path;

    /**
     * 左上经度
     */
    protected Double ulLon;


    protected Double ulLat;

    /**
     * 左下经度
     */
    protected Double dlLon;


    protected Double dlLat;

    /**
     * 右下经度
     */
    protected Double drLon;


    protected Double drLat;

    /**
     * 右上经度
     */
    protected Double urLon;

    protected Double urLat;

    /**
     * 空间分辨率（米、度）
     */
    protected Double resolution;

    /**
     * 图像宽度
     */
    protected Integer width;

    /**
     * 图像高度
     */
    protected Integer height;

    /**
     * 波段数量
     */
    protected Integer bandCount;


    /**
     * 能够切片显示的最大层级
     */
    protected Integer maxLevel;

    /**
     * 投影类型
     */
    protected String projectionType;

    /**
     * 投影字符串
     */
    protected String proWKT;

    /**
     * 投影参数
     */
    protected Double geoTrans1;
    protected Double geoTrans2;
    protected Double geoTrans3;
    protected Double geoTrans4;
    protected Double geoTrans5;
    protected Double geoTrans6;

    protected String geoHashCode1;

    protected String path2;

    protected String path3;

    public static String transToCreateSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table tradi(");
        stringBuilder.append("fileName text not null,");
        stringBuilder.append("md5 text not null,");
        stringBuilder.append("path text not null,");
        stringBuilder.append("ulLon real not null,");
        stringBuilder.append("ulLat real not null,");
        stringBuilder.append("dlLon real not null,");
        stringBuilder.append("dlLat real not null,");
        stringBuilder.append("drLon real not null,");
        stringBuilder.append("drLat real not null,");
        stringBuilder.append("urLon real not null,");
        stringBuilder.append("urLat real not null,");
        stringBuilder.append("resolution real not null,");
        stringBuilder.append("width integer not null,");
        stringBuilder.append("height integer not null,");
        stringBuilder.append("bandCount integer not null,");
        stringBuilder.append("maxLevel integer not null,");
        stringBuilder.append("projectionType text not null,");
        stringBuilder.append("proWKT text not null,");
        stringBuilder.append("geoTrans1 real not null,");
        stringBuilder.append("geoTrans2 real not null,");
        stringBuilder.append("geoTrans3 real not null,");
        stringBuilder.append("geoTrans4 real not null,");
        stringBuilder.append("geoTrans5 real not null,");
        stringBuilder.append("geoTrans6 real not null,");
        stringBuilder.append("geoHashCode1 text not null,");
        stringBuilder.append("path2 text not null,");
        stringBuilder.append("path3 text not null");
        stringBuilder.append(");");
        return stringBuilder.toString();
    }


    public static MetaData CreateMetaData(int index) {
        MetaData metaData = new MetaData();
        metaData.setFileName("fileName.fileName.fileName.fileName.fileName" + index);
        metaData.setMd5("01234567890123456789012345678901234567890123456789");
        metaData.setPath("fileName/fileName/fileName/fileName/fileName/fileName");
        metaData.setUlLon(0.001);
        metaData.setUlLat(0.001);
        metaData.setDlLon(0.001);
        metaData.setDlLat(0.001);
        metaData.setDrLon(0.001);
        metaData.setDrLat(0.001);
        metaData.setUrLon(0.001);
        metaData.setUrLat(0.001);
        metaData.setResolution(0.001);
        metaData.setWidth(1024);
        metaData.setHeight(1024);
        metaData.setBandCount(8);
        metaData.setMaxLevel(18);
        metaData.setProjectionType("PROJCS[\"WGS 84 / Pseudo-Mercator\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],EXTENSION[\"PROJ4\",\"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs\"],AUTHORITY[\"EPSG\",\"3857\"],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH]]");
        metaData.setProWKT("LngLat");
        metaData.setGeoTrans1(0.001);
        metaData.setGeoTrans2(0.001);
        metaData.setGeoTrans3(0.001);
        metaData.setGeoTrans4(0.001);
        metaData.setGeoTrans5(0.001);
        metaData.setGeoTrans6(0.001);
        metaData.setGeoHashCode1("rsazk000123");
        metaData.setPath2("fileName/fileName/fileName/fileName/fileName/fileName2");
        metaData.setPath3("fileName/fileName/fileName/fileName/fileName/fileName3");
        return metaData;
    }
}
