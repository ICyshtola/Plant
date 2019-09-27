package com.example.icarus.plant.Mina;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class MinaThread extends Thread {

    public static IoSession session = null;
    private IoConnector connector = null;

    @Override
    public void run() {
        super.run();
        // TODO Auto-generated method stub
        System.out.println("客户端链接开始...");
        connector = new NioSocketConnector();
        System.out.println(1);
        // 设置链接超时时间
        connector.setConnectTimeoutMillis(10000);
        System.out.println(2);
        // 添加过滤器
        TextLineCodecFactory linecodec = new TextLineCodecFactory(Charset.forName("UTF-8"),
                LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue());
        //设置解码器大小，避免出现line is too long的报错
        linecodec.setDecoderMaxLineLength(1024 * 1024);
        linecodec.setEncoderMaxLineLength(1024 * 1024);
        connector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(linecodec));
        System.out.println(3);
        /**心跳机制**/
        ClientKeepAliveMessageFactory heartBeatFactory = new ClientKeepAliveMessageFactory();
        //设置心跳超时时间
//        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,40 * 1000);
        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory, IdleStatus.WRITER_IDLE,KeepAliveRequestTimeoutHandler.CLOSE);
        heartBeat.setForwardEvent(true);
        // 设置心跳包请求后超时无反馈情况下的处理机制，默认为关闭连接,在此处设置为输出日志提醒
        heartBeat.setRequestTimeout(30);
        heartBeat.setRequestTimeoutHandler(KeepAliveRequestTimeoutHandler.LOG);
        //设置心跳频率
//        heartBeat.setRequestInterval(10);
        connector.getFilterChain().addLast("keepAlive", heartBeat);
        /**心跳设置结束**/
        connector.setHandler(new MinaClientHandler());
        System.out.println(4);
        connector.setDefaultRemoteAddress(new InetSocketAddress(ConstantUtil.OUT_MATCH_PATH, ConstantUtil.WEB_MATCH_PORT));
        // 监听客户端是否断线
        connector.addListener(new IoListener() {
            @Override
            public void sessionDestroyed(IoSession arg0) throws Exception {
                super.sessionDestroyed(arg0);
                try {
                    int failCount = 0;
                    while (true) {
                        Thread.sleep(5000);
                        System.out.println(((InetSocketAddress) connector.getDefaultRemoteAddress()).getAddress()
                                .getHostAddress());
                        ConnectFuture future = connector.connect();
                        future.awaitUninterruptibly();// 等待连接创建完成
                        session = future.getSession();// 获得session
                        if (session != null && session.isConnected()) {
                            System.out.println("断线重连["
                                    + ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress()
                                    + ":" + ((InetSocketAddress) session.getRemoteAddress()).getPort() + "]成功");
                            break;
                        } else {
                            System.out.println("断线重连失败---->" + failCount + "次");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //开始连接
        try {
            System.out.println(5);
            ConnectFuture future = connector.connect(new InetSocketAddress(ConstantUtil.OUT_MATCH_PATH, ConstantUtil.WEB_MATCH_PORT));
            System.out.println(6);
            future.awaitUninterruptibly();// 等待连接创建完成
            System.out.println(7);
            session = future.getSession();// 获得session
            System.out.println(8);
            if (session != null && session.isConnected()) {
                System.out.println("连接成功");
            } else {
                System.out.println("连接失败");
            }
            System.out.println(9);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("客户端链接异常...");
        }
        System.out.println(10);
        if (session != null && session.isConnected()) {
            System.out.println(11);
            session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
            System.out.println("客户端断开...");
//            connector.dispose();//彻底释放Session,退出程序时调用不需要重连的可以调用这句话，也就是短连接不需要重连。长连接不要调用这句话，注释掉就OK。
        }
    }
}