package com.zhanghao.ceph.service;

import com.ceph.fs.CephMount;
import com.ceph.fs.CephStat;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

@Service
public class CephService {

    private CephMount mount=null;

    private Logger logger=Logger.getLogger("CephService");

    public Boolean mountCephFsByRoot(){
        try {
            this.mount = new CephMount("admin");
            this.mount.conf_set("mon_host", "10.170.66.63;10.170.66.88;10.170.66.98");
            //System.out.println(mount.conf_get("mon_host"));
            this.mount.conf_set("key","AQBko3dhfCO9GxAA7V9mcBbrTMZhuOc5UuKrsg==");
            this.mount.mount("/");
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public Boolean unmountCephFs(){
        try {
            mount.unmount();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    protected void finalize( )
    {
        this.unmountCephFs();
    }

    public String[] createDirByPath(String path){
        this.mountCephFsByRoot();
        String[] dirList = null;
        try {
            if (this.mount == null){
                return null;
            }
            this.mount.mkdirs(path, 0777);
            dirList = this.mount.listdir("/");
            return dirList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public String[] deleteDirByPath(String path){
        this.mountCephFsByRoot();
        String[] dirList = null;
        try {
            if (this.mount == null){
                return null;
            }
            this.mount.rmdir(path);
            dirList = this.mount.listdir("/");
            return dirList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public CephStat getFileStatusByPath(String path){
        this.mountCephFsByRoot();
        CephStat stat = new CephStat();
        try {
            if (this.mount == null){
                return null;
            }
            this.mount.lstat(path, stat);
            return stat;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String readFileByPath(String path){
        this.mountCephFsByRoot();
        CephStat stat = new CephStat();
        String context=null;
        try {
            if (this.mount == null){
                return null;
            }
            int fd = this.mount.open(path, CephMount.O_RDONLY, 0);
            this.mount.fstat(fd, stat);
            byte[] buffer = new byte[(int)stat.size];
            this.mount.read(fd, buffer, stat.size, 0);
            context = new String(buffer);
            this.mount.close(fd);
            return context;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Boolean uploadFileByPath(String filePath, String fileName){
        this.mountCephFsByRoot();
        // exit with null if not mount
        if (this.mount == null){
            logger.info("Ceph fs not mount!");
            return null;
        }

        // file definition
        char pathChar = File.separatorChar;
        String fileFullName = "";
        Long fileLength = 0l;
        Long uploadedLength = 0l;
        File file = null;

        // Io
        FileInputStream fis = null;

        // get local file info
        fileFullName = filePath + pathChar + fileName;
        file = new File(fileFullName);
        if (!file.exists()){
            logger.info("File not exist!");
            return false;
        }
        fileLength = file.length();

        // get io from local file
        try {
            fis = new FileInputStream(file);
        }catch (FileNotFoundException e){
            logger.info("Read local file failed!");
            e.printStackTrace();
        }

        // if file exists or not
        String[] dirList = null;
        Boolean fileExist = false;
        try {
            dirList = this.mount.listdir("/");
            for (String fileInfo : dirList){
                if (fileInfo.equals(fileName)){
                    fileExist = true;
                }
            }
        }catch (FileNotFoundException e){
            logger.info("File Path not exist!");
            e.printStackTrace();
        }

        // transfer file by diff pattern
        if (!fileExist){
            try {
                // create file and set mode WRITE
                this.mount.open(fileName, CephMount.O_CREAT, 0);
                int fd = this.mount.open(fileName, CephMount.O_RDWR, 0);

                // start transfer
                int length = 0;
                byte[] bytes = new byte[1024];
                while ((length = fis.read(bytes, 0, bytes.length)) != -1){
                    // write
                    this.mount.write(fd, bytes, length, uploadedLength);

                    // update length
                    uploadedLength += length;

                    // output transfer rate
                    float rate = (float)uploadedLength * 100 / (float)fileLength;
                    String rateValue = (int)rate + "%";
                    System.out.println(rateValue);

                    // complete flag
                    if (uploadedLength == fileLength){
                        break;
                    }
                }
                System.out.println("文件传输成功！");

                // chmod
                this.mount.fchmod(fd, 0666);

                // close
                this.mount.close(fd);
                if (fis != null){
                    fis.close();
                }
                return true;
            }catch (Exception e){
                logger.info("File transfer failed!");
                e.printStackTrace();
            }
        }else if (fileExist){
            try {
                // get file length
                CephStat stat = new CephStat();
                this.mount.stat(fileName, stat);
                uploadedLength = stat.size;
                int fd = this.mount.open(fileName, CephMount.O_RDWR, 0);

                // start transfer
                int length = 0;
                byte[] bytes = new byte[1024];
                fis.skip(uploadedLength);
                while ((length = fis.read(bytes, 0, bytes.length)) != -1){
                    // write
                    this.mount.write(fd, bytes, length, uploadedLength);

                    // update length
                    uploadedLength += length;

                    // output transfer rate
                    float rate = (float)uploadedLength * 100 / (float)fileLength;
                    String rateValue = (int)rate + "%";
                    System.out.println(rateValue);

                    // complete flag
                    if (uploadedLength == fileLength){
                        break;
                    }
                }
                System.out.println("断点文件传输成功！");

                // chmod
                this.mount.fchmod(fd, 0666);

                // close
                this.mount.close(fd);
                if (fis != null){
                    fis.close();
                }
                return true;
            }catch (Exception e){
                logger.info("BreakPoint transfer failed!");
                e.printStackTrace();
            }
        }else {
            try {
                if (fis != null){
                    fis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    public Boolean downloadFileByPath(String fileName, String savePath) {
        this.mountCephFsByRoot();
        // exit with null if not mount
        if (this.mount == null) {
            logger.info("Ceph fs not mount!");
            return null;
        }

        // file definition
        char pathChar = File.separatorChar;
        String fileFullName = "";
        Long fileLength = 0l;
        Long downloadedLength = 0l;
        File file = null;

        // IO
        FileOutputStream fos = null;
        RandomAccessFile raf = null;

        // new file object
        fileFullName = savePath + pathChar + fileName;
        file = new File(fileFullName);

        // get cephfs file size
        try {
            CephStat stat = new CephStat();
            this.mount.stat(fileName, stat);
            fileLength = stat.size;
        } catch (Exception e) {
            logger.info("Fail to get file size.");
            e.printStackTrace();
        }

        if (fileLength != 0) {
            if (!file.exists()) {
                // download file
                int length = 10240;
                byte[] bytes = new byte[length];
                try {
                    int fd = this.mount.open(fileName, CephMount.O_RDONLY, 0);
                    fos = new FileOutputStream(file);
                    float rate = 0;
                    String rateValue = "";
                    while ((fileLength - downloadedLength) >= length && (this.mount.read(fd, bytes, (long) length, downloadedLength)) != -1) {
                        fos.write(bytes, 0, length);
                        fos.flush();
                        downloadedLength += (long) length;

                        // output transfer rate
                        rate = (float) downloadedLength * 100 / (float) fileLength;
                        rateValue = (int) rate + "%";
                        System.out.println(rateValue);

                        if (downloadedLength == fileLength) {
                            break;
                        }
                    }
                    if (downloadedLength != fileLength) {
                        this.mount.read(fd, bytes, fileLength - downloadedLength, downloadedLength);
                        fos.write(bytes, 0, (int) (fileLength - downloadedLength));
                        fos.flush();
                        downloadedLength = fileLength;

                        // output transfer rate
                        rate = (float) downloadedLength * 100 / (float) fileLength;
                        rateValue = (int) rate + "%";
                        System.out.println(rateValue);
                    }

                    System.out.println("Download Success!");
                    fos.close();
                    this.mount.close(fd);
                    return true;
                } catch (Exception e) {
                    logger.info("First download fail!");
                    e.printStackTrace();
                }
            } else if (file.exists()) {
                // download file
                int length = 10240;
                byte[] bytes = new byte[length];
                Long filePoint = file.length();
                try {
                    int fd = this.mount.open(fileName, CephMount.O_RDONLY, 0);
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(filePoint);
                    downloadedLength = filePoint;
                    float rate = 0;
                    String rateValue = "";
                    while ((fileLength - downloadedLength) >= length && (this.mount.read(fd, bytes, (long) length, downloadedLength)) != -1) {
                        raf.write(bytes, 0, length);
                        downloadedLength += (long) length;

                        // output transfer rate
                        rate = (float) downloadedLength * 100 / (float) fileLength;
                        rateValue = (int) rate + "%";
                        System.out.println(rateValue);

                        if (downloadedLength == fileLength) {
                            break;
                        }
                    }
                    if (downloadedLength != fileLength) {
                        this.mount.read(fd, bytes, fileLength - downloadedLength, downloadedLength);
                        raf.write(bytes, 0, (int) (fileLength - downloadedLength));
                        downloadedLength = fileLength;

                        // output transfer rate
                        rate = (float) downloadedLength * 100 / (float) fileLength;
                        rateValue = (int) rate + "%";
                        System.out.println(rateValue);
                    }

                    System.out.println("Cut Point Download Success!");
                    raf.close();
                    this.mount.close(fd);
                    return true;
                } catch (Exception e) {
                    logger.info("Continue download fail!");
                    e.printStackTrace();
                }
            } else {
                logger.info("Unknown Error!");
                return false;
            }
        }
        return false;
    }
}
