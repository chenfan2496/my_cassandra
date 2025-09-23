package org.miniCassandra.serializers;

import java.nio.ByteBuffer;

public interface TypeSerializer2<T> {

    public ByteBuffer serialize(T t);

    public T deserialize(ByteBuffer buffer);

    public Class<T> getType();

    public default String toCQLLiteral(ByteBuffer buffer)
    {
        return buffer == null || !buffer.hasRemaining()
                ? "null"
                : toString(deserialize(buffer));
    }

    public void validate(ByteBuffer bytes) throws MarshalException;

    String toString(T deserialize);
}
