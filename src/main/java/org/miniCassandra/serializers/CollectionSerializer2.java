package org.miniCassandra.serializers;

import org.miniCassandra.transport.Server;
import org.miniCassandra.utils.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

public abstract class CollectionSerializer2<T> implements TypeSerializer<T>{

    protected abstract List<ByteBuffer> serializeValue(T value);

    protected abstract T deserializeForNativeProtocol(ByteBuffer buffer, int version);

    public abstract void validateForNativeProtocol(ByteBuffer buffer, int version);

    protected abstract int getElementCount(T value);


    public static ByteBuffer pack(Collection<ByteBuffer> buffers, int elements, int version) {
        int size = 0;
        for(ByteBuffer bb : buffers) {
            size +=  sizeOfValue(bb,version);
        }
        ByteBuffer result = ByteBuffer.allocate(sizeOfCollectionSize(elements, version)+size);
        writeCollectionSize(result, elements, version);
        for (ByteBuffer bb : buffers)
            writeValue(result, bb, version);
        return (ByteBuffer)result.flip();
    }
    public static void writeValue(ByteBuffer output, ByteBuffer value, int version)
    {
        if (value == null)
        {
            output.putInt(-1);
            return;
        }

        output.putInt(value.remaining());
        output.put(value.duplicate());
    }
    public static ByteBuffer readValue(ByteBuffer input, int version)
    {
        int size = input.getInt();
        if (size < 0)
            return null;

        return ByteBufferUtil.readBytes(input, size);
    }
    protected static void writeCollectionSize(ByteBuffer output, int elements, int version)
    {
        output.putInt(elements);
    }
    public static int readCollectionSize(ByteBuffer input, int version)
    {
        return input.getInt();
    }

    protected static int sizeOfCollectionSize(int elements, int version)
    {
        return 4;
    }
    public static int sizeOfValue(ByteBuffer value, int version)
    {
        return value == null ? 4 : 4 + value.remaining();
    }

    @Override
    public ByteBuffer serialize(T value) {
        List<ByteBuffer> byteBuffers = serializeValue(value);
        return pack(byteBuffers, getElementCount(value), Server.VERSION_3);
    }

    @Override
    public T deserialize(ByteBuffer bytes) {
        return deserializeForNativeProtocol(bytes, Server.VERSION_3);
    }

    @Override
    public void validate(ByteBuffer bytes) throws MarshalException {

    }
}
