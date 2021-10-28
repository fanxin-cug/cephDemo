package com.zhanghao.ceph.Utils.geo.data.test;

import java.util.ArrayList;
import java.util.List;

public class BigDataTest {

    public static void main(String[] args) throws Exception {
//        insertData(20 * 1000);
        fetchData();
    }

    public static void fetchData() {
        {
            long time1 = new java.util.Date().getTime();
            List<BigData> bigDatas = BigDataDb.fetchBigData();
            long time2 = new java.util.Date().getTime();
            System.out.println("出库" + bigDatas.size() + "个元数据记录，耗时:" + (time2 - time1) / 1000 + "秒");
            System.out.println(bigDatas.size());
        }
    }

    public static void insertData(int count) {
        List<BigData> bigDatas = new ArrayList<>();
        {
            long time1 = new java.util.Date().getTime();
            byte[] buffer = new byte[count];
            for (int i = 0; i < count; i++) {
                bigDatas.add(BigData.CreateBigData(i, buffer));
            }
            long time2 = new java.util.Date().getTime();
            System.out.println("生成" + count + "个元数据记录，耗时:" + (time2 - time1) / 1000 + "秒");
        }

        {
            long time1 = new java.util.Date().getTime();
            for (int i = 0; i < 5; i++) {
                BigDataDb.storeBigData(bigDatas);
            }
            long time2 = new java.util.Date().getTime();
            System.out.println("入库" + count + "个元数据记录，耗时:" + (time2 - time1) / 1000 + "秒");
        }
    }
}
