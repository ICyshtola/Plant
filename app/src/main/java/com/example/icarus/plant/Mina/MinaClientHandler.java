package com.example.icarus.plant.Mina;

import android.util.Log;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class MinaClientHandler extends IoHandlerAdapter {
    public static String mina_msg = "";

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        Log.i("MinaClientHandler", "客户端发生异常");
        System.out.println(cause.getMessage());
        super.exceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String msg = message.toString();
        mina_msg = msg;
        /**返回值判断 根据消息类型判断是哪种消息**/
        Log.i("MinaClientHandler", "客户端接收到的信息为: " + msg);
        super.messageReceived(session, message);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Log.i("MinaClientHandler", "发送的消息为: " + message.toString());
        super.messageSent(session, message);
    }
}
