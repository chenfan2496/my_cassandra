package org.frantz.mycassandra.db.io.util;

import java.nio.ByteBuffer;

public class FileSegmentInputStream extends DataInputBuffer implements DataInputPlus{
    private final String filePath;
    private final long offset;

    public FileSegmentInputStream(ByteBuffer buffer, String filePath, long offset)
    {
        super(buffer, false);
        this.filePath = filePath;
        this.offset = offset;
    }
    public String getPath()
    {
        return filePath;
    }

    private long size()
    {
        return offset + buffer.capacity();
    }

    public boolean isEOF()
    {
        return !buffer.hasRemaining();
    }

    public long bytesRemaining()
    {
        return buffer.remaining();
    }

    public void seek(long pos)
    {
        if (pos < 0 || pos > size())
            throw new IllegalArgumentException(String.format("Unable to seek to position %d in %s (%d bytes) in partial mode",
                    pos,
                    getPath(),
                    size()));


        buffer.position((int) (pos - offset));
    }

    @Override
    public boolean markSupported()
    {
        return false;
    }

    public DataPosition mark()
    {
        throw new UnsupportedOperationException();
    }

    public void reset(DataPosition mark)
    {
        throw new UnsupportedOperationException();
    }

    public long bytesPastMark(DataPosition mark)
    {
        return 0;
    }

    public long getFilePointer()
    {
        return offset + buffer.position();
    }
}
