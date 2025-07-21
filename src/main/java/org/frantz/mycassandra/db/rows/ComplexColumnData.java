package org.frantz.mycassandra.db.rows;

import org.frantz.mycassandra.db.cql.ColumnMetadata;

import java.util.Iterator;
import java.util.NavigableMap;

public class ComplexColumnData implements ColumnData{
    private final ColumnMetadata column;
    private final NavigableMap<CellPath, Cell> cells;

    public ComplexColumnData(ColumnMetadata column, NavigableMap<CellPath, Cell> cells) {
        this.column = column;
        this.cells = cells;
    }

    @Override
    public ColumnMetadata column() {
        return column;
    }

    @Override
    public Iterator<Cell> iterator() {
        return cells.values().iterator();
    }

    @Override
    public Cell getCell(CellPath path) {
        return cells.get(path);
    }

    @Override
    public int cellCount() {
        return cells.size();
    }

    @Override
    public boolean hasCells() {
        return !cells.isEmpty();
    }
}
