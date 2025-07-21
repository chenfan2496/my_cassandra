package org.frantz.mycassandra.db.io.util;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DataInputBuffer extends RebufferingInputStream {

    private static ByteBuffer slice(byte[] buffer, int offset, int length)
    {
        ByteBuffer buf = ByteBuffer.wrap(buffer);
        if (offset > 0 || length < buf.capacity())
        {
            buf.position(offset);
            buf.limit(offset + length);
            buf = buf.slice();
        }
        return buf;
    }
    /**
     * @param buffer
     * @param duplicate Whether or not to duplicate the buffer to ensure thread safety
     */
    public DataInputBuffer(ByteBuffer buffer, boolean duplicate)
    {
        super(duplicate ? buffer.duplicate() : buffer);
    }

    public DataInputBuffer(byte[] buffer, int offset, int length)
    {
        super(slice(buffer, offset, length));
    }

    public DataInputBuffer(byte[] buffer)
    {
        super(ByteBuffer.wrap(buffer));
    }

    @Override
    protected void reBuffer() throws IOException {

    }

    @Override
    public void skipBytesFully(int n) throws IOException {
        super.skipBytesFully(n);
    }
}
