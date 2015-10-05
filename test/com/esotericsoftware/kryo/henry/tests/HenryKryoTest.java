package com.esotericsoftware.kryo.henry.tests;

import com.esotericsoftware.kryo.KryoTestCase;
import com.esotericsoftware.kryo.henry.tests.model.User;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HenryKryoTest extends KryoTestCase {

    public void testSerialize(){
        User user=new User();
        user.setName("test");
        user.setAge(8);
        kryo.setRegistrationRequired(false);
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        final Output output = new Output(byteStream);
        kryo.writeClassAndObject(output, user);
        output.flush();
        output.close();
        final byte[] bytes = byteStream.toByteArray();

        // bytes contains unrecognized character, sometimes can not deserialize successfully
        final Input input = new Input(new ByteArrayInputStream(bytes));
        final Object obj =  kryo.readClassAndObject(input);
        input.close();
    }
}
