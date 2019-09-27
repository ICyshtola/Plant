package com.example.icarus.plant.Mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import java.io.IOException;

public class ClientKeepAliveMessageFactory implements KeepAliveMessageFactory {

    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
//        System.out.println(o.toString());
        String heart = "ping";
        if(o.toString().contains(heart)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
        return null;
}

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
        System.out.println("发送心跳");
        return "pong";
    }
}

