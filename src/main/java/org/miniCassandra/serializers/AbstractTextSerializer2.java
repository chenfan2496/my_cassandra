package org.miniCassandra.serializers;

import org.apache.commons.lang3.StringUtils;
import org.miniCassandra.utils.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

public abstract class AbstractTextSerializer2 implements TypeSerializer<String>{
    private Charset charset;
    protected AbstractTextSerializer2(Charset charset)
    {
        this.charset = charset;
    }


    @Override
    public ByteBuffer serialize(String value) {
       return ByteBufferUtil.bytes(value,charset);
    }

    @Override
    public String deserialize(ByteBuffer bytes) {
        try {
           return ByteBufferUtil.string(bytes,charset);
        }catch (CharacterCodingException e) {
            throw new MarshalException("Invalid " + charset + " bytes " + ByteBufferUtil.bytesToHex(bytes));
        }
    }

    @Override
    public void validate(ByteBuffer bytes) throws MarshalException {

    }

    @Override
    public String toString(String value) {
        return value;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String toCQLLiteral(ByteBuffer buffer) {
        return buffer == null
                ? "null"
                : '\'' + StringUtils.replace(deserialize(buffer), "'", "''") + '\'';    }
}
