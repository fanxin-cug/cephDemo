package com.zhanghao.ceph.Utils.geo.tile.mem;


public class Size {
    public int width;
    public int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Size() {

    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
