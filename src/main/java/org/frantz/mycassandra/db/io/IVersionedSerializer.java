package org.frantz.mycassandra.db.io;

import org.frantz.mycassandra.db.io.util.DataInputPlus;
import org.frantz.mycassandra.db.io.util.DataOutputPlus;

import java.io.IOException;

public interface IVersionedSerializer<T>
{
    /**
     * Serialize the specified type into the specified DataOutputStream instance.
     *
     * @param t type that needs to be serialized
     * @param out DataOutput into which serialization needs to happen.
     * @param version protocol version
     * @throws java.io.IOException if serialization fails
     */
    public void serialize(T t, DataOutputPlus out, int version) throws IOException;

    /**
     * Deserialize into the specified DataInputStream instance.
     * @param in DataInput from which deserialization needs to happen.
     * @param version protocol version
     * @return the type that was deserialized
     * @throws IOException if deserialization fails
     */
    public T deserialize(DataInputPlus in, int version) throws IOException;

    /**
     * Calculate serialized size of object without actually serializing.
     * @param t object to calculate serialized size
     * @param version protocol version
     * @return serialized size of object t
     */
    public long serializedSize(T t, int version);
}
