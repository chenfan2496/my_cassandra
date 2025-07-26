package org.miniCassandra.db.io.util;

import org.miniCassandra.utils.vints.VIntCoding;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.function.Function;

public interface DataOutputPlus extends DataOutput {
    // write the buffer without modifying its position
    void write(ByteBuffer buffer) throws IOException;

    void write(Memory memory, long offset, long length) throws IOException;

    /**
     * Safe way to operate against the underlying channel. Impossible to stash a reference to the channel
     * and forget to flush
     */
    <R> R applyToChannel(Function<WritableByteChannel, R> c) throws IOException;

    default void writeVInt(long i) throws IOException
    {
        VIntCoding.writeVInt(i, this);
    }

    /**
     * This is more efficient for storing unsigned values, both in storage and CPU burden.
     *
     * Note that it is still possible to store negative values, they just take up more space.
     * So this method doesn't forbid e.g. negative sentinel values in future, if they need to be snuck in.
     * A protocol version bump can then be introduced to improve efficiency.
     */
    default void writeUnsignedVInt(long i) throws IOException
    {
        VIntCoding.writeUnsignedVInt(i, this);
    }

    /**
     * Returns the current position of the underlying target like a file-pointer
     * or the position withing a buffer. Not every implementation may support this
     * functionality. Whether or not this functionality is supported can be checked
     * via the {@link #hasPosition()}.
     *
     * @throws UnsupportedOperationException if the implementation does not support
     *                                       position
     */
    default long position()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * If the implementation supports providing a position, this method returns
     * {@code true}, otherwise {@code false}.
     */
    default boolean hasPosition()
    {
        return false;
    }
}
