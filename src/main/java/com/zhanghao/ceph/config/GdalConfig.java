package com.zhanghao.ceph.config;

import com.zhanghao.ceph.Utils.GdalLibLoad;
import org.gdal.gdal.gdal;


public class GdalConfig {

    public static final String GDAL_LIB_PATH = "/lib/gdal";

    public static boolean init(){
        try{
            GdalLibLoad.loadLinuxGdalLib(GDAL_LIB_PATH);
            gdal.SetConfigOption("GDAL_FILENAME_IS_GBK","YES");
            gdal.AllRegister();
            return true;
        }catch (Exception e){
            System.out.println("gdal加载失败");
            return false;
        }
    }
}
