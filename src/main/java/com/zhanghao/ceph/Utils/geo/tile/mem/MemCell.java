package com.zhanghao.ceph.Utils.geo.tile.mem;

import java.awt.*;

/**
 * Created by Lenovo on 2021/8/26.
 */
public class MemCell {
    /**
     * 数据块的宽度
     */
    private int nWidth;

    /**
     * 数据块的高度
     */
    private int nHeight;


    /**
     * 数据块
     */
    private short[] shortBufferArray = null;

    /**
     * 数据块
     */
    private byte[] byteBufferArray = null;

    /**
     * 波段编号
     */
    private int iBand;


    /**
     * 分块编号
     */
    private int iChunk;

    public int getnWidth() {
        return nWidth;
    }

    public void setnWidth(int nWidth) {
        this.nWidth = nWidth;
    }

    public int getnHeight() {
        return nHeight;
    }

    public void setnHeight(int nHeight) {
        this.nHeight = nHeight;
    }

    public short[] getShortBufferArray() {
        return shortBufferArray;
    }

    public void setShortBufferArray(short[] shortBufferArray) {
        this.shortBufferArray = shortBufferArray;
    }

    public byte[] getByteBufferArray() {
        return byteBufferArray;
    }

    public void setByteBufferArray(byte[] byteBufferArray) {
        this.byteBufferArray = byteBufferArray;
    }

    public int getiBand() {
        return iBand;
    }

    public void setiBand(int iBand) {
        this.iBand = iBand;
    }

    public int getiChunk() {
        return iChunk;
    }

    public void setiChunk(int iChunk) {
        this.iChunk = iChunk;
    }

    public MemCell(int nWidth, int nHeight, int iBand, int iChunk, short[] intArray) {
        this.nWidth = nWidth;
        this.nHeight = nHeight;
        this.shortBufferArray = intArray;
        this.iBand = iBand;
        this.iChunk = iChunk;
    }

    public MemCell(int nWidth, int nHeight, int iBand, int iChunk, byte[] intArray) {
        this.nWidth = nWidth;
        this.nHeight = nHeight;
        this.byteBufferArray = intArray;
        this.iBand = iBand;
        this.iChunk = iChunk;
    }


    /**
     * 获取由readRect指定的
     * @param readRect
     * @param buffer
     * @param yOffBuffer
     */
    public void getMemBuffer(Rectangle readRect, short[] buffer, int yOffBuffer) {
        for (int iHeight = 0; iHeight < readRect.height; iHeight++) {
            System.arraycopy(this.shortBufferArray, (readRect.y + iHeight) * this.nWidth + readRect.x, buffer, readRect.width * (iHeight + yOffBuffer), readRect.width);
        }
    }

    /**
     * 获取由readRect指定的
     * @param readRect
     * @param buffer
     * @param yOffBuffer
     */
    public void getMemBuffer(Rectangle readRect, byte[] buffer, int yOffBuffer) {
        for (int iHeight = 0; iHeight < readRect.height; iHeight++) {
            System.arraycopy(this.byteBufferArray, (readRect.y + iHeight) * this.nWidth + readRect.x, buffer, readRect.width * (iHeight + yOffBuffer), readRect.width);
        }
    }

    /**
     * 存储由readRect指定的
     * @param buffer
     * @param readRect
     * @param yOffBuffer
     */
    public void setMemBuffer(short[] buffer, Rectangle readRect, int yOffBuffer) {
        for (int iHeight = 0; iHeight < readRect.height; iHeight++) {
            System.arraycopy(buffer, readRect.width * (iHeight + yOffBuffer), this.shortBufferArray, (readRect.y + iHeight) * this.nWidth + readRect.x, readRect.width);
        }
    }

    /**
     * 存储由readRect指定的
     * @param buffer
     * @param readRect
     * @param yOffBuffer
     */
    public void setMemBuffer(byte[] buffer, Rectangle readRect, int yOffBuffer) {
        for (int iHeight = 0; iHeight < readRect.height; iHeight++) {
            System.arraycopy(buffer, readRect.width * (iHeight + yOffBuffer), this.byteBufferArray, (readRect.y + iHeight) * this.nWidth + readRect.x, readRect.width);
        }
    }
}
