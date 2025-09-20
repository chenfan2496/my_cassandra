package org.miniCassandra.db.rows;


import org.junit.Test;
import org.miniCassandra.config.ColumnDefinition;
import org.miniCassandra.db.marshal.AbstractType;
import org.miniCassandra.db.marshal.ByteType;
import org.miniCassandra.db.marshal.SetType;
import org.miniCassandra.db.marshal.UTF8Type;
import org.miniCassandra.utils.ByteBufferUtil;

import java.nio.ByteBuffer;

public class BufferCellTest {

    private ColumnDefinition createColumnDefinition(AbstractType<?> type) {
        ColumnDefinition cd = ColumnDefinition.regularDef("ks", "cf", "col", type);
        return cd;
    }
    @Test
    public void testBasicFunction() {
        ByteBuffer byteBuffer = ByteBufferUtil.bytes("test");
        CellPath path = CellPath.create(ByteBufferUtil.bytes("elem1"));
        BufferCell bufferCell = new BufferCell(createColumnDefinition(ByteType.instance),123456789L,20,25,byteBuffer,null);
        System.out.println(bufferCell.dataSize());
    }
}
