package com.zhanghao.ceph.Utils.geo.data.test;


import java.util.ArrayList;
import java.util.List;

public class MeteDataTest {

    public static void main(String[] args) throws Exception {
//        insertData(1000 * 1000);
        fetchData();
    }

    public static void fetchData() {
        {
            long time1 = new java.util.Date().getTime();
            List<MetaData> metaDatas = MetaDataDb.fetchMetaData();
            long time2 = new java.util.Date().getTime();
            System.out.println("出库" + metaDatas.size() + "个元数据记录，耗时:" + (time2 - time1) / 1000 + "秒");
        }
    }

    public static void insertData(int count) {
        List<MetaData> metaDatas = new ArrayList<>();
        {
            long time1 = new java.util.Date().getTime();
            for (int i = 0; i < count; i++) {
                metaDatas.add(MetaData.CreateMetaData(i));
            }
            long time2 = new java.util.Date().getTime();
            System.out.println("生成" + count + "个元数据记录，耗时:" + (time2 - time1) / 1000 + "秒");
        }

        {
            long time1 = new java.util.Date().getTime();
            MetaDataDb.storeMetaData(metaDatas);
            long time2 = new java.util.Date().getTime();
            System.out.println("入库" + count + "个元数据记录，耗时:" + (time2 - time1) / 1000 + "秒");
        }
    }
}
