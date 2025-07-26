package org.miniCassandra.db.io.util;

import org.miniCassandra.utils.vints.VIntCoding;

import java.io.*;

/**
 * 继承jdk DataInput接口 扩展增强读取变长整形数据能力
 */
public interface DataInputPlus extends DataInput {

    default long readVInt() throws IOException {
        return VIntCoding.readVInt(this);
    }
    /**
     * Think hard before opting for an unsigned encoding. Is this going to bite someone because some day
     * they might need to pass in a sentinel value using negative numbers? Is the risk worth it
     * to save a few bytes?
     *
     * Signed, not a fan of unsigned values in protocols and formats
     */
    default long readUnsignedVInt() throws IOException
    {
        return VIntCoding.readUnsignedVInt(this);
    }

    /**
     * Always skips the requested number of bytes, unless EOF is reached
     *
     * @param n number of bytes to skip
     * @return number of bytes skipped
     */
    public int skipBytes(int n) throws IOException;

    public default void skipBytesFully(int n) throws IOException
    {
        int skipped = skipBytes(n);
        if (skipped != n)
            throw new EOFException("EOF after " + skipped + " bytes out of " + n);
    }

    /**
     * Wrapper around an InputStream that provides no buffering but can decode varints
     */
    public class DataInputStreamPlus extends DataInputStream implements DataInputPlus
    {
        public DataInputStreamPlus(InputStream is)
        {
            super(is);
        }
    }
}
