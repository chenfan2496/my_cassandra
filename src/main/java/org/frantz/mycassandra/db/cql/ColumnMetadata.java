package org.frantz.mycassandra.db.cql;

public class ColumnMetadata {
    final int id;
    final String name;
    final ColumnType type;
    final boolean isStatic;

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