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

package org.miniCassandra.serializers;


import org.miniCassandra.utils.ByteBufferUtil;

import java.nio.ByteBuffer;

public class LongSerializer implements TypeSerializer<Long>
{
    public static final LongSerializer instance = new LongSerializer();
    @Override
    public Long deserialize(ByteBuffer bytes)
    {
        return bytes.remaining() == 0 ? null : ByteBufferUtil.toLong(bytes);
    }
    @Override
    public ByteBuffer serialize(Long value)
    {
        return value == null ? ByteBufferUtil.EMPTY_BYTE_BUFFER : ByteBufferUtil.bytes(value);
    }
    @Override
    public void validate(ByteBuffer bytes) throws MarshalException
    {
        if (bytes.remaining() != 8 && bytes.remaining() != 0)
            throw new MarshalException(String.format("Expected 8 or 0 byte long (%d)", bytes.remaining()));
    }
    @Override
    public String toString(Long value)
    {
        return value == null ? "" : String.valueOf(value);
    }
    @Override
    public Class<Long> getType()
    {
        return Long.class;
    }
}
