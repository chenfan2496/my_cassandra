package org.frantz.mycassandra.db.rows;

import org.frantz.mycassandra.db.cql.ColumnMetadata;

import java.util.Collections;
import java.util.Iterator;

public class SimpleColumnData implements ColumnData{
    private final ColumnMetadata column;
    private final Cell cell;

    public SimpleColumnData(ColumnMetadata column, Cell cell) {
        this.column = column;
        this.cell = cell;
    }

    @Override
    public ColumnMetadata column() {
        return column;
    }

    @Override
    public Iterator<Cell> iterator() {
        return Collections.singleton(cell).iterator();
    }

    @Override
    public Cell getCell(CellPath path) {
        return (path == CellPath.EMPTY) ? cell : null;
    }

    @Override
    public int cellCount() {
        return 1;
    }

    @Override
    public boolean hasCells() {
        return cell != null;
    }
}
