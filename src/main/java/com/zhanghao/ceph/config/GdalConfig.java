package com.zhanghao.ceph.config;

import com.zhanghao.ceph.Utils.GdalLibLoad;
import org.gdal.gdal.gdal;


public class GdalConfig {

    public static final String GDAL_LIB_PATH = "/lib/gdal";

    public static boolean init(String gdalPath){
        try{
            GdalLibLoad.loadLinuxGdalLib(gdalPath);
            gdal.SetConfigOption("GDAL_FILENAME_IS_GBK","YES");
            gdal.AllRegister();
            return true;
        }catch (Exception e){
            System.out.println("gdal加载失败");
            return false;
        }
    }
}
