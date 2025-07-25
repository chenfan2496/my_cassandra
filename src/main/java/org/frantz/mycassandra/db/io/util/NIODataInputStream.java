package org.frantz.mycassandra.db.io.util;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;

public class NIODataInputStream extends RebufferingInputStream{
    protected final ReadableByteChannel channel;

    private static ByteBuffer makeBuffer(int bufferSize)
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
        buffer.position(0);
        buffer.limit(0);

        return buffer;
    }

    public NIODataInputStream(ReadableByteChannel channel, int bufferSize)
    {
        super(makeBuffer(bufferSize));

        Preconditions.checkNotNull(channel);
        this.channel = channel;
    }

    @Override
    protected void reBuffer() throws IOException
    {
        Preconditions.checkState(buffer.remaining() == 0);
        buffer.clear();

        while ((channel.read(buffer)) == 0) {}

        buffer.flip();
    }

    @Override
    public void close() throws IOException
    {
        channel.close();
        super.close();
        FileUtils.clean(buffer);
        buffer = null;
    }

    @Override
    public int available() throws IOException
    {
        if (channel instanceof SeekableByteChannel)
        {
            SeekableByteChannel sbc = (SeekableByteChannel) channel;
            long remainder = Math.max(0, sbc.size() - sbc.position());
            return (remainder > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)(remainder + buffer.remaining());
        }
        return buffer.remaining();
    }
}
