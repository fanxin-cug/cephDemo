package com.zhanghao.ceph.Utils.geo.tile.mem;


import com.zhanghao.ceph.Utils.geo.UtilImageInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lenovo on 2021/8/26.
 */
public class MemImage {

    /**
     * 图像数据列表<波段号，<分块序号，数据块>>
     */
    private List<MemCell> memCells = null;

    /**
     * 图像的宽度
     */
    private int nWidth;


    /**
     * 图像的高度
     */
    private int nHeight;


    /**
     * 图像波段数，即逻辑上需要的波段数
     */
    private int nBand;

    /**
     * 内存图像是否只保存1个波段数据
     */
    private Boolean isOnlyOneMemBand;

    /**
     * 分块数
     */
    private int nChunkCount;


    /**
     * 分块的行数（高度）
     */
    private int nPerChunk;

    /**
     * 层级(剖分时用)
     */
    private int level;

    /**
     * 扩展参数
     */
    private UtilImageInfo utilImageInfo;


    /**
     * 数据类型 1：byte;2:short;
     */
    private int dataType;

    public List<MemCell> getMemCells() {
        return memCells;
    }

    public void setMemCells(List<MemCell> memCells) {
        this.memCells = memCells;
    }

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

    public int getnBand() {
        return nBand;
    }

    public void setnBand(int nBand) {
        this.nBand = nBand;
    }

    public Boolean getOnlyOneMemBand() {
        return isOnlyOneMemBand;
    }

    public void setOnlyOneMemBand(Boolean onlyOneMemBand) {
        isOnlyOneMemBand = onlyOneMemBand;
    }

    public int getnChunkCount() {
        return nChunkCount;
    }

    public void setnChunkCount(int nChunkCount) {
        this.nChunkCount = nChunkCount;
    }

    public int getnPerChunk() {
        return nPerChunk;
    }

    public void setnPerChunk(int nPerChunk) {
        this.nPerChunk = nPerChunk;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public UtilImageInfo getUtilImageInfo() {
        return utilImageInfo;
    }

    public void setUtilImageInfo(UtilImageInfo utilImageInfo) {
        this.utilImageInfo = utilImageInfo;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public MemImage(int nWidth, int nHeight, int nBand, Boolean isOnlyOneMemBand, int nChunkCount, int nPerChunk, int level, int dataType) {
        this.nBand = nBand;
        this.nChunkCount = nChunkCount;
        this.nWidth = nWidth;
        this.nHeight = nHeight;
        this.level = level;
        this.nPerChunk = nPerChunk;
        this.dataType = dataType;
        this.isOnlyOneMemBand = isOnlyOneMemBand;
    }


    public void setMemCell(MemCell memCell) {
        if (this.memCells == null) {
            this.memCells = new ArrayList<>();
        }
        this.memCells.add(memCell);
    }

    public MemCell getMemCell(int iBand, int iChunk) {
        if (this.memCells != null) {
            for (MemCell memCell : this.memCells) {
                if (memCell.getiBand() == iBand && memCell.getiChunk() == iChunk) {
                    return memCell;
                }
            }
        }
        return null;
    }

    /**
     * 得到实际内存图像的波段索引
     *
     * @param iBand 物理上的波段编号
     * @return
     */
    public int getMemBandIndex(int iBand) {
        if (this.isOnlyOneMemBand) {
            return 0;
        } else {
            return iBand;
        }
    }

    /**
     * 判断某个块是否存在
     *
     * @param iBand
     * @param iChunk
     * @return
     */
    public Boolean isExist(int iBand, int iChunk) {
        if (this.memCells != null) {
            for (MemCell memCell : this.memCells) {
                if (memCell.getiBand() == iBand && memCell.getiChunk() == iChunk) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取16位图像数据
     *
     * @param iBand    波段号 从1开始计数
     * @param readRect 读取的矩形范围
     * @return 矩形范围的8位影像数据
     */
    public short[] getMemShortBuffer(int iBand, Rectangle readRect) {
        int yChunk1 = readRect.y / this.nPerChunk;
        int yChunk2 = (readRect.y + readRect.height) / this.nPerChunk;
        List<MemCell> memCellBand = this.getMemCells(iBand);
        yChunk1 = Math.min(yChunk1, memCellBand.size() - 1);
        yChunk2 = Math.min(yChunk2, memCellBand.size() - 1);

        // 分配内存.
        short[] shortbuffer = new short[readRect.width * readRect.height];
        int yRead = 0;
        for (int i = yChunk1; i <= yChunk2; i++) {
            Rectangle rectNew = new Rectangle();
            rectNew.x = readRect.x;
            rectNew.width = readRect.width;
            if (i == yChunk1 && yChunk1 == yChunk2) // 仅落在一个分块上时
            {
                rectNew.y = readRect.y - i * this.nPerChunk;
                rectNew.height = readRect.height;
            } else if (i == yChunk1) // 落在的第一个分块
            {
                rectNew.y = readRect.y - i * this.nPerChunk;
                rectNew.height = Math.min(this.nPerChunk - rectNew.y, readRect.height);
            } else if (i == yChunk2) // 落在的最后一个分块
            {
                rectNew.y = 0;
                rectNew.height = Math.min(memCellBand.get(i).getnHeight(), readRect.height - yRead);
            } else  // 落在的中间分块
            {
                rectNew.y = 0;
                rectNew.height = Math.min(memCellBand.get(i).getnHeight(), readRect.height - yRead);
            }
            memCellBand.get(i).getMemBuffer(rectNew, shortbuffer, yRead);
            yRead += rectNew.height;
        }

        return shortbuffer;
    }

    /**
     * 获取16位图像数据
     *
     * @param iBand        波段号 从1开始计数
     * @param readRect     读取区域
     * @param bufSize      结果区域
     * @param resampleType 采样方式，1：最近邻；2：双线性
     * @return
     */
    public short[] getMemShortBuffer(int iBand, Rectangle readRect, Size bufSize, double ratio, int resampleType) {
        short[] srcBuffer = this.getMemShortBuffer(iBand, readRect);
        short[] shorts = MemImageHelper.resample(srcBuffer, new Size(readRect.width, readRect.height), bufSize, ratio, resampleType);
        return shorts;
    }


    /**
     * 获取16位图像数据
     *
     * @param bandList     波段列表，波段从1开始计数
     * @param readRect     读取区域
     * @param bufSize      结果区域
     * @param resampleType 采样方式，1：最近邻；2：双线性
     * @return
     */
    public List<short[]> getMemShortBuffer(int[] bandList, Rectangle readRect, Size bufSize, double ratio, int resampleType) {
        List<short[]> dstBuffer = new ArrayList<>();
        for (int i = 0; i < bandList.length; i++) {
            short[] srcBuffer = this.getMemShortBuffer(bandList[i], readRect);
            short[] shorts = MemImageHelper.resample(srcBuffer, new Size(readRect.width, readRect.height), bufSize, ratio, resampleType);
            dstBuffer.add(shorts);
        }
        return dstBuffer;
    }


    /**
     * 获取8位图像数据
     *
     * @param iBand    波段号 从1开始计数
     * @param readRect 读取的矩形范围
     * @return 矩形范围的8位影像数据
     */
    public byte[] getMemByteBuffer(int iBand, Rectangle readRect) {
        int yChunk1 = readRect.y / this.nPerChunk;
        int yChunk2 = (readRect.y + readRect.height) / this.nPerChunk;
        List<MemCell> memCellBand = this.getMemCells(iBand);
        yChunk1 = Math.min(yChunk1, memCellBand.size() - 1);
        yChunk2 = Math.min(yChunk2, memCellBand.size() - 1);

        // 分配内存.
        byte[] bytebuffer = new byte[readRect.width * readRect.height];
        int yRead = 0;
        for (int i = yChunk1; i <= yChunk2; i++) {
            Rectangle rectNew = new Rectangle();
            rectNew.x = readRect.x;
            rectNew.width = readRect.width;
            if (i == yChunk1 && yChunk1 == yChunk2) // 仅落在一个分块上时
            {
                rectNew.y = readRect.y - i * this.nPerChunk;
                rectNew.height = readRect.height;
            } else if (i == yChunk1) // 落在的第一个分块
            {
                rectNew.y = readRect.y - i * this.nPerChunk;
                rectNew.height = Math.min(this.nPerChunk - rectNew.y, readRect.height);
            } else if (i == yChunk2) // 落在的最后一个分块
            {
                rectNew.y = 0;
                rectNew.height = Math.min(memCellBand.get(i).getnHeight(), readRect.height - yRead);
            } else  // 落在的中间分块
            {
                rectNew.y = 0;
                rectNew.height = Math.min(memCellBand.get(i).getnHeight(), readRect.height - yRead);
            }
            memCellBand.get(i).getMemBuffer(rectNew, bytebuffer, yRead);
            yRead += rectNew.height;
        }

        return bytebuffer;
    }


    /**
     * 获取8位图像数据
     *
     * @param iBand        波段号 从1开始计数
     * @param readRect     读取的矩形范围
     * @param bufSize      结果区域
     * @param resampleType 采样方式，1：最近邻；2：双线性
     * @return 矩形范围的8位影像数据
     */
    public byte[] getMemByteBuffer(int iBand, Rectangle readRect, Size bufSize, double ratio, int resampleType) {
        byte[] srcBuffer = this.getMemByteBuffer(iBand, readRect);
        byte[] bytes = MemImageHelper.resample(srcBuffer, new Size(readRect.width, readRect.height), bufSize, ratio, resampleType);
        return bytes;
    }

    /**
     * 获取8位图像数据
     *
     * @param bandList     波段列表，波段从1开始计数
     * @param readRect     读取区域
     * @param bufSize      结果区域
     * @param resampleType 采样方式，1：最近邻；2：双线性
     * @return
     */
    public List<byte[]> getMemByteBuffer(int[] bandList, Rectangle readRect, Size bufSize, double ratio, int resampleType) {
        List<byte[]> dstBuffer = new ArrayList<>();
        for (int i = 0; i < bandList.length; i++) {
            byte[] srcBuffer = this.getMemByteBuffer(bandList[i], readRect);
            byte[] bytes = MemImageHelper.resample(srcBuffer, new Size(readRect.width, readRect.height), bufSize, ratio, resampleType);
            dstBuffer.add(bytes);
        }
        return dstBuffer;
    }


    /**
     * 设置16位图像数据
     *
     * @param writeBuffer 内存图像
     * @param iBand       波段号
     * @param setRect     写入的矩形范围
     */
    public void setMemBuffer(short[] writeBuffer, int iBand, Rectangle setRect) {
        int yChunk1 = setRect.y / this.nPerChunk;
        int yChunk2 = (setRect.y + setRect.height) / this.nPerChunk;
        List<MemCell> memCellBand = this.getMemCells(iBand);
        yChunk1 = Math.min(yChunk1, memCellBand.size() - 1);
        yChunk2 = Math.min(yChunk2, memCellBand.size() - 1);

        int yRead = 0;
        for (int i = yChunk1; i <= yChunk2; i++) {
            Rectangle rectNew = new Rectangle();
            rectNew.x = setRect.x;
            rectNew.width = setRect.width;
            if (i == yChunk1 && yChunk1 == yChunk2) // 仅落在一个分块上时
            {
                rectNew.y = setRect.y - i * this.nPerChunk;
                rectNew.height = setRect.height;
            } else if (i == yChunk1) // 落在的第一个分块
            {
                rectNew.y = setRect.y - i * this.nPerChunk;
                rectNew.height = Math.min(this.nPerChunk - rectNew.y, setRect.height);
            } else if (i == yChunk2) // 落在的最后一个分块
            {
                rectNew.y = 0;
                rectNew.height = Math.min(memCellBand.get(i).getnHeight(), setRect.height - yRead);
            } else  // 落在的中间分块
            {
                rectNew.y = 0;
                rectNew.height = Math.min(memCellBand.get(i).getnHeight(), setRect.height - yRead);
            }
            memCellBand.get(i).setMemBuffer(writeBuffer, rectNew, yRead);

            yRead += rectNew.height;
        }
    }


    /**
     * 设置8位图像数据
     *
     * @param writeBuffer 内存图像
     * @param iBand       波段号，从0开始计数
     * @param setRect     写入的矩形范围
     */
    public void setMemBuffer(byte[] writeBuffer, int iBand, Rectangle setRect) {
        int yChunk1 = setRect.y / this.nPerChunk;
        int yChunk2 = (setRect.y + setRect.height) / this.nPerChunk;
        List<MemCell> memCellBand = this.getMemCells(iBand);
        yChunk1 = Math.min(yChunk1, memCellBand.size() - 1);
        yChunk2 = Math.min(yChunk2, memCellBand.size() - 1);

        int yRead = 0;
        for (int i = yChunk1; i <= yChunk2; i++) {
            Rectangle rectNew = new Rectangle();
            rectNew.x = setRect.x;
            rectNew.width = setRect.width;
            if (i == yChunk1 && yChunk1 == yChunk2) // 仅落在一个分块上时
            {
                rectNew.y = setRect.y - i * this.nPerChunk;
                rectNew.height = setRect.height;
            } else if (i == yChunk1) // 落在的第一个分块
            {
                rectNew.y = setRect.y - i * this.nPerChunk;
                rectNew.height = Math.min(this.nPerChunk - rectNew.y, setRect.height);
            } else if (i == yChunk2) // 落在的最后一个分块
            {
                rectNew.y = 0;
                rectNew.height = Math.min(memCellBand.get(i).getnHeight(), setRect.height - yRead);
            } else  // 落在的中间分块
            {
                rectNew.y = 0;
                rectNew.height = Math.min(memCellBand.get(i).getnHeight(), setRect.height - yRead);
            }
            memCellBand.get(i).setMemBuffer(writeBuffer, rectNew, yRead);

            yRead += rectNew.height;
        }
    }


    /**
     * 获取由iBand指定的全部内存块，并按照分块顺序排序
     *
     * @param iBand
     * @return
     */
    public List<MemCell> getMemCells(int iBand) {
        List<MemCell> memCellBand = new ArrayList<>();
        for (MemCell memCell :
                this.memCells) {
            if (memCell.getiBand() == iBand) {
                memCellBand.add(memCell);
            }
        }


        Collections.sort(memCellBand, new Comparator<MemCell>() {
            @Override
            public int compare(MemCell o1, MemCell o2) {
                return o1.getiChunk() - o2.getiChunk();
            }
        });


//        List<MemCell> memCellBand = new ArrayList<>();

//        List<MemCell> memCellBand= Linq4j.asEnumerable(this.memCells).where(new Predicate1<MemCell>() {
//            public boolean apply(MemCell arg0) {
//                return arg0.getiBand() == iBand;
//            }
//        }).select(new Function1<MemCell, MemCell>() {
//            public MemCell apply(MemCell arg) {
//                return arg;
//            }
//        }).toList();

//        .orderByDescending(new Function1<MemCell, MemCell>() {
//        public MemCell apply(MemCell arg) {
//            // 降序排列
//            return arg;
//        }
//    })

//        IEnumerable<MemCell> query = from items in this.memCells where items.IBand == iBand orderby items.IChunk select items;
//        foreach (var item in query)
//        {
//            memCellBand.add(item);
//        }
        return memCellBand;
    }


    /**
     * 根据图像参数申请相同大小的空内存
     */
    public void createMem() {
        this.memCells = new ArrayList<>();
        for (int iBand = 0; iBand < this.nBand; iBand++) {
            for (int iChunk = 0; iChunk < this.nChunkCount; iChunk++) {
                int ySize = 0;
                if (iChunk == this.nChunkCount - 1) {
                    ySize = this.nHeight - (this.nChunkCount - 1) * this.nPerChunk;
                } else {
                    ySize = this.nPerChunk;
                }
                short[] bytes = new short[this.nWidth * ySize];
                MemCell memCell = new MemCell(this.nWidth, ySize, iBand, iChunk, bytes);
                this.memCells.add(memCell);
            }
        }
    }
}
