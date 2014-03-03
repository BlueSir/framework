package com.sohu.smc.common.lifecycle.memcached;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public final class KryoSerializer {

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false); //
        kryo.register( ArrayList.class );
        kryo.register( LinkedList.class );
        kryo.register( HashSet.class );
        kryo.register( HashMap.class );

        kryo.register( BigDecimal.class, new DefaultSerializers.BigDecimalSerializer() );
        kryo.register( BigInteger.class, new DefaultSerializers.BigIntegerSerializer() );

    }

    public static void register(Class... classes) {
        for(Class clazz : classes) {
            kryo.register(clazz);
        }
    }

    public static byte[] write(Object obj) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        OutputStream outputStream = new DataOutputStream(baos);
        Output output = new Output(outputStream);
        kryo.writeClassAndObject(output, obj);


        byte[] b = output.toBytes();
        output.close();
        return b;
    }

    public static Object read(byte[] bytes) {
        ByteArrayInputStream bais=new ByteArrayInputStream(bytes);
        InputStream inputStream = new DataInputStream(bais);
        Input input = new Input(inputStream);
        Object o = kryo.readClassAndObject(input);
        input.close();

        return o;
    }
}
