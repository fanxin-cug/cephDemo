package com.zhanghao.ceph.Utils.geo.tile.mem;

import lombok.Data;

/**
 * 分块参数
 */
@Data
public class ChunkSize {

    // 分块的行数（高度）
    private int nPerChunk;

    // 分块数
    private int nChunkCount;

    // 最后一块的大小
    private int nLastChunk;

    public ChunkSize(int nPerChunk, int nChunkCount, int nLastChunk) {
        this.nPerChunk = nPerChunk;
        this.nChunkCount = nChunkCount;
        this.nLastChunk = nLastChunk;
    }
}
