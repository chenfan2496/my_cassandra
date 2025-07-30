/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.miniCassandra.db.rows;

import org.miniCassandra.config.CFMetaData;
import org.miniCassandra.config.ColumnDefinition;
import org.miniCassandra.db.SerializationHeader;

import java.nio.ByteBuffer;
import java.util.*;
public class SerializationHelper
{
    /**
     * Flag affecting deserialization behavior (this only affect counters in practice).
     *  - LOCAL: for deserialization of local data (Expired columns are
     *      converted to tombstones (to gain disk space)).
     *  - FROM_REMOTE: for deserialization of data received from remote hosts
     *      (Expired columns are converted to tombstone and counters have
     *      their delta cleared)
     *  - PRESERVE_SIZE: used when no transformation must be performed, i.e,
     *      when we must ensure that deserializing and reserializing the
     *      result yield the exact same bytes. Streaming uses this.
     */
    public enum Flag
    {
        LOCAL, FROM_REMOTE, PRESERVE_SIZE
    }

    private final Flag flag;
    public final int version;

    private final ColumnFilter columnsToFetch;
    private ColumnFilter.Tester tester;

    private final Map<ByteBuffer, CFMetaData.DroppedColumn> droppedColumns;
    private CFMetaData.DroppedColumn currentDroppedComplex;


    public SerializationHelper(CFMetaData metadata, int version, Flag flag, ColumnFilter columnsToFetch)
    {
        this.flag = flag;
        this.version = version;
        this.columnsToFetch = columnsToFetch;
        this.droppedColumns = metadata.getDroppedColumns();
    }

    public SerializationHelper(CFMetaData metadata, int version, Flag flag)
    {
        this(metadata, version, flag, null);
    }

    public Columns fetchedStaticColumns(SerializationHeader header)
    {
        return columnsToFetch == null ? header.columns().statics : columnsToFetch.fetchedColumns().statics;
    }

    public Columns fetchedRegularColumns(SerializationHeader header)
    {
        return columnsToFetch == null ? header.columns().regulars : columnsToFetch.fetchedColumns().regulars;
    }

    public boolean includes(ColumnDefinition column)
    {
        return columnsToFetch == null || columnsToFetch.includes(column);
    }

    public boolean includes(CellPath path)
    {
        return path == null || tester == null || tester.includes(path);
    }

    public boolean canSkipValue(ColumnDefinition column)
    {
        return columnsToFetch != null && columnsToFetch.canSkipValue(column);
    }

    public boolean canSkipValue(CellPath path)
    {
        return path != null && tester != null && tester.canSkipValue(path);
    }

    public void startOfComplexColumn(ColumnDefinition column)
    {
        this.tester = columnsToFetch == null ? null : columnsToFetch.newTester(column);
        this.currentDroppedComplex = droppedColumns.get(column.name.bytes);
    }

    public void endOfComplexColumn()
    {
        this.tester = null;
    }

    public boolean isDropped(Cell cell, boolean isComplex)
    {
        CFMetaData.DroppedColumn dropped = isComplex ? currentDroppedComplex : droppedColumns.get(cell.column().name.bytes);
        return dropped != null && cell.timestamp() <= dropped.droppedTime;
    }

    public boolean isDroppedComplexDeletion(DeletionTime complexDeletion)
    {
        return currentDroppedComplex != null && complexDeletion.markedForDeleteAt() <= currentDroppedComplex.droppedTime;
    }

    public ByteBuffer maybeClearCounterValue(ByteBuffer value)
    {
        return flag == Flag.FROM_REMOTE || (flag == Flag.LOCAL && CounterContext.instance().shouldClearLocal(value))
             ? CounterContext.instance().clearAllLocal(value)
             : value;
    }
}
