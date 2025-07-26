package org.miniCassandra.db.rows;

import org.miniCassandra.db.cql.ColumnMetadata;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.miniCassandra.db.rows.Constant.NO_DELETION_TIME;
import static org.miniCassandra.db.rows.Constant.NO_TTL;

public class BufferCell extends AbstractCell{
    private final ByteBuffer value;
    private final CellPath path;

    public BufferCell(ColumnMetadata column, long timestamp, ByteBuffer value) {
        this(column, timestamp, NO_TTL, NO_DELETION_TIME, value, null);
    }
    public BufferCell(ColumnMetadata column, long timestamp, ByteBuffer value, CellPath homePath) {
        this(column, timestamp, NO_TTL,NO_DELETION_TIME, value, homePath);
    }
    public BufferCell(ColumnMetadata column, long timestamp, int ttl,
                      int localDeletionTime, ByteBuffer value) {
        this(column, timestamp, ttl, localDeletionTime, value, null);
    }

    public BufferCell(ColumnMetadata column, long timestamp, int ttl,
                      int localDeletionTime, ByteBuffer value, CellPath path) {
        super(column, timestamp, ttl, localDeletionTime);
        this.value = Objects.requireNonNull(value);
        this.path = path != null ? path : CellPath.EMPTY;
    }




    @Override
    public int dataSize() {
        int size = 0;
        size += 8; // timestamp
        size += 4; // ttl
        size += 4; // localDeletionTime
        // 值长度前缀
        size += 4;
        size += value.remaining();
        // 路径大小
        size += path.size();
        return size;
    }

    /** 获取单元格值的字节视图（只读） */
    public ByteBuffer bytes() {
        return value.asReadOnlyBuffer();
    }
}
