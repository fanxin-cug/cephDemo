package com.zhanghao.ceph.Utils.geo.data.test;

import lombok.Data;


/**
 * Created by Lenovo on 2021/3/29.
 */
@Data
public class BigData {

    protected String fileName;

    protected String md5;

    protected String path;

    protected String path2;

    protected String path3;

    protected byte[] buffer;

    public static String transToCreateSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table tradi(");
        stringBuilder.append("fileName text not null,");
        stringBuilder.append("md5 text not null,");
        stringBuilder.append("path1 text not null,");
        stringBuilder.append("path2 text not null,");
        stringBuilder.append("path3 text not null,");
        stringBuilder.append("buffer blob not null");
        stringBuilder.append(");");
        return stringBuilder.toString();
    }


    public static BigData CreateBigData(int index, byte[] buffer) {
        BigData metaData = new BigData();
        metaData.setFileName("fileName.fileName.fileName.fileName.fileName" + index);
        metaData.setMd5("01234567890123456789012345678901234567890123456789");
        metaData.setPath("fileName/fileName/fileName/fileName/fileName/fileName");
        metaData.setPath2("fileName/fileName/fileName/fileName/fileName/fileName2");
        metaData.setPath3("fileName/fileName/fileName/fileName/fileName/fileName3");
        metaData.setBuffer(buffer);
        return metaData;
    }
}
