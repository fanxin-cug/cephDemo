package com.zhanghao.ceph.Utils.geo.tile.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2021/9/3
 */
public class Lookup4326 {
    public final static List<Double> Epsg4326DegreeList = new ArrayList<Double>() {
        {
            add(0.703125);
            add(0.3515625);
            add(0.17578125);
            add(0.087890625);
            add(0.0439453125);
            add(0.02197265625);
            add(0.010986328125);
            add(0.0054931640625);
            add(0.00274658203125);
            add(0.001373291015625);
            add(0.0006866455078125);
            add(0.00034332275390625);
            add(0.000171661376953125);
            add(0.0000858306884765625);
            add(0.00004291534423828125);
            add(0.000021457672119140625);    // 16层约2米
            add(0.0000107288360595703125);   // 17
            add(0.00000536441802978515625);
            add(0.000002682209014892578125);
            add(0.0000013411045074462890625);
            add(0.00000067055225372314453125);  // 21
        }
    };

    /**
     * 获取单景影像切片的最大层级
     *
     * @param resolution 分辨率（单位度）
     * @return
     */
    public static int getMaxLevel(Double resolution) {
        int level = 1;
        // 分辨率大于1级或者小于21级
        if (resolution > 0.703125) {
            // 分辨率大于1级或者小于21级
            level = 1;
        } else if (resolution < 0.00000067055225372314453125) {
            // 分辨率大于1级或者小于21级
            level = 21;
        } else {
            for (int i = 1; i <= 21; i++) {
                int n = 1 << i;
                double tileWidth = 360.0D / n;
                double levelResolution = tileWidth / 256;
                if (resolution >= levelResolution) {
                    if (resolution > levelResolution * 3 / 2) {
                        level = i - 1;
                        break;
                    } else {
                        level = i;
                    }

                }
            }
        }
        return level;
    }
}


