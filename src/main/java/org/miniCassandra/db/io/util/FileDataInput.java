package org.miniCassandra.db.io.util;

import java.io.Closeable;
import java.io.IOException;

public interface FileDataInput extends RewindableDataInput, Closeable {
    String getPath();

    boolean isEOF() throws IOException;

    long bytesRemaining() throws IOException;

    void seek(long pos) throws IOException;

    long getFilePointer();
}
