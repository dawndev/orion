package com.github.dawndev.orion.broker.modular;

import com.github.dawndev.orion.broker.SimpleTcpServerHandler;
import com.github.dawndev.orion.broker.lang.NettyUtils;
import com.github.dawndev.orion.core.annotation.Modular;
import com.github.dawndev.orion.broker.config.ApplicationConfig;
import com.github.dawndev.orion.broker.config.NettyConfig;
import com.github.dawndev.orion.core.modular.AbstractModular;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Modular
@Component
public class NettyModular extends AbstractModular {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private NettyConfig nettyConfig;

    @Autowired
    SimpleTcpServerHandler simpleTcpServerHandler;

    private ApplicationContext context;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private ChannelFuture channelFuture;

    public void init() {
        logger.info("初始化netty, {}, {}, {}", applicationConfig.getTcpPort(), nettyConfig.getBossThreadCount(), nettyConfig.getWorkThreadCount());
    }

    @Override
    public void start() {
        int bossThreads = nettyConfig.getBossThreadCount();
        int workThreads = nettyConfig.getWorkThreadCount();
        int port = applicationConfig.getTcpPort();
        boolean useEpoll = NettyUtils.isEpollAvailable();

        // 它主要用来处理连接的管理
        bossGroup = useEpoll ? new EpollEventLoopGroup(bossThreads) : new NioEventLoopGroup(bossThreads);

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
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    simpleTcpServerHandler
                            );
                        }
                    });
            channelFuture = bootstrap.bind(port);
            channelFuture.sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("服务器启动失败,自动退出", e);
            System.exit(0);
        }
    }

    @Override
    public void stop() {
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

    @Override
    public int getPhase() {
        return 0;
    }
}
