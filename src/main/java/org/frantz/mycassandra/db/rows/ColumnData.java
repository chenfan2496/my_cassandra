package org.frantz.mycassandra.db.rows;

import org.frantz.mycassandra.db.cql.ColumnMetadata;

import java.util.Iterator;

public interface ColumnData {
    ColumnMetadata column();
    Iterator<Cell> iterator();
    Cell getCell(CellPath path);
    int cellCount();
    boolean hasCells();
}

