package com.zhanghao.ceph.Utils;

import java.io.File;

public class GdalLibLoad {
    public static void loadLinuxGdalLib(String libpath){
        System.load(libpath+ File.separator+"centos64"+File.separator+"libgdal.so");
        System.load(libpath+ File.separator+"centos64"+File.separator+"libgdaljni.so");
        System.load(libpath+ File.separator+"centos64"+File.separator+"libgdalconstjni.so");
        System.load(libpath+ File.separator+"centos64"+File.separator+"libogrjni.so");
        System.load(libpath+ File.separator+"centos64"+File.separator+"libosrjni.so");
    }
}
