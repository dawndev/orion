package com.github.dawndev.orion.gateway.modular;

import com.github.dawndev.orion.core.annotation.Modular;
import com.github.dawndev.orion.gateway.config.ApplicationConfig;
import com.github.dawndev.orion.gateway.config.NettyConfig;
import com.github.dawndev.orion.gateway.lang.SystemUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

@Modular
public class NettyModular implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private NettyConfig nettyConfig;


    private ApplicationContext context;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private ChannelFuture channelFuture;
    ChannelInitializer<Channel> channelInitializer;

    public void init() {
        logger.info("初始化netty, {}, {}, {}", applicationConfig.getTcpPort(), nettyConfig.getBossThreadCount(), nettyConfig.getWorkThreadCount());
    }

    private void start() {
        int bossThreads = nettyConfig.getBossThreadCount();
        int workThreads = nettyConfig.getWorkThreadCount();
        int port = applicationConfig.getTcpPort();
        boolean useEpoll = useEpoll();
        bossGroup = useEpoll ? new EpollEventLoopGroup(bossThreads) : new NioEventLoopGroup(bossThreads);// 它主要用来处理连接的管理

        // 设置工作线程，工作线程负责处理Channel中的消息
        workerGroup = useEpoll ? new EpollEventLoopGroup(workThreads)
                : new NioEventLoopGroup(workThreads);
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            // 创建连接channel的初始化器
            bootstrap.group(bossGroup, workerGroup)
                    .channel(useEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128).option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_SNDBUF, NettyConfig.MAX_FRAME_BYTES_LENGTH)
                    .childOption(ChannelOption.SO_RCVBUF, NettyConfig.MAX_FRAME_BYTES_LENGTH)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).childHandler(channelInitializer);
            channelFuture = bootstrap.bind(port);
            channelFuture.sync();
//            logger.info("----游戏服务器启动成功，port:{}---,服务类型: {}", port,
//                    (channelInitializer instanceof SocketServerChannelInitializer) ? "socket" : "websocket");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("服务器启动失败,自动退出", e);
            System.exit(0);
        }
    }

    @Override
    public void close() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        int quietPeriod = 5;
        int timeout = 30;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        if (workerGroup != null) {
            workerGroup.shutdownGracefully(quietPeriod, timeout, timeUnit);
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully(quietPeriod, timeout, timeUnit);
        }
    }

    private boolean useEpoll() {
        boolean flag = SystemUtils.isLinuxPlatform() && Epoll.isAvailable();
        logger.info("启用epoll: {}", flag);
        return flag;
    }
}
