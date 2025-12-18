package org.miniCassandra.db.rows;

public class Test {

//    import org.apache.cassandra.config.ColumnDefinition;
//import org.apache.cassandra.db.*;
//import org.apache.cassandra.db.rows.BTreeRow;
//import org.apache.cassandra.db.rows.Cell;
//import org.apache.cassandra.db.rows.BufferCell;
//import org.apache.cassandra.schema.TableMetadata;
//import org.apache.cassandra.schema.KeyspaceMetadata;
//import org.apache.cassandra.schema.SchemaConstants;
//import org.apache.cassandra.utils.ByteBufferUtil;
//import org.junit.Test;
//import java.util.ArrayList;
//import java.util.List;
//import static org.junit.Assert.*;
//
//    public class BTreeBuildWithUpdateFunctionTest {
//
//        // 表元数据（复用，全局唯一）
//        private static TableMetadata tableMeta;
//
//        static {
//            // 初始化表元数据（id为主键，cid为聚类键，col_0~col_999为普通列）
//            KeyspaceMetadata keyspaceMeta = KeyspaceMetadata.create(
//                    SchemaConstants.SYSTEM_KEYSPACE_NAME,
//                    SchemaConstants.STANDARD_DURABILITY,
//                    KeyspaceParams.simple(1)
//            );
//
//            TableMetadata.Builder tableBuilder = TableMetadata.builder(keyspaceMeta, "test_btree_build")
//                    .addPartitionKeyColumn("id", org.apache.cassandra.db.marshal.Int32Type.instance)
//                    .addClusteringColumn("cid", org.apache.cassandra.db.marshal.Int32Type.instance);
//
//            // 预定义列定义（col_0 ~ col_999）
//            for (int i = 0; i < 1000; i++) {
//                tableBuilder.addRegularColumn("col_" + i, org.apache.cassandra.db.marshal.Int32Type.instance);
//            }
//            tableMeta = tableBuilder.build();
//        }
//
//        // ===================== 核心方法：生成指定数量的Cell（包含重复列） =====================
//        private List<Cell> createCells(int count) {
//            List<Cell> cells = new ArrayList<>();
//            long baseTimestamp = System.currentTimeMillis() * 1000; // 微秒级时间戳
//
//            for (int i = 0; i < count; i++) {
//                // 构造列定义：col_[i%1000]（故意制造重复列，比如i=1000时，col_0重复）
//                String colName = "col_" + (i % 1000);
//                ColumnDefinition colDef = ColumnDefinition.regularBuilder()
//                        .table(tableMeta)
//                        .name(ByteBufferUtil.bytes(colName))
//                        .type(org.apache.cassandra.db.marshal.Int32Type.instance)
//                        .build();
//
//                // 构造Cell：值为i，时间戳递增（保证新元素时间戳更大）
//                Cell cell = BufferCell.create(
//                        colDef,
//                        baseTimestamp + i, // 时间戳递增
//                        ByteBufferUtil.bytes(i), // 列值
//                        LivenessInfo.create(baseTimestamp + i),
//                        Deletion.NONE
//                );
//                cells.add(cell);
//            }
//            return cells;
//        }
//
//        // ===================== 自定义UpdateFunction：按时间戳保留最新Cell =====================
//        private UpdateFunction<Cell> cellUpdateFunction() {
//            return (existingCell, newCell) -> {
//                // 比较时间戳：保留时间戳更大的Cell（Cassandra中最新的版本）
//                if (newCell.timestamp() > existingCell.timestamp()) {
//                    return newCell;
//                }
//                return existingCell;
//            };
//        }
//
//        // ===================== 测试BTree.build + UpdateFunction构造BTreeRow =====================
//        @Test
//        public void testBTreeBuildWithUpdateFunction() {
//            // 步骤1：生成1000个Cell（包含重复列，比如col_0会被生成2次）
//            List<Cell> cells = createCells(1000);
//
//            // 步骤2：用BTree.build构造btree数组（两种方式：noOp / 自定义UpdateFunction）
//            // 方式1：使用noOp（直接覆盖重复元素）
//            Object[] btreeNoOp = BTree.build(cells, UpdateFunction.<Cell>noOp(), ColumnDefinition.comparator);
//            // 方式2：使用自定义UpdateFunction（保留最新时间戳）
//            Object[] btreeCustom = BTree.build(cells, cellUpdateFunction(), ColumnDefinition.comparator);
//
//            // 步骤3：验证btree数组的有序性和去重性（最终应为1000个唯一列）
//            assertEquals("去重后btree数组长度应为1000", 1000, btreeNoOp.length);
//            assertEquals("自定义UpdateFunction后长度也应为1000", 1000, btreeCustom.length);
//
//            // 步骤4：构造BTreeRow（使用noOp的btree数组）
//            Clustering clustering = Clustering.create(ByteBufferUtil.bytes(100)); // 聚类键cid=100
//            BTreeRow row = BTreeRow.create(
//                    clustering,
//                    LivenessInfo.NONE,
//                    Deletion.NONE,
//                    btreeNoOp
//            );
//
//            // 步骤5：验证getCell获取数据（以col_0为例）
//            ColumnDefinition col0Def = ColumnDefinition.regularBuilder()
//                    .table(tableMeta)
//                    .name(ByteBufferUtil.bytes("col_0"))
//                    .type(org.apache.cassandra.db.marshal.Int32Type.instance)
//                    .build();
//
//            Cell col0Cell = row.getCell(col0Def);
//            assertNotNull("col_0的Cell应为非空", col0Cell);
//            // noOp模式下，col_0的最终值是最后一次生成的（i=1000时，i%1000=0，值为1000）
//            assertEquals("col_0的值应为1000（noOp覆盖）",
//                    1000,
//                    ByteBufferUtil.toInt(col0Cell.value()));
//
//            // 步骤6：验证自定义UpdateFunction的btree数组（col_0的值同样是1000，因为时间戳递增）
//            BTreeRow rowCustom = BTreeRow.create(
//                    clustering,
//                    LivenessInfo.NONE,
//                    Deletion.NONE,
//                    btreeCustom
//            );
//            Cell col0CellCustom = rowCustom.getCell(col0Def);
//            assertEquals("自定义UpdateFunction后col_0的值也应为1000",
//                    1000,
//                    ByteBufferUtil.toInt(col0CellCustom.value()));
//
//            // 步骤7：验证随机列（比如col_500）
//            ColumnDefinition col500Def = ColumnDefinition.regularBuilder()
//                    .table(tableMeta)
//                    .name(ByteBufferUtil.bytes("col_500"))
//                    .type(org.apache.cassandra.db.marshal.Int32Type.instance)
//                    .build();
//            Cell col500Cell = row.getCell(col500Def);
//            assertEquals("col_500的值应为500",
//                    500,
//                    ByteBufferUtil.toInt(col500Cell.value()));
//        }
//    }
}
