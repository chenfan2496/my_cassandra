package org.frantz.mycassandra.db.rows;

import org.frantz.mycassandra.db.cql.ColumnMetadata;

import java.nio.ByteBuffer;
import java.util.*;

public abstract class Cells {
    private Cells() {} // 工具类，不可实例化

    /** 比较两个单元格的时序和路径 */
    public static int compare(Cell c1, Cell c2) {
        int cmp = Integer.compare(c1.column().id, c2.column().id);
        if (cmp != 0) return cmp;

        cmp = CellPathComparator.INSTANCE.compare(c1.path(), c2.path());
        if (cmp != 0) return cmp;

        return Long.compare(c2.timestamp(), c1.timestamp()); // 倒序
    }

    /** 合并两个单元格（保留时间戳最新的） */
    public static Cell reconcile(Cell existing, Cell update) {
        // 时间戳较大的优先
        if (existing.timestamp() < update.timestamp())
            return update;

        // 时间戳相等时，使用更高的本地删除时间
        if (existing.timestamp() == update.timestamp() &&
                existing.localDeletionTime() < update.localDeletionTime())
            return update;

        return existing;
    }

    /** 收集所有不重复的列ID */
    public static Set<Integer> collectColumnIds(Iterator<Cell> cells) {
        Set<Integer> ids = new HashSet<>();
        while (cells.hasNext()) {
            ids.add(cells.next().column().id);
        }
        return ids;
    }

    /** 将单元格迭代器分组到ColumnData结构中 */
    public static Map<ColumnMetadata, ColumnData> groupByColumn(Iterator<Cell> cells) {
        Map<ColumnMetadata, ColumnData> result = new HashMap<>();

        while (cells.hasNext()) {
            Cell cell = cells.next();
            ColumnMetadata column = cell.column();

            if (cell.path() == CellPath.EMPTY) {
                // 简单列
                result.put(column, new SimpleColumnData(column, cell));
            } else {
                // 复杂列
                ComplexColumnData complexData = (ComplexColumnData) result.get(column);
                if (complexData == null) {
                    complexData = new ComplexColumnData(column, new TreeMap<>());
                    result.put(column, complexData);
                }

                // 在实际Cassandra中这个转换是不安全的，这里简化处理
                NavigableMap<CellPath, Cell> cellMap = complexData.getCells();
                cellMap.put(cell.path(), cell);
            }
        }

        return result;
    }
    // ======================= CellPath 比较器 =======================
    private static class CellPathComparator implements Comparator<CellPath> {
        static final CellPathComparator INSTANCE = new CellPathComparator();

        @Override
        public int compare(CellPath p1, CellPath p2) {
            if (p1 == CellPath.EMPTY && p2 == CellPath.EMPTY) return 0;
            if (p1 == CellPath.EMPTY) return -1;
            if (p2 == CellPath.EMPTY) return 1;

            List<ByteBuffer> l1 = p1.getComponents();
            List<ByteBuffer> l2 = p2.getComponents();

            int len = Math.min(l1.size(), l2.size());
            for (int i = 0; i < len; i++) {
                int cmp = l1.get(i).compareTo(l2.get(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(l1.size(), l2.size());
        }
    }
}
