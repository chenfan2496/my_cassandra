package org.frantz.mycassandra.db.rows;

import org.frantz.mycassandra.db.cql.ColumnMetadata;

import java.nio.ByteBuffer;

public interface Cell {
    /** 获取列元数据 */
    ColumnMetadata column();
    /** 获取时间戳 (Long.MAX_VALUE - 时间戳 实现倒序排序) */
    long timestamp();

    /** 获取存活时间(TTL) */
    int ttl();

    /** 获取本地删除时间 */
    int localDeletionTime();

    /** 获取单元格值 */
    ByteBuffer value();

    /** 获取单元格路径 (复杂类型使用) */
    CellPath path();

    /** 序列化后的字节大小 */
    int dataSize();

    /** 判断单元格是否存活 */
    boolean isLive(long nowInSec);

    /** 比较两个单元格的时序 */
    int compareTimestamps(Cell other);
}
