package com.zhanghao.ceph;

import com.zhanghao.ceph.config.GdalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        for(String arg : args){
            if(arg.contains("--gdal")){
                String gdalPath = arg.substring(arg.indexOf("=")+1);
                GdalConfig.init(gdalPath);
            }
        }
        SpringApplication.run(Application.class,args);
    }
}
