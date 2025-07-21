package org.frantz.mycassandra.db.cql;

public class ColumnMetadata {
    public final int id;
    public final String name;
    public final ColumnType type;
    public final boolean isStatic;

    public ColumnMetadata(int id, String name, ColumnType type, boolean isStatic) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
    }

    public enum ColumnType {
        PARTITION_KEY,
        CLUSTERING_COLUMN,
        REGULAR,
        STATIC
    }
}