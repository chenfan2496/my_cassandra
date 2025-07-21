package org.frantz.mycassandra.db.rows;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CellPath {
    public static final CellPath EMPTY = new CellPath(Collections.emptyList());

    private final List<ByteBuffer> pathComponents;

    public CellPath(List<ByteBuffer> pathComponents) {
        this.pathComponents = Collections.unmodifiableList(
                new ArrayList<>(pathComponents));
    }

    public int size() {
        int size = 0;
        for (ByteBuffer component : pathComponents) {
            size += 4; // 每个组件的长度前缀
            size += component.remaining();
        }
        return size;
    }

    public List<ByteBuffer> getComponents() {
        return pathComponents;
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
