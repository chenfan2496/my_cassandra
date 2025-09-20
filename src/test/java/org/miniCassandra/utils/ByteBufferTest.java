package org.miniCassandra.utils;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteBufferTest {
    @Test
    public void testByteBuffer() {
        byte[] bytes = "test_slice_array".getBytes();
        ByteBuffer bb = ByteBuffer.allocate(1024);
        assert bb.position() == 0;
        assert bb.limit() == 1024;
        assert bb.capacity() == 1024;
        bb.put(bytes);
        assert bb.position() == bytes.length;
        assert bb.remaining() == bb.limit() - bb.position();
        ByteBuffer bb2 = bb.slice();
        assert bb2.position() == 0;
    }
    @Test
    public void byteBufferTest2() {
       ByteBuffer bb1 =  ByteBuffer.allocate(1024);
       ByteBuffer bb2 = ByteBuffer.allocateDirect(1024);
       System.out.println("初始状态: " + bufferToString(bb1)); // position=0, limit=256, capacity=256
        bb1.put((byte)65);
        bb1.put("Hello, ByteBuffer!".getBytes(StandardCharsets.UTF_8)); // 写入字节数组[1](@ref)
       bb1.putInt(29);
        System.out.println("写入后: " + bufferToString(bb1)); // position 前进到写入数据后的位置
        bb1.flip();
        System.out.println("flip后: " + bufferToString(bb1)); // position 前进到写入数据后的位置
        byte firstByte = bb1.get();
        System.out.println("第一个字节: " + firstByte); // 65
        byte[] textBytes = new byte[17]; // 根据写入的文本长度创建数组
        bb1.get(textBytes); // 读取文本数据
        System.out.println("文本内容: " + new String(textBytes, StandardCharsets.UTF_8)); // Hello, ByteBuffer!
        int number = bb1.getInt(); // 读取整数
        System.out.println("整数: " + number); // 12345
        System.out.println("读取后: " + bufferToString(bb1)); // position 已前进到 limit 处
        // 🔄 重置缓冲区以重新读取
        bb1.rewind(); // 将 position 重置为 0，limit 不变[1,4](@ref)
        System.out.println("rewind()后: " + bufferToString(bb1)); // position=0, limit不变

        // 🔄 清空缓冲区以准备再次写入 (不会真正清除数据，只是重置指针)
        bb1.clear(); // position 设置为 0，limit 设置为 capacity[1,3,4](@ref)
        System.out.println("clear()后: " + bufferToString(bb1)); // position=0, limit=capacity=256

        // 现在可以重新写入数据了
        bb1.put("New data".getBytes());
        bb1.flip(); // 再次切换到读模式
        System.out.println("重新写入并flip后: " + bufferToString(bb1));
    }
    // 辅助方法：可视化 Buffer 状态
    private static String bufferToString(ByteBuffer buffer) {
        return String.format("pos=%d, lim=%d, cap=%d",
                buffer.position(), buffer.limit(), buffer.capacity());
    }
}
