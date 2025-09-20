package org.miniCassandra.db.rows;

import org.junit.Before;
import org.junit.Test;
import org.miniCassandra.config.ColumnDefinition;
import org.miniCassandra.db.BufferDecoratedKey;
import org.miniCassandra.db.Clustering;
import org.miniCassandra.db.DecoratedKey;
import org.miniCassandra.db.marshal.AbstractType;
import org.miniCassandra.db.marshal.ByteType;
import org.miniCassandra.db.marshal.Int32Type;
import org.miniCassandra.db.marshal.UTF8Type;
import org.miniCassandra.dht.IPartitioner;
import org.miniCassandra.dht.Murmur3Partitioner;
import org.miniCassandra.dht.Token;
import org.miniCassandra.utils.ByteBufferUtil;
import org.miniCassandra.utils.btree.BTree;
import org.miniCassandra.utils.btree.UpdateFunction;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class BTreeRowTest {
    private DecoratedKey KEY;
    private IPartitioner partitioner;
    private Token token1, token2, token3;
    private ByteBuffer key1, key2, key3,column1,column2,column3;
    private ColumnDefinition INT_COL;
    private ColumnDefinition TEXT_COL;
    private Clustering clustering;

    private ColumnDefinition createColumnDefinition(String name,AbstractType<?> type) {
        ColumnDefinition cd = ColumnDefinition.regularDef("ks", "cf", name, type);
        return cd;
    }

    public List<Cell> createCells(int size) {
        List<Cell> cells = new ArrayList<>(size);
        for(int i=0;i < size;i++) {
            ByteBuffer byteBuffer = ByteBufferUtil.bytes(i);
            cells.add(new BufferCell(createColumnDefinition("column" + i,Int32Type.instance),123456789L,200000,2500,byteBuffer,null));
        }
        return cells;
    }

    @Before
    public void setUp() {
        partitioner =  Murmur3Partitioner.instance;
        key1 = ByteBufferUtil.bytes("key1");
        key2 = ByteBufferUtil.bytes("key2");
        key3 = ByteBufferUtil.bytes("key1");
        column1 = ByteBufferUtil.bytes("column1");
        column2 = ByteBufferUtil.bytes("column2");
        column3 = ByteBufferUtil.bytes("column3");
        token1 = partitioner.getToken(key1);
        token2 = partitioner.getToken(key2);
        token3 = partitioner.getToken(key3);
        KEY =  new BufferDecoratedKey(token1,key1);
        INT_COL = ColumnDefinition.regularDef("ks", "cf", "int_col", Int32Type.instance);
        TEXT_COL = ColumnDefinition.regularDef("ks", "cf", "text_col", UTF8Type.instance);
        clustering =new Clustering(column1,column2);
    }

    // 1. 空行测试
    @Test
    public void testEmptyRow() {
        BTreeRow row = BTreeRow.create(clustering, LivenessInfo.EMPTY, Row.Deletion.LIVE,BTree.build(createCells(100), UpdateFunction.noOp()));
        ColumnDefinition columnDefinition = createColumnDefinition("column2",Int32Type.instance);
        Cell cell = row.getCell(columnDefinition);
        System.out.println(ByteBufferUtil.toInt(row.getCell(createColumnDefinition("2",Int32Type.instance)).value()));
        //        assertTrue("Row should be empty", row.isEmpty());
        //        assertEquals("Key mismatch", KEY, row.key());
        //        assertNull("No clustering should exist", row.clustering());
        //        assertEquals("Iterator should be empty", 0, getCellCount(row));
    }
//
//    // 2. 单单元格行测试
//    @Test
//    public void testSingleCellRow() {
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//        builder.addCell(new BufferCell(INT_COL, 1000L, ByteBufferUtil.bytes(42), 0, 0, null));
//
//        BTreeRow row = (BTreeRow) builder.build();
//
//        assertEquals(1, getCellCount(row));
//        Cell cell = row.getCell(INT_COL);
//        assertEquals(42, ByteBufferUtil.toInt(cell.value()));
//        assertEquals(1000L, cell.timestamp());
//    }
//
//    // 3. 多单元格行测试（有序插入）
//    @Test
//    public void testMultiCellOrderedInsertion() {
//        ColumnDefinition col1 = ColumnDefinition.regularDef("ks", "cf", "col1", Int32Type.instance);
//        ColumnDefinition col2 = ColumnDefinition.regularDef("ks", "cf", "col2", Int32Type.instance);
//
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//        builder.addCell(new BufferCell(col1, 1000L, ByteBufferUtil.bytes(10), 0, 0, null));
//        builder.addCell(new BufferCell(col2, 2000L, ByteBufferUtil.bytes(20), 0, 0, null));
//
//        BTreeRow row = (BTreeRow) builder.build();
//
//        Iterator<Cell> iter = row.iterator();
//        Cell firstCell = iter.next();
//        Cell secondCell = iter.next();
//
//        assertEquals("Cells should be ordered", col1, firstCell.column());
//        assertEquals(10, ByteBufferUtil.toInt(firstCell.value()));
//        assertEquals("Cells should be ordered", col2, secondCell.column());
//        assertEquals(20, ByteBufferUtil.toInt(secondCell.value()));
//    }
//
//    // 4. 无序插入测试（验证排序）
//    @Test
//    public void testUnorderedInsertionWithSorting() {
//        ColumnDefinition colA = ColumnDefinition.regularDef("ks", "cf", "a_col", Int32Type.instance);
//        ColumnDefinition colB = ColumnDefinition.regularDef("ks", "cf", "b_col", Int32Type.instance);
//
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//        // 插入顺序：B -> A
//        builder.addCell(new BufferCell(colB, 1000L, ByteBufferUtil.bytes(20), 0, 0, null));
//        builder.addCell(new BufferCell(colA, 1000L, ByteBufferUtil.bytes(10), 0, 0, null));
//
//        BTreeRow row = (BTreeRow) builder.build();
//        Iterator<Cell> iter = row.iterator();
//
//        // 验证排序：A -> B
//        Cell firstCell = iter.next();
//        assertEquals("A should come before B", colA, firstCell.column());
//        assertEquals(10, ByteBufferUtil.toInt(firstCell.value()));
//    }
//
//    // 5. 重复列更新测试
//    @Test
//    public void testDuplicateColumnUpdate() {
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//        builder.addCell(new BufferCell(INT_COL, 1000L, ByteBufferUtil.bytes(10), 0, 0, null));
//        builder.addCell(new BufferCell(INT_COL, 2000L, ByteBufferUtil.bytes(20), 0, 0, null)); // 更高时间戳
//
//        BTreeRow row = (BTreeRow) builder.build();
//
//        assertEquals(1, getCellCount(row)); // 只应保留一个单元格
//        Cell cell = row.getCell(INT_COL);
//        assertEquals("Higher timestamp should win", 20, ByteBufferUtil.toInt(cell.value()));
//        assertEquals(2000L, cell.timestamp());
//    }
//
//    // 6. 墓碑单元格测试
//    @Test
//    public void testTombstoneCell() {
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//
//        // 添加墓碑单元格
//        builder.addCell(new BufferCell(INT_COL, 1000L, ByteBufferUtil.bytes(10), 0, 1000, null));
//        // 添加有效单元格
//        builder.addCell(new BufferCell(TEXT_COL, 2000L, ByteBufferUtil.bytes("valid"), 0, 0, null));
//
//        BTreeRow row = (BTreeRow) builder.build();
//
//        // 墓碑应被保留（Cassandra在压缩前保留墓碑）
//        assertEquals(2, getCellCount(row));
//        assertTrue("INT cell should be tombstone", row.getCell(INT_COL).isTombstone());
//        assertFalse("TEXT cell should be alive", row.getCell(TEXT_COL).isTombstone());
//    }
//
//    // 7. 获取不存在的列
//    @Test
//    public void testGetNonExistentColumn() {
//        BTreeRow row = createSingleCellRow();
//
//        ColumnDefinition nonExistent = ColumnDefinition.regularDef("ks", "cf", "no_col", UTF8Type.instance);
//        assertNull("Should return null for non-existent column", row.getCell(nonExistent));
//    }
//
//    // 8. 集群键测试
//    @Test
//    public void testWithClustering() {
//        Clustering clustering = new Clustering(ByteBufferUtil.bytes("cluster1"), ByteBufferUtil.bytes("cluster2"));
//
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(clustering);
//        builder.addCell(new BufferCell(INT_COL, 1000L, ByteBufferUtil.bytes(42), 0, 0, null));
//
//        BTreeRow row = (BTreeRow) builder.build();
//
//        assertEquals("Clustering mismatch", clustering, row.clustering());
//        assertArrayEquals("Clustering values mismatch",
//                new ByteBuffer[]{ByteBufferUtil.bytes("cluster1"), ByteBufferUtil.bytes("cluster2")},
//                row.clustering().getRawValues());
//    }
//
//    // 9. 大型 BTree 压力测试
//    @Test
//    public void testLargeBTreeStructure() {
//        int cellCount = 1000;
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//
//        for (int i = 0; i < cellCount; i++) {
//            ColumnDefinition col = ColumnDefinition.regularDef("ks", "cf", "col" + i, Int32Type.instance);
//            builder.addCell(new BufferCell(col, System.currentTimeMillis() * 1000,
//                    ByteBufferUtil.bytes(i), 0, 0, null));
//        }
//
//        BTreeRow row = (BTreeRow) builder.build();
//        assertEquals("All cells should be present", cellCount, getCellCount(row));
//
//        // 验证所有单元格
//        int counter = 0;
//        for (Cell cell : row) {
//            assertEquals(counter++, cell.column().name.toString());
//        }
//    }
//
//    // 10. 行合并测试
//    @Test
//    public void testRowMerging() {
//        // 创建第一行
//        Row.Builder builder1 = BTreeRow.sortedBuilder();
//        builder1.newRow(Clustering.EMPTY);
//        builder1.addCell(new BufferCell(INT_COL, 1000L, ByteBufferUtil.bytes(10), 0, 0, null));
//        BTreeRow row1 = (BTreeRow) builder1.build();
//
//        // 创建第二行（更高时间戳）
//        Row.Builder builder2 = BTreeRow.sortedBuilder();
//        builder2.newRow(Clustering.EMPTY);
//        builder2.addCell(new BufferCell(INT_COL, 2000L, ByteBufferUtil.bytes(20), 0, 0, null));
//        builder2.addCell(new BufferCell(TEXT_COL, 2000L, ByteBufferUtil.bytes("new"), 0, 0, null));
//        BTreeRow row2 = (BTreeRow) builder2.build();
//
//        // 合并行
//        Row mergedRow = Rows.merge(row1, row2);
//
//        // 验证合并结果
//        assertEquals(2, getCellCount(mergedRow));
//        Cell intCell = mergedRow.getCell(INT_COL);
//        assertEquals("Higher timestamp should win", 20, ByteBufferUtil.toInt(intCell.value()));
//        Cell textCell = mergedRow.getCell(TEXT_COL);
//        assertEquals("new", UTF8Type.instance.getString(textCell.value()));
//    }
//
//    // 11. 迭代器顺序验证
//    @Test
//    public void testIteratorOrder() {
//        ColumnDefinition colA = ColumnDefinition.regularDef("ks", "cf", "a_col", Int32Type.instance);
//        ColumnDefinition colB = ColumnDefinition.regularDef("ks", "cf", "b_col", Int32Type.instance);
//        ColumnDefinition colC = ColumnDefinition.regularDef("ks", "cf", "c_col", Int32Type.instance);
//
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//        // 乱序添加
//        builder.addCell(new BufferCell(colC, 1000L, ByteBufferUtil.bytes(30), 0, 0, null));
//        builder.addCell(new BufferCell(colA, 1000L, ByteBufferUtil.bytes(10), 0, 0, null));
//        builder.addCell(new BufferCell(colB, 1000L, ByteBufferUtil.bytes(20), 0, 0, null));
//
//        BTreeRow row = (BTreeRow) builder.build();
//        Iterator<Cell> iter = row.iterator();
//
//        // 验证列名顺序
//        assertEquals(colA, iter.next().column());
//        assertEquals(colB, iter.next().column());
//        assertEquals(colC, iter.next().column());
//        assertFalse("No more cells", iter.hasNext());
//    }
//
//    // 12. 删除单元格测试（通过墓碑）
//    @Test
//    public void testCellDeletionWithTombstone() {
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//
//        // 添加普通单元格
//        builder.addCell(new BufferCell(INT_COL, 1000L, ByteBufferUtil.bytes(42), 0, 0, null));
//        // 添加删除同列的墓碑
//        builder.addCell(new BufferCell(INT_COL, 2000L, ByteBufferUtil.EMPTY_BYTE_BUFFER, 0, 2000, null));
//
//        BTreeRow row = (BTreeRow) builder.build();
//        Cell cell = row.getCell(INT_COL);
//
//        assertTrue("Cell should be tombstone", cell.isTombstone());
//        assertEquals("Tombstone should have deletion time", 2000, cell.getLocalDeletionTime());
//    }
//
//    // 13. 复杂类型路径测试
//    @Test
//    public void testComplexTypeWithPath() {
//        ColumnDefinition mapCol = ColumnDefinition.regularDef("ks", "cf", "map_col",
//                MapType.getInstance(UTF8Type.instance, Int32Type.instance));
//
//        CellPath path = CellPath.create(ByteBufferUtil.bytes("key1"));
//
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//        builder.addCell(new BufferCell(mapCol, 1000L, ByteBufferUtil.bytes(100), 0, 0, path));
//
//        BTreeRow row = (BTreeRow) builder.build();
//        Cell cell = row.getCell(mapCol);
//
//        assertNotNull(cell);
//        assertEquals(path, cell.path());
//        assertEquals(100, ByteBufferUtil.toInt(cell.value()));
//    }
//
//    // 辅助方法：获取行中的单元格数量
//    private int getCellCount(Row row) {
//        int count = 0;
//        for (Cell cell : row) {
//            count++;
//        }
//        return count;
//    }
//
//    // 辅助方法：创建单单元格行
//    private BTreeRow createSingleCellRow() {
//        Row.Builder builder = BTreeRow.sortedBuilder();
//        builder.newRow(Clustering.EMPTY);
//        builder.addCell(new BufferCell(INT_COL, 1000L, ByteBufferUtil.bytes(42), 0, 0, null));
//        return (BTreeRow) builder.build();
//    }
}
