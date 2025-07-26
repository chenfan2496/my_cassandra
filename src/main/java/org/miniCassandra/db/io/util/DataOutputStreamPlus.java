package org.miniCassandra.db.io.util;

import com.google.common.base.Function;
import org.miniCassandra.config.Config;
import org.miniCassandra.utils.ByteBufferUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract class DataOutputStreamPlus extends OutputStream implements DataOutputPlus {
    protected final WritableByteChannel channel;

    protected DataOutputStreamPlus()
    {
        this.channel = newDefaultChannel();
    }

    protected DataOutputStreamPlus(WritableByteChannel channel)
    {
        this.channel = channel;
    }

    private static int MAX_BUFFER_SIZE =
            Integer.getInteger(Config.PROPERTY_PREFIX + "data_output_stream_plus_temp_buffer_size", 8192);

    private static final ThreadLocal<byte[]> tempBuffer = new ThreadLocal<byte[]>()
    {
        @Override
        public byte[] initialValue()
        {
            return new byte[16];
        }
    };
    /*
     * Factored out into separate method to create more flexibility around inlining
     */
    protected static byte[] retrieveTemporaryBuffer(int minSize)
    {
        byte[] bytes = tempBuffer.get();
        if (bytes.length < Math.min(minSize, MAX_BUFFER_SIZE))
        {
            // increase in powers of 2, to avoid wasted repeat allocations
            bytes = new byte[Math.min(MAX_BUFFER_SIZE, 2 * Integer.highestOneBit(minSize))];
            tempBuffer.set(bytes);
        }
        return bytes;
    }


    protected WritableByteChannel newDefaultChannel()
    {
        return new WritableByteChannel()
        {

            @Override
            public boolean isOpen()
            {
                return true;
            }

            @Override
            public void close() throws IOException
            {
            }

            @Override
            public int write(ByteBuffer src) throws IOException
            {
                int toWrite = src.remaining();

                if (src.hasArray())
                {
                    DataOutputStreamPlus.this.write(src.array(), src.arrayOffset() + src.position(), src.remaining());
                    src.position(src.limit());
                    return toWrite;
                }

                if (toWrite < 16)
                {
                    int offset = src.position();
                    for (int i = 0 ; i < toWrite ; i++)
                        DataOutputStreamPlus.this.write(src.get(i + offset));
                    src.position(src.limit());
                    return toWrite;
                }

                byte[] buf = retrieveTemporaryBuffer(toWrite);

                int totalWritten = 0;
                while (totalWritten < toWrite)
                {
                    int toWriteThisTime = Math.min(buf.length, toWrite - totalWritten);

                    ByteBufferUtil.arrayCopy(src, src.position() + totalWritten, buf, 0, toWriteThisTime);

                    DataOutputStreamPlus.this.write(buf, 0, toWriteThisTime);

                    totalWritten += toWriteThisTime;
                }

                src.position(src.limit());
                return totalWritten;
            }

        };
    }

    public abstract <R> R applyToChannel(Function<WritableByteChannel, R> f) throws IOException;
}
