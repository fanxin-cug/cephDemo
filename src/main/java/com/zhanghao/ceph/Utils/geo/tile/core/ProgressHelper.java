package com.zhanghao.ceph.Utils.geo.tile.core;

/**
 * 进度条
 */
public class ProgressHelper {
    /**
     * 是否输出详细的日志信息
     */
    public final static Boolean isPrintDetailedLog = false;

    /**
     * 打印进度 至少20等份
     *
     * @param off        进度量
     * @param size       总量
     * @param keyword    关键字
     * @param totalCount 总份数
     */
    public static void printProgress(int off, int size, String keyword, int totalCount) {
        if (totalCount < 20) {
            totalCount = 20;
        }
        int[] mark = new int[totalCount];
        for (int i = 0; i < totalCount; i++) {
            mark[i] = (int) ((i * 1.0 / totalCount) * size);
        }

        for (int i = 0; i < totalCount; i++) {
            if (mark[i] == off) {
                System.out.println(keyword + " progress:" + i * 100.0 / totalCount + "%");
                break;
            }
        }
    }

}
