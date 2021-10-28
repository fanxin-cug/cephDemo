package com.zhanghao.ceph.Utils.geo.tile.core;


import com.zhanghao.ceph.Utils.geo.tile.RegistrationUtility;
import com.zhanghao.ceph.Utils.geo.tile.mem.Size;
import org.apache.log4j.Logger;
import org.gdal.osr.SpatialReference;

import java.awt.*;


/**
 * Created by Lenovo on 2019/4/10.
 */
public class SpatialTileHelper {
    private static final Logger log = Logger.getLogger(SpatialTileHelper.class);

    /**
     * 通过XYZ计算地理范围
     *
     * @param tileCol
     * @param tileRow
     * @param level
     * @return
     */
    public static SpatialInfo getTileLonLatRangeByXYZ(int tileCol, int tileRow, int level) {
        SpatialInfo spatialInfo = new SpatialInfo();
        int n = 1 << level;
        double tileResolution = 360.0D / n;
        spatialInfo.setUllon(tileCol * tileResolution - 180);
        spatialInfo.setUllat(90 - tileRow * tileResolution);
        spatialInfo.setDrlon((tileCol + 1) * tileResolution - 180);
        spatialInfo.setDrlat(90 - (tileRow + 1) * tileResolution);
        spatialInfo.setResolution(tileResolution / TileConsts.tilesize);
        return spatialInfo;
    }


    /**
     * 根据经纬度范围计算瓦片范围
     *
     * @param ullon
     * @param ullat
     * @param drlon
     * @param drlat
     * @param level
     * @param isDB  直接读取数据库时，层级应该减1；以服务方式获取瓦片时，层级不用减1
     * @return
     */
    public static Rectangle getTileRangeByLonlat(double ullon, double ullat, double drlon, double drlat, int level, Boolean isDB) {
        // 计算瓦片参数
        int girdNumXY = (int) Math.pow(2, level);
        if (isDB) {
            girdNumXY = (int) Math.pow(2, level - 1);
        }
        double tileResolution = (360.0 / girdNumXY);

        int ulX = (int) Math.floor((180 + ullon) / tileResolution);
        int ulY = (int) Math.floor((90 - ullat) / tileResolution);
        int drX = (int) Math.ceil((180 + drlon) / tileResolution);
        int drY = (int) Math.ceil((90 - drlat) / tileResolution);
        return new Rectangle(ulX, ulY, drX - ulX, drY - ulY);
    }

    /**
     * 根据经纬度范围计算在全球的像素范围
     *
     * @param ullon
     * @param ullat
     * @param drlon
     * @param drlat
     * @param level
     * @param isDB  直接读取数据库时，层级应该减1；以服务方式获取瓦片时，层级不用减1
     * @return
     */
    public static Rectangle getPixelRangeByLonlat(double ullon, double ullat, double drlon, double drlat, int level, Boolean isDB) {
        // 计算瓦片参数
        int girdNumXY = (int) Math.pow(2, level);
        if (isDB) {
            girdNumXY = (int) Math.pow(2, level - 1);
        }
        double pixelResolution = (360.0 / girdNumXY) / TileConsts.tilesize;

        int x = (int) Math.floor((180 + ullon) / pixelResolution);
        int y = (int) Math.floor((90 - ullat) / pixelResolution);
        int width = (int) Math.ceil(Math.abs(ullon - drlon) / pixelResolution);
        int height = (int) Math.ceil(Math.abs(drlat - ullat) / pixelResolution);
        return new Rectangle(x, y, width, height);
    }

    /**
     * 根据经纬度范围计算像素范围
     *
     * @param ullon
     * @param ullat
     * @param drlon
     * @param drlat
     * @param level
     * @param isDB  直接读取数据库时，层级应该减1；以服务方式获取瓦片时，层级不用减1
     * @return
     */
    public static Size getPixelSizeByLonlat(double ullon, double ullat, double drlon, double drlat, int level, Boolean isDB) {
        // 计算瓦片参数
        int girdNumXY = (int) Math.pow(2, level);
        if (isDB) {
            girdNumXY = (int) Math.pow(2, level - 1);
        }
        double pixelResolution = (360.0 / girdNumXY) / TileConsts.tilesize;
        int width = (int) Math.ceil(Math.abs(ullon - drlon) / pixelResolution);
        int height = (int) Math.ceil(Math.abs(drlat - ullat) / pixelResolution);
        return new Size(width, height);
    }

    /**
     * 根据经纬度点计算其在全球中的定位
     *
     * @param lon
     * @param lat
     * @param level
     * @param isDB  直接读取数据库时，层级应该减1；以服务方式获取瓦片时，层级不用减1
     * @return
     */
    public static Size getPixelIndexByLonlat(double lon, double lat, int level, Boolean isDB) {
        // 计算瓦片参数
        // 注意：直接读数据库的瓦片时，层级应该减1；以服务方式获取瓦片时，层级不能减1
        // int girdNumXY = (int) Math.pow(2, level - 1);
        // 计算瓦片参数
        int girdNumXY = (int) Math.pow(2, level);
        if (isDB) {
            girdNumXY = (int) Math.pow(2, level - 1);
        }
        double pixelResolution = (360.0 / girdNumXY) / TileConsts.tilesize;

        int x = (int) Math.floor((180 + lon) / pixelResolution);
        int y = (int) Math.ceil((90 - lat) / pixelResolution);

        return new Size(x, y);
    }

    /**
     * 根据层级计算分辨率(度）
     *
     * @param level
     * @return
     */
    public static double getResolutionByLevel(int level) {
        int n = 1 << level;
        double tileWidth = 360.0D / n;
        return tileWidth / TileConsts.tilesize;
    }

    /**
     * 获取partialSpatialInfo在fullSpatialInfo中重叠区域（像素）
     * 默认fullSpatialInfo指定的尺寸为 tilesize*tilesize
     *
     * @param fullSpatialInfo
     * @param partialSpatialInfo
     * @return
     */
    public static Rectangle getPixelIntersect(SpatialInfo fullSpatialInfo, SpatialInfo partialSpatialInfo) {
        int x = (int) (Math.round(Math.abs((partialSpatialInfo.getUllon() - fullSpatialInfo.getUllon()) / (fullSpatialInfo.getDrlon() - fullSpatialInfo.getUllon())) * TileConsts.tilesize));
        int y = (int) (Math.round(Math.abs((partialSpatialInfo.getUllat() - fullSpatialInfo.getUllat()) / (fullSpatialInfo.getDrlat() - fullSpatialInfo.getUllat())) * TileConsts.tilesize));
        int width = (int) (Math.ceil(Math.abs((partialSpatialInfo.getDrlon() - partialSpatialInfo.getUllon()) / (fullSpatialInfo.getDrlon() - fullSpatialInfo.getUllon())) * TileConsts.tilesize));
        int height = (int) (Math.ceil(Math.abs((partialSpatialInfo.getDrlat() - partialSpatialInfo.getUllat()) / (fullSpatialInfo.getDrlat() - fullSpatialInfo.getUllat())) * TileConsts.tilesize));

        width = ((x + width) > TileConsts.tilesize) ? TileConsts.tilesize - x : width;
        height = ((y + height) > TileConsts.tilesize) ? TileConsts.tilesize - y : height;

        if (x >= 0 && y >= 0 && width > 0 && height > 0 && (x + width) <= TileConsts.tilesize && (y + height) <= TileConsts.tilesize) {
            return new Rectangle(x, y, width, height);
        } else {
            return null;
        }
    }


    /**
     * 判断是否有重叠区域
     *
     * @param transform
     * @param wktProj
     * @param nWidth
     * @param nHeight
     * @param spatialInfo
     * @return 图像像素坐标
     */
    public static Boolean isIntersect(double[] transform, String wktProj, int nWidth, int nHeight, SpatialInfo spatialInfo) {
        Rectangle rectangle1 = new Rectangle(0, 0, nWidth, nHeight);
        Rectangle rectangle2 = SpatialTileHelper.getPixelRectangle(transform, wktProj, spatialInfo);
        return rectangle1.intersects(rectangle2);
    }

    /**
     * 得到由spatialInfo指定的像素坐标
     *
     * @param transform
     * @param wktProj
     * @param spatialInfo
     * @return 图像像素坐标
     */
    public static Rectangle getPixelRectangle(double[] transform, String wktProj, SpatialInfo spatialInfo) {
        double[] dX = new double[]{spatialInfo.getUllon(), spatialInfo.getDrlon(), spatialInfo.getDrlon(), spatialInfo.getUllon()};
        double[] dY = new double[]{spatialInfo.getUllat(), spatialInfo.getUllat(), spatialInfo.getDrlat(), spatialInfo.getDrlat()};

        if (!SpatialTileHelper.isEpsg4326ByWktString(wktProj)) {
            RegistrationUtility.ConvertMapBettewnGeo(dX, dY, wktProj, false);
        }

        double[] pixelX = new double[4];
        double[] pixelY = new double[4];
        for (int i = 0; i < 4; i++) {
            double[] tempPixel = RegistrationUtility.GetPixByTransform(transform, dX[i], dY[i]);
            pixelX[i] = tempPixel[0];
            pixelY[i] = tempPixel[1];
        }

        return new Rectangle((int) ArrayHelper.getMin(pixelX), (int) ArrayHelper.getMin(pixelY), (int) (ArrayHelper.getMax(pixelX) - ArrayHelper.getMin(pixelX)), (int) (ArrayHelper.getMax(pixelY) - ArrayHelper.getMin(pixelY)));
    }

    /**
     * 是否是等经纬度投影
     *
     * @param wktProj
     * @return
     */
    public static Boolean isEpsg4326ByWktString(String wktProj) {
        //判断输入图像的投影是否为4326投影
        SpatialReference sp1 = new SpatialReference(GdalIOUtil.strProjectionLngLat);
        //sp1.ImportFromEPSG(4326);
        SpatialReference sp2 = new SpatialReference();
        sp2.ImportFromWkt(wktProj);
        if (sp1.IsSame(sp2) > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断WKT字符串是否是经纬度投影字符串
     *
     * @param projWKT
     * @return
     */
    public Boolean isEpsh4326(String projWKT) {
        Boolean is4326 = false;
        try {
            SpatialReference srProj = new SpatialReference(projWKT);
            if (srProj.IsGeographic() == 1) {
                is4326 = true;
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return is4326;
    }


}