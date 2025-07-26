package org.miniCassandra.db.io.util;

import java.io.IOException;

public interface RewindableDataInput extends DataInputPlus{

    DataPosition mark();

    void reset(DataPosition mark) throws IOException;

    long bytesPastMark(DataPosition mark);

}
