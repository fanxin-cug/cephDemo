package com.zhanghao.ceph.controller;

import com.ceph.fs.CephStat;
import com.zhanghao.ceph.Utils.geo.tile.mem.MemImage;
import com.zhanghao.ceph.Utils.geo.tile.mem.MemImageHelper;
import com.zhanghao.ceph.service.CephService;
import io.swagger.annotations.ApiOperation;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.Date;

/**
 * 测试gdal
 */

@Controller
@RequestMapping("/gdal")
@ResponseBody
public class GdalController {

    @ApiOperation(value = "Gdaltest1", notes = "gdal读取文件时间")
    @RequestMapping(value = "/gdalTest1", method = RequestMethod.GET)
    @ResponseBody
    public String gdalTest1(String fileName) {
        Date startDate = new Date();
        //读文件
        gdal.AllRegister();
        File tifFile = new File(fileName);
        Dataset dataset = gdal.Open(tifFile.toString(), gdalconstConstants.GA_ReadOnly);
        if (null != dataset) {
            return "读取失败";
        } else {
            System.out.println(dataset.GetGeoTransform());
            dataset.delete();
            return "用时:" + (new Date().getTime() - startDate.getTime()) + " ms";
        }
    }

    @ApiOperation(value = "Gdaltest2", notes = "gdal读写测试")
    @RequestMapping(value = "/gdalTest2", method = RequestMethod.GET)
    @ResponseBody
    public String gdalTest2(String fileName) {
        String newFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_new." + fileName.substring(fileName.lastIndexOf("."));
        Date startDate = new Date();
        //读文件
        MemImage memImage = MemImageHelper.readToMemImage(fileName);
        MemImageHelper.writeToDisk(memImage, newFileName);
        return "用时:" + (new Date().getTime() - startDate.getTime()) + " ms";
    }


}