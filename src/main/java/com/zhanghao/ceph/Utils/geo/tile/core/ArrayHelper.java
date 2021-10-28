package com.zhanghao.ceph.Utils.geo.tile.core;


import java.awt.*;
import java.util.List;

/**
 * Created by Lenovo on 2021/10/23.
 */
public class ArrayHelper {

    /**
     * 判断数组内容是否全部相等
     *
     * @param array
     * @return
     */
    public static Boolean isAllEqual(Integer[] array) {
        int data = array[0];
        for (int i = 1; i < array.length; i++) {
            if (data != array[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取数组
     *
     * @param num
     * @return
     */
    public static Integer[] getArrayByNum(Integer num) {
        Integer[] array = new Integer[num];
        for (int i = 0; i < num; i++) {
            array[i] = i;
        }
        return array;
    }

    /**
     * 获取数组中最大值
     *
     * @param array
     * @return
     */
    public static Integer getMax(Integer[] array) {
        Integer max = 1;
        for (int i = 0; array != null && i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * 获取数组中最大值
     *
     * @param array
     * @return
     */
    public static double getMax(double[] array) {
        double max = Double.MIN_VALUE;
        for (int i = 0; array != null && i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * 获取数组最小值
     *
     * @param array
     * @return
     */
    public static double getMin(double[] array) {
        double min = Double.MAX_VALUE;
        for (int i = 0; array != null && i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }


    /**
     * 内存对齐
     * 将xSizeBuf、ySizeBuf指定的内存array调整为xOffDst、yOffDst、xSizeDst、ySizeDst指定的array
     * 参数必须满足xSizeDst>(xSizeBuf+xOffDst)，ySizeDst>(ySizeBuf+yOffDst)
     *
     * @param array
     * @param xSizeBuf
     * @param ySizeBuf
     * @param xSizeDst
     * @param ySizeDst
     * @return
     */
    public static int[] adjustMemory(int[] array, int xSizeBuf, int ySizeBuf, int xOffDst, int yOffDst, int xSizeDst, int ySizeDst) {
        int[] arrayDst = new int[xSizeDst * ySizeDst];
        for (int i = 0; i < ySizeBuf; i++) {
            for (int j = 0; j < xSizeBuf; j++) {
                arrayDst[(i + yOffDst) * xSizeDst + j + xOffDst] = array[i * xSizeBuf + j];
            }
        }
        return arrayDst;
    }

    /**
     * 内存对齐
     * 将xSizeBuf、ySizeBuf指定的内存array调整为xOffDst、yOffDst、xSizeDst、ySizeDst指定的array
     * 参数必须满足xSizeDst>(xSizeBuf+xOffDst)，ySizeDst>(ySizeBuf+yOffDst)
     *
     * @param srcBuffer
     * @param xSizeBuf
     * @param ySizeBuf
     * @param xSizeDst
     * @param ySizeDst
     * @return
     */
    public static void adjustMemory(short[] srcBuffer, int xSizeBuf, int ySizeBuf, short[] dstBuffer, int xOffDst, int yOffDst, int xSizeDst, int ySizeDst) {
        for (int i = 0; i < ySizeBuf; i++) {
            for (int j = 0; j < xSizeBuf; j++) {
                dstBuffer[(i + yOffDst) * xSizeDst + j + xOffDst] = srcBuffer[i * xSizeBuf + j];
            }
        }
    }

    /**
     * 内存对齐
     * 将xSizeBuf、ySizeBuf指定的内存array调整为xOffDst、yOffDst、xSizeDst、ySizeDst指定的array
     * 参数必须满足xSizeDst>(xSizeBuf+xOffDst)，ySizeDst>(ySizeBuf+yOffDst)
     *
     * @param srcBuffer
     * @param xSizeBuf
     * @param ySizeBuf
     * @param xSizeDst
     * @param ySizeDst
     * @return
     */
    public static void adjustMemory(byte[] srcBuffer, int xSizeBuf, int ySizeBuf, byte[] dstBuffer, int xOffDst, int yOffDst, int xSizeDst, int ySizeDst) {
        for (int i = 0; i < ySizeBuf; i++) {
            for (int j = 0; j < xSizeBuf; j++) {
                dstBuffer[(i + yOffDst) * xSizeDst + j + xOffDst] = srcBuffer[i * xSizeBuf + j];
            }
        }
    }

    /**
     * 内存对齐
     * 将xSizeBuf、ySizeBuf指定的内存array调整为xOffDst、yOffDst、xSizeDst、ySizeDst指定的array
     * 参数必须满足xSizeDst>(xSizeBuf+xOffDst)，ySizeDst>(ySizeBuf+yOffDst)
     *
     * @param srcBuffer
     * @param xSizeBuf
     * @param ySizeBuf
     * @param xSizeDst
     * @param ySizeDst
     * @return
     */
    public static byte[] adjustMemory(byte[] srcBuffer, int xSizeBuf, int ySizeBuf, int xOffDst, int yOffDst, int xSizeDst, int ySizeDst) {
        byte[] dstBuffer = new byte[xSizeDst * ySizeDst];
        for (int i = 0; i < ySizeBuf; i++) {
            for (int j = 0; j < xSizeBuf; j++) {
                dstBuffer[(i + yOffDst) * xSizeDst + j + xOffDst] = srcBuffer[i * xSizeBuf + j];
            }
        }
        return dstBuffer;
    }


    /**
     * 判断内存块是否全部为0
     *
     * @param bufByte
     * @return
     */
    public static Boolean isZeroBuffer(byte[] bufByte) {
        long sum = 0;
        for (byte buf :
                bufByte) {
            sum += buf;
        }
        if (sum == 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 内存复制
     *
     * @param bytes
     * @param bufR
     * @param bufG
     * @param bufB
     * @param rectangle
     */
    public static void copyMemory(List<byte[]> bytes, byte[] bufR, byte[] bufG, byte[] bufB, Rectangle rectangle) {
        for (int i = 0; i < rectangle.height; i++) {
            for (int j = 0; j < rectangle.width; j++) {
                //     if (bytes.get(0)[i * rectangle.width + j] != 0) { //
                bufR[(i + rectangle.y) * TileConsts.tilesize + rectangle.x + j] = bytes.get(0)[i * rectangle.width + j];
                bufG[(i + rectangle.y) * TileConsts.tilesize + rectangle.x + j] = bytes.get(1)[i * rectangle.width + j];
                bufB[(i + rectangle.y) * TileConsts.tilesize + rectangle.x + j] = bytes.get(2)[i * rectangle.width + j];
                //       }
            }
        }
    }


    /**
     * 从内存中取出指定大小的小块数据并装配到指定大小的内存区间中
     *
     * @param srcBytes     原数据
     * @param xSizeSrc     原数据的X尺寸
     * @param ySizeSrc     原数据的Y尺寸
     * @param xOffSrc      原始数据的起读点X偏移
     * @param yOffSrc      原始数据的起读点Y偏移
     * @param xSizeDst     存放结果数据的X尺寸
     * @param ySizeDst     存放结果数据的Y尺寸
     * @param xOffDst      写结果数据的起写点X偏移
     * @param yOffDst      写结果数据的起写点Y偏移
     * @param xSizeReadDst 实际读取的数据宽度
     * @param ySizeReadDst 实际读取的数据高度
     * @return
     */
    public static byte[] getByteBuffer(byte[] srcBytes, int xSizeSrc, int ySizeSrc, int xOffSrc, int yOffSrc, int xSizeDst, int ySizeDst, int xOffDst, int yOffDst, int xSizeReadDst, int ySizeReadDst) {
        byte[] dstBytes = new byte[xSizeDst * ySizeDst];

        for (int y = 0; y < ySizeReadDst; y++) {
            for (int x = 0; x < xSizeReadDst; x++) {
                dstBytes[(y + yOffDst) * xSizeDst + xOffDst + x] = srcBytes[(yOffSrc + y) * xSizeSrc + xOffSrc + x];
            }
        }

        return dstBytes;
    }
}
