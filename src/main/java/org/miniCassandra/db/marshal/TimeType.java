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
package org.miniCassandra.db.marshal;

//import org.miniCassandra.cql3.CQL3Type;
import org.miniCassandra.serializers.MarshalException;
import org.miniCassandra.serializers.TimeSerializer;
import org.miniCassandra.serializers.TypeSerializer;

import java.nio.ByteBuffer;


/**
 * Nanosecond resolution time values
 */
public class TimeType extends AbstractType<Long>
{
    public static final TimeType instance = new TimeType();
    private TimeType() {super(ComparisonType.BYTE_ORDER);} // singleton

    public ByteBuffer fromString(String source) throws MarshalException
    {
        return decompose(TimeSerializer.timeStringToLong(source));
    }

    @Override
    public boolean isValueCompatibleWithInternal(AbstractType<?> otherType)
    {
        return this == otherType || otherType == LongType.instance;
    }

//    public Term fromJSONObject(Object parsed) throws MarshalException
//    {
//        try
//        {
//            return new Constants.Value(fromString((String) parsed));
//        }
//        catch (ClassCastException exc)
//        {
//            throw new MarshalException(String.format(
//                    "Expected a string representation of a time value, but got a %s: %s", parsed.getClass().getSimpleName(), parsed));
//        }
//    }

    @Override
    public String toJSONString(ByteBuffer buffer, int protocolVersion)
    {
        return '"' + TimeSerializer.instance.toString(TimeSerializer.instance.deserialize(buffer)) + '"';
    }

//    public CQL3Type asCQL3Type()
//    {
//        return CQL3Type.Native.TIME;
//    }

    public TypeSerializer<Long> getSerializer()
    {
        return TimeSerializer.instance;
    }
}
