package org.frantz.mycassandra.db.rows;

import org.frantz.mycassandra.db.cql.ColumnMetadata;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.frantz.mycassandra.db.rows.Constant.NO_DELETION_TIME;
import static org.frantz.mycassandra.db.rows.Constant.NO_TTL;

public abstract class AbstractCell implements  Cell{
    protected final ColumnMetadata column;
    protected final long timestamp;
    protected final int ttl;
    protected final int localDeletionTime;

    protected AbstractCell(ColumnMetadata column, long timestamp, int ttl, int localDeletionTime) {
        this.column = column;
        this.timestamp = timestamp;
        this.ttl = ttl;
        this.localDeletionTime = localDeletionTime;
    }


    @Override
    public ColumnMetadata column() {
        return column;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    @Override
    public int ttl() {
        return ttl;
    }

    @Override
    public int localDeletionTime() {
        return localDeletionTime;
    }

    @Override
    public ByteBuffer value() {
        return null;
    }

    @Override
    public CellPath path() {
        return CellPath.EMPTY;
    }

    @Override
    public boolean isLive(long nowInSec) {
        return localDeletionTime == NO_DELETION_TIME ||
                (ttl == NO_TTL && localDeletionTime >= nowInSec) ||
                (ttl != NO_TTL && nowInSec < localDeletionTime);
    }

    @Override
    public int compareTimestamps(Cell other) {
        return Long.compare(timestamp, other.timestamp());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell that = (Cell) o;
        return timestamp == that.timestamp() &&
                ttl == that.ttl() &&
                localDeletionTime == that.localDeletionTime() &&
                Objects.equals(column, that.column()) &&
                Objects.equals(path(), that.path()) &&
                value().equals(that.value());
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, timestamp, ttl, localDeletionTime, path(), value());
    }
}
