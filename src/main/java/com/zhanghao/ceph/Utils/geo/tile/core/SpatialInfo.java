package com.zhanghao.ceph.Utils.geo.tile.core;


import com.zhanghao.ceph.Utils.geo.UtilImageInfo;

/**
 * Created by Lenovo on 2019/3/29.
 * 空间信息的公共字段
 * 经纬度的四角坐标、空间分辨率、投影类型
 */

public class SpatialInfo {

    /**
     * 左上经度
     */
    protected Double ullon;


    protected Double ullat;

    /**
     * 左下经度
     */
    protected Double dllon;


    protected Double dllat;

    /**
     * 右下经度
     */
    protected Double drlon;


    protected Double drlat;

    /**
     * 右上经度
     */
    protected Double urlon;


    protected Double urlat;

    /**
     * 左上投影坐标
     */
    protected Double ulxmap;


    protected Double ulymap;

    /**
     * 右下投影坐标
     */
    protected Double drxmap;


    protected Double drymap;


    /**
     * 空间分辨率（米、度）
     */
    protected Double resolution;

    /**
     * 条带号
     */
    protected String scenePath;

    /**
     * 行编号
     */
    protected String sceneRow;

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
    protected double[] geoTrans;

    protected String geoHashCode1;

    protected String geoHashCode2;

    protected String geoHashCode3;

    protected String geoHashCode4;

    protected String geoHashCode5;

    protected String geoHashCode6;

    protected String geoHashCode7;

    protected String geoHashCode12;


    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * 得到地理数据格式的SpatialInfo信息
     *
     * @param fileName
     * @return
     */
    public static SpatialInfo readSpatialInfo(String fileName) {
        if (fileName != null) {
            try {
                SpatialInfo spatialInfo = new SpatialInfo();
                //读取影像基本信息
                UtilImageInfo imageInfo = new UtilImageInfo(fileName);
                spatialInfo.setBBoxLonLat(imageInfo.getBboxLonLat());
                //  spatialInfo.setBBoxMap(imageInfo.getImgTransform());  // TODO BBox有误
                spatialInfo.setResolution(imageInfo.getMiResolution());
                spatialInfo.setProjectionType(imageInfo.getProjName());
                spatialInfo.setBandCount(imageInfo.getBandNumb());
                spatialInfo.setWidth(imageInfo.getnWidth());
                spatialInfo.setHeight(imageInfo.getnHeight());
                spatialInfo.setGeoTrans(imageInfo.getImgTransform());
                spatialInfo.setProWKT(imageInfo.getProj());
                return spatialInfo;
            } catch (Exception ex) {
            }
        }
        return null;
    }


    /**
     * 经纬度表示的地理范围是否有效
     *
     * @return
     */
    public Boolean isValid() {
        if (this.ullat != null &&
                this.ullat >= -90.0 &&
                this.ullat <= 90.0 &&
                this.ullon != null &&
                this.ullon >= -180.0 &&
                this.ullon <= 180.0 &&
                this.drlat != null &&
                this.drlat >= -90.0 &&
                this.drlat <= -90.0 &&
                this.drlon != null &&
                this.drlon >= -180.0 &&
                this.drlon <= 180.0 &&
                this.ullat > this.drlat &&
                this.ullon < this.drlon) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断是否包含某个矩形对象
     *
     * @param spatialInfo
     * @return
     */
    public Boolean isContains(SpatialInfo spatialInfo) {
        if ((this.ullon <= spatialInfo.ullon && this.drlon >= spatialInfo.drlon) &&
                (this.ullat >= spatialInfo.ullat && this.drlat <= spatialInfo.drlat)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否与某个矩形对象相交
     *
     * @param spatialInfo
     * @return
     */
    public Boolean isIntersection(SpatialInfo spatialInfo) {
        if (this.drlon < spatialInfo.ullon ||
                this.ullon > spatialInfo.drlon ||
                this.ullat < spatialInfo.drlat ||
                this.drlat > spatialInfo.ullat) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 得到相交矩形
     *
     * @param spatialInfo
     * @return
     */
    public SpatialInfo getIntersection(SpatialInfo spatialInfo) {
        SpatialInfo interSpatialInfo = new SpatialInfo();
        interSpatialInfo.setUllon(Math.max(this.getUllon(), spatialInfo.getUllon()));
        interSpatialInfo.setUllat(Math.min(this.getUllat(), spatialInfo.getUllat()));
        interSpatialInfo.setDrlon(Math.min(this.getDrlon(), spatialInfo.getDrlon()));
        interSpatialInfo.setDrlat(Math.max(this.getDrlat(), spatialInfo.getDrlat()));
        interSpatialInfo.setResolution(spatialInfo.getResolution());

        if (interSpatialInfo.getUllon() < interSpatialInfo.getDrlon() && interSpatialInfo.getUllat() > interSpatialInfo.getDrlat()) {
            return interSpatialInfo;
        } else {
            return null;
        }
    }

    /**
     * 判断是否包含某个点对象
     * @param lon
     * @param lat
     * @return
     */
    public Boolean isContains(double lon,double lat) {
        if (lon >= this.ullon &&
                lon <= this.drlon &&
                lat >= this.drlat &&
                lat <= this.ullat) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 是否是与影像匹配的层级  TODO
     *
     * @param level
     * @return
     */
    public Boolean isMatchingLevel(int level) {

        return true;
    }

    /**
     * 设置四角坐标（经纬度）
     *
     * @param bbox 四个参数；左上经度、右下纬度、右下经度、左上纬度
     */
    public void setBBoxLonLat(double[] bbox) {
        if (bbox != null && bbox.length >= 4) {
            //四角坐标存在时，进行成员变量初始化
            this.ullat = bbox[3];
            this.ullon = bbox[0];
            this.drlat = bbox[1];
            this.drlon = bbox[2];
            this.urlat = this.ullat;
            this.urlon = this.drlon;
            this.dllon = this.ullon;
            this.dllat = this.drlat;
        } else {
            //四角坐标不存在时，默认赋值0
            this.ullat = 0.0;
            this.ullon = 0.0;
            this.drlat = 0.0;
            this.drlon = 0.0;
            this.urlat = 0.0;
            this.urlon = 0.0;
            this.dllon = 0.0;
            this.dllat = 0.0;
        }
    }


    /**
     * 设置四角坐标（经纬度）
     *
     * @param spatialInfo
     */
    public void setBBoxLonLat(SpatialInfo spatialInfo) {
        if (spatialInfo != null) {
            //四角坐标存在时，进行成员变量初始化
            this.ullat = spatialInfo.ullat;
            this.ullon = spatialInfo.ullon;
            this.drlat = spatialInfo.drlat;
            this.drlon = spatialInfo.drlon;
            this.urlat = spatialInfo.urlat;
            this.urlon = spatialInfo.urlon;
            this.dllon = spatialInfo.dllon;
            this.dllat = spatialInfo.dllat;
        } else {
            //四角坐标不存在时，默认赋值0
            this.ullat = 0.0;
            this.ullon = 0.0;
            this.drlat = 0.0;
            this.drlon = 0.0;
            this.urlat = 0.0;
            this.urlon = 0.0;
            this.dllon = 0.0;
            this.dllat = 0.0;
        }
    }


    /**
     * 得到四角坐标（经纬度,4参数）
     *
     * @return bbox 左上经度、右下纬度、右下经度、左上纬度
     */
    public double[] getBBoxLonLat() {
        if (this.ullon != null && this.drlat != null && this.drlon != null && this.ullat != null) {
            return new double[]{this.ullon, this.drlat, this.drlon, this.ullat};
        } else {
            return null;
        }
    }

    /**
     * 设置四角坐标（投影）
     *
     * @param bbox bbox 左上、右下、右下、左上
     */
    public void setBBoxMap(double[] bbox) {
        if (bbox != null && bbox.length >= 4) {
            //四角坐标存在时，进行成员变量初始化
            this.ulymap = bbox[3];
            this.ulxmap = bbox[0];
            this.drymap = bbox[1];
            this.drxmap = bbox[2];
        } else {
            //四角坐标不存在时，默认赋值0
            this.ulymap = 0.0;
            this.ulxmap = 0.0;
            this.drymap = 0.0;
            this.drxmap = 0.0;
        }
    }

    /**
     * 设置四角坐标（投影）
     *
     * @return bbox 左上、右下、右下、左上
     */
    public double[] getBBoxMap() {
        if (this.ulxmap != null && this.drymap != null && this.drxmap != null && this.ulymap != null) {
            return new double[]{this.ulxmap, this.drymap, this.drxmap, this.ulymap};
        } else {
            return null;
        }
    }


    public Double getUllon() {
        return ullon;
    }

    public void setUllon(Double ullon) {
        this.ullon = ullon;
    }

    public Double getUllat() {
        return ullat;
    }

    public void setUllat(Double ullat) {
        this.ullat = ullat;
    }

    public Double getDrlon() {
        return drlon;
    }

    public void setDrlon(Double drlon) {
        this.drlon = drlon;
    }

    public Double getDrlat() {
        return drlat;
    }

    public void setDrlat(Double drlat) {
        this.drlat = drlat;
    }

    public Double getDllon() {
        return dllon;
    }

    public void setDllon(Double dllon) {
        this.dllon = dllon;
    }

    public Double getDllat() {
        return dllat;
    }

    public void setDllat(Double dllat) {
        this.dllat = dllat;
    }

    public Double getUrlon() {
        return urlon;
    }

    public void setUrlon(Double urlon) {
        this.urlon = urlon;
    }

    public Double getUrlat() {
        return urlat;
    }

    public void setUrlat(Double urlat) {
        this.urlat = urlat;
    }

    public String getGeoHashCode1() {
        return geoHashCode1;
    }

    public void setGeoHashCode1(String geoHashCode1) {
        this.geoHashCode1 = geoHashCode1;
    }

    public String getGeoHashCode2() {
        return geoHashCode2;
    }

    public void setGeoHashCode2(String geoHashCode2) {
        this.geoHashCode2 = geoHashCode2;
    }

    public String getGeoHashCode3() {
        return geoHashCode3;
    }

    public void setGeoHashCode3(String geoHashCode3) {
        this.geoHashCode3 = geoHashCode3;
    }

    public String getGeoHashCode4() {
        return geoHashCode4;
    }

    public void setGeoHashCode4(String geoHashCode4) {
        this.geoHashCode4 = geoHashCode4;
    }

    public String getGeoHashCode5() {
        return geoHashCode5;
    }

    public void setGeoHashCode5(String geoHashCode5) {
        this.geoHashCode5 = geoHashCode5;
    }

    public String getGeoHashCode6() {
        return geoHashCode6;
    }

    public void setGeoHashCode6(String geoHashCode6) {
        this.geoHashCode6 = geoHashCode6;
    }

    public String getGeoHashCode7() {
        return geoHashCode7;
    }

    public void setGeoHashCode7(String geoHashCode7) {
        this.geoHashCode7 = geoHashCode7;
    }

    public String getGeoHashCode12() {
        return geoHashCode12;
    }

    public void setGeoHashCode12(String geoHashCode12) {
        this.geoHashCode12 = geoHashCode12;
    }

    public Double getResolution() {
        return resolution;
    }

    public void setResolution(Double resolution) {
        this.resolution = resolution;
    }

    public String getProjectionType() {
        return projectionType;
    }

    public void setProjectionType(String projectionType) {
        this.projectionType = projectionType;
    }

    public Double getUlxmap() {
        return ulxmap;
    }

    public void setUlxmap(Double ulxmap) {
        this.ulxmap = ulxmap;
    }

    public Double getUlymap() {
        return ulymap;
    }

    public void setUlymap(Double ulymap) {
        this.ulymap = ulymap;
    }

    public Double getDrxmap() {
        return drxmap;
    }

    public void setDrxmap(Double drxmap) {
        this.drxmap = drxmap;
    }

    public Double getDrymap() {
        return drymap;
    }

    public void setDrymap(Double drymap) {
        this.drymap = drymap;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getBandCount() {
        return bandCount;
    }

    public void setBandCount(Integer bandCount) {
        this.bandCount = bandCount;
    }

    public String getProWKT() {
        return proWKT;
    }

    public void setProWKT(String proWKT) {
        this.proWKT = proWKT;
    }

    public double[] getGeoTrans() {
        return geoTrans;
    }

    public void setGeoTrans(double[] geoTrans) {
        this.geoTrans = geoTrans;
    }

    public String getScenePath() {
        return scenePath;
    }

    public void setScenePath(String scenePath) {
        this.scenePath = scenePath;
    }

    public String getSceneRow() {
        return sceneRow;
    }

    public void setSceneRow(String sceneRow) {
        this.sceneRow = sceneRow;
    }
}
