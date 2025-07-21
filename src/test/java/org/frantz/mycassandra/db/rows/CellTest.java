package org.frantz.mycassandra.db.rows;


import org.frantz.mycassandra.db.cql.ColumnMetadata;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class CellTest {

    public void cellTest() {
        // 创建元数据
        ColumnMetadata nameCol = new ColumnMetadata(1, "name", ColumnMetadata.ColumnType.REGULAR, false);
        ColumnMetadata emailsCol = new ColumnMetadata(2, "emails", ColumnMetadata.ColumnType.REGULAR, false);

        // 创建简单单元格
        Cell nameCell = new BufferCell(nameCol, System.currentTimeMillis(),
                ByteBuffer.wrap("Alice".getBytes()));

        // 创建复杂单元格
        CellPath homePath = new CellPath(Arrays.asList(ByteBuffer.wrap("home".getBytes())));
        Cell homeEmail = new BufferCell(emailsCol, System.currentTimeMillis() - 1000,
                ByteBuffer.wrap("alice@home.com".getBytes()), homePath);

        CellPath workPath = new CellPath(Arrays.asList(ByteBuffer.wrap("work".getBytes())));
        Cell workEmail = new BufferCell(emailsCol, System.currentTimeMillis(),
                ByteBuffer.wrap("alice@company.com".getBytes()), workPath);

        // 分组单元格
        Iterator<Cell> cells = Arrays.asList(nameCell, homeEmail, workEmail).iterator();
        Map<ColumnMetadata, ColumnData> groupedData = Cells.groupByColumn(cells);

        // 获取复杂列数据
        ColumnData emailData = groupedData.get(emailsCol);
        System.out.println("Email cells count: " + emailData.cellCount());

        // 查询特定路径的单元格
        Cell homeCell = emailData.getCell(homePath);
        System.out.println("Home email: " +
                new String(homeCell.value().array()));

        // 合并冲突单元格
        Cell newHomeEmail = new BufferCell(emailsCol, System.currentTimeMillis() + 1000,
                ByteBuffer.wrap("new_alice@home.com".getBytes()), homePath);

        Cell reconciled = Cells.reconcile(homeEmail, newHomeEmail);
        System.out.println("Reconciled email: " +
                new String(reconciled.value().array()));
    }
}
