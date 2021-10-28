package com.zhanghao.ceph.Utils.geo.tile.mem;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 分块参数
 * 注意：分块大小必须是256的倍数，可以保证2倍降采样8次不出现分块为0或者小数的情况
 */
public class ChunkHelper {

    /**
     * 单个分块的内存大小控制在400MB左右或者以内
     */
    private static long chunkBufferSize = 40 * 1024 * 1024 * 10;

    /**
     * 计算分块的高度，必须为256的倍数
     *
     * @param nWidth   图像宽度
     * @param nHeight  图像高度
     * @param dataType 图像位数，1：byte；2；short；4：int
     * @return
     */
    public static int getPerChunkHeight(int nWidth, int nHeight, int dataType) {
        int nPerChunk = 256;
        long imageSize = ((long) nWidth) * nHeight * dataType;
        if (imageSize <= chunkBufferSize) {
            nPerChunk = nHeight;
        } else {
            long tempPerChunk = chunkBufferSize / nWidth / dataType;
            nPerChunk = (int) ((tempPerChunk / 256 + 1) * 256);
        }
        return nPerChunk;
    }

    /**
     * 计算分块参数
     *
     * @param nWidth
     * @param nHeight
     * @param dataType
     * @return
     */
    public static ChunkSize getChunkSize(int nWidth, int nHeight, int dataType) {
        int nPerChunk = ChunkHelper.getPerChunkHeight(nWidth, nHeight, dataType);
        // 分块数
        int nChunkCount = 1;
        // 最后一块的大小
        int nLastChunk = nHeight;
        // 如果需要读取的图像行数太多，则需要进行分块读取
        if (nHeight > nPerChunk) {
            nChunkCount = nHeight / nPerChunk;
            nLastChunk = nPerChunk + nHeight % nPerChunk;
        }
        return new ChunkSize(nPerChunk, nChunkCount, nLastChunk);
    }

    /**
     * 得到分块读取时当前块的数据区域参数，按行分块
     *
     * @param iChunk
     * @param chunkSize
     * @param nWidth
     * @return
     */
    public static Rectangle getChunkRectFromSrc(int iChunk, ChunkSize chunkSize, int nWidth) {
        Rectangle srcChunkRect = new Rectangle();
        int yPerSize = chunkSize.getNPerChunk();
        srcChunkRect.y = iChunk * chunkSize.getNPerChunk();

        if (chunkSize.getNChunkCount() == 1 || iChunk == (chunkSize.getNChunkCount() - 1))  // 只有一块 或者 最后一块
        {
            yPerSize = chunkSize.getNLastChunk();
        }
        srcChunkRect.x = 0;
        srcChunkRect.width = nWidth;
        srcChunkRect.height = yPerSize;
        return srcChunkRect;
    }


    /**
     * 得到分块读取时每块的数据区域参数，按行分块
     *
     * @param nWidth         图像宽度
     * @param nHeight        图像高度
     * @param dataType       图像位数，1：byte；2；short；4：int
     * @param yOffFirstChunk 第一个分块的偏移（为了一些特殊场合的数据对齐），无偏移为0
     * @return
     */
    public static List<Rectangle> getAllChunkRectFromSrc(int nWidth, int nHeight, int dataType, int yOffFirstChunk) {
        List<Rectangle> rectangleList = new ArrayList<>();

        // 实际参与计算的图像高度
        int nHeightLogic = nHeight - yOffFirstChunk;
        int nPerChunk = ChunkHelper.getPerChunkHeight(nWidth, nHeightLogic, dataType);

        // 计算分块数
        int nChunkCount = nHeightLogic > nPerChunk ? nHeightLogic / nPerChunk : 1;
        int nFirstChunk = nPerChunk + yOffFirstChunk;
        int nLastChunk = nChunkCount > 1 ? nPerChunk + nHeightLogic % nPerChunk : nHeight;

        for (int iChunk = 0; iChunk < nChunkCount; iChunk++) {
            Rectangle srcChunkRect = new Rectangle();
            int yPerSize = nPerChunk;
            if (nChunkCount == 1 || iChunk == nChunkCount - 1) {// 只有一块 或者 最后一块
                yPerSize = nLastChunk;
            } else if (iChunk == 0) {
                yPerSize = nFirstChunk;
            }

            srcChunkRect.x = 0;
            srcChunkRect.y = iChunk > 0 ? (iChunk - 1) * nPerChunk + nFirstChunk : 0;
            srcChunkRect.width = nWidth;
            srcChunkRect.height = yPerSize;
            rectangleList.add(srcChunkRect);
        }
        return rectangleList;
    }
}
