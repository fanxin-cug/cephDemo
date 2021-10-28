package com.zhanghao.ceph.controller;

import com.ceph.fs.CephStat;
import com.zhanghao.ceph.service.CephService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/ceph")
@ResponseBody
public class CephController {

    @Autowired
    private CephService cephfsService = new CephService();


    @ApiOperation(value = "Mount", notes = "Mount")
    @RequestMapping(value = "/mount", method = RequestMethod.GET)
    @ResponseBody
    public Boolean mountCephFsByRoot(){
        return cephfsService.mountCephFsByRoot();
    }

    @ApiOperation(value = "Unmount", notes = "Unmount")
    @RequestMapping(value = "/unmount", method = RequestMethod.GET)
    @ResponseBody
    public Boolean unmountCephFs(){
        return cephfsService.unmountCephFs();
    }

    @ApiOperation(value = "CreateDir", notes = "CreateDir")
    @RequestMapping(value = "/createdir", method = RequestMethod.POST)
    @ResponseBody
    public String[] createDirByPath(@RequestParam(value = "DirPath") String path){
        return cephfsService.createDirByPath(path);
    }

    @ApiOperation(value = "DeleteDir", notes = "DeleteDir")
    @RequestMapping(value = "/deletedir", method = RequestMethod.DELETE)
    @ResponseBody
    public String[] deleteDirByPath(@RequestParam(value = "DirPath") String path){
        return cephfsService.deleteDirByPath(path);
    }

    @ApiOperation(value = "FileStatus", notes = "FileStatus")
    @RequestMapping(value = "/getfilestatus", method = RequestMethod.GET)
    @ResponseBody
    public CephStat getFileStatusByPath(@RequestParam(value = "DirPath") String path){
        return cephfsService.getFileStatusByPath(path);
    }

    @ApiOperation(value = "FileContext", notes = "FileContext")
    @RequestMapping(value = "/getfilecontext", method = RequestMethod.GET)
    @ResponseBody
    public String readFileByPath(@RequestParam(value = "DirPath") String path){
        return cephfsService.readFileByPath(path);
    }

    @ApiOperation(value = "FileUpload", notes = "FileUpload")
    @RequestMapping(value = "/fileupload", method = RequestMethod.POST)
    @ResponseBody
    public Boolean uploadFileByPath(@RequestParam(value = "FilePath") String filepath, @RequestParam(value = "FileName") String filename){
        return cephfsService.uploadFileByPath(filepath, filename);
    }

    @ApiOperation(value = "FileDownload", notes = "FileDownload")
    @RequestMapping(value = "/filedownload", method = RequestMethod.GET)
    @ResponseBody
    public Boolean downloadFileByPath(@RequestParam(value = "FileName") String filename, @RequestParam(value = "SavePath") String savepath){
        return cephfsService.downloadFileByPath(filename, savepath);
    }

}