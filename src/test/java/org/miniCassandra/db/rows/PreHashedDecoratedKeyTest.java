package org.miniCassandra.db.rows;

import org.junit.Before;
import org.junit.Test;
import org.miniCassandra.db.PreHashedDecoratedKey;
import org.miniCassandra.dht.IPartitioner;
import org.miniCassandra.dht.Murmur3Partitioner;
import org.miniCassandra.dht.Token;
import org.miniCassandra.utils.ByteBufferUtil;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PreHashedDecoratedKeyTest {
    private IPartitioner partitioner;
    private Token token1, token2, token3;
    private ByteBuffer key1, key2, key3;

    @Before
    public void setup() {
        partitioner =  Murmur3Partitioner.instance;
        key1 = ByteBufferUtil.bytes("key1");
        key2 = ByteBufferUtil.bytes("key2");
        key3 = ByteBufferUtil.bytes("key1");
        token1 = partitioner.getToken(key1);
        token2 = partitioner.getToken(key2);
        token3 = partitioner.getToken(key3);
    }
    @Test
    public void testEquality() {
        PreHashedDecoratedKey dk1 = new PreHashedDecoratedKey(token1,key1);
        PreHashedDecoratedKey dk3 = new PreHashedDecoratedKey(token3, key3);
        System.out.println("token1: " + token1);
        System.out.println("token3: " + token3);
        System.out.println(dk1.getPartitioner().getToken(ByteBufferUtil.bytes("test1")).toString());
        assertEquals(token1, dk1.getToken());
        assertEquals(token3, dk3.getToken());
        assertEquals(token1, token3); // 相同输入应生成相同 Token
    }
    @Test
    public void testKeyComparison() {
        PreHashedDecoratedKey dk1 = new PreHashedDecoratedKey(token1, key1);
        PreHashedDecoratedKey dk2 = new PreHashedDecoratedKey(token2, key2);
        assertTrue(dk1.compareTo(dk2) < 0); // key1 < key2
        assertTrue(dk2.compareTo(dk1) > 0); // key2 > key1
        assertEquals(0, dk1.compareTo(dk1)); // 自反性
    }
}
