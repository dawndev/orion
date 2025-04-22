package com.github.dawndev.orion.broker.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

// 无状态，单例
@Component
//@Scope("prototype")
@ChannelHandler.Sharable
public class SimpleTcpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        System.out.println("Received message from client: " + message);
        ctx.writeAndFlush("Server received: " + message);
    }
}


//```python
//import socket
//
//        HOST = 'localhost'
//PORT = 8888
//
//with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
//        s.connect((HOST, PORT))
//        s.sendall(b'Hello, Netty Server!')
//data = s.recv(1024)
//
//print('Received from server:', data.decode())
//```