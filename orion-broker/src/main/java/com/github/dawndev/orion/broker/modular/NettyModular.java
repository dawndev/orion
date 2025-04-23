package com.github.dawndev.orion.broker.modular;

import com.github.dawndev.orion.broker.net.SimpleChannelInboundHandler;
import com.github.dawndev.orion.broker.lang.NettyUtils;
import com.github.dawndev.orion.core.annotation.Modular;
import com.github.dawndev.orion.broker.config.BrokerConfig;
import com.github.dawndev.orion.broker.config.NettyConfig;
import com.github.dawndev.orion.core.concurrent.NamedThreadFactory;
import com.github.dawndev.orion.core.modular.AbstractModular;
import com.github.dawndev.orion.core.proto.AppMessage.BaseMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
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
    private BrokerConfig brokerConfig;

    @Autowired
    private NettyConfig nettyConfig;

    @Autowired
    private SimpleChannelInboundHandler simpleChannelInboundHandler;

    private ApplicationContext context;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private ChannelFuture channelFuture;

    public void init() {
        logger.info("初始化netty, {}, {}, {}", brokerConfig.getTcpPort(), nettyConfig.getBossThreadCount(), nettyConfig.getWorkThreadCount());
    }

    @Override
    public void start() {
        logger.info("准备启动netty");
        int bossThreads = nettyConfig.getBossThreadCount();
        int workThreads = nettyConfig.getWorkThreadCount();
        int port = brokerConfig.getTcpPort();
        boolean useEpoll = NettyUtils.isEpollAvailable();

        // 它主要用来处理连接的管理
        bossGroup = useEpoll ? new EpollEventLoopGroup(bossThreads) : new NioEventLoopGroup(bossThreads);

        // 设置工作线程，工作线程负责处理Channel中的消息
        workerGroup = useEpoll ? new EpollEventLoopGroup(workThreads)
                : new NioEventLoopGroup(workThreads);

        ServerBootstrap bootstrap = new ServerBootstrap();
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
                        BaseMessage appMessage = BaseMessage.getDefaultInstance();

                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(2036334592, 0, 4,
                                0, 4));
                        pipeline.addLast(new ProtobufDecoder(appMessage));
                        pipeline.addLast(new LengthFieldPrepender(4));
                        pipeline.addLast(new ProtobufEncoder());
                        pipeline.addLast(simpleChannelInboundHandler);
                    }
                });
        channelFuture = bootstrap.bind(port);
        logger.info("tcp server started on port: {}", port);

        // 开启一个线程，监控网络服务器的关闭
        new NamedThreadFactory.Builder().namingPattern("netty-sync-close").build().newThread(
            () -> {
                try {
                    // 等待关闭
                    channelFuture.channel().closeFuture().sync();
                    logger.info( "Netty服务器关闭了{} ", channelFuture.channel());
                } catch (Exception e) {
                    logger.error("", e);
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                    logger.info("All loop groups are closed");
                }
            }).start();

        // 等待端口启动完毕
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        logger.info("准备关闭netty");
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
        super.stop();
    }


    // Positive values also represent the order in which components are stopped during the container shutdown.
    @Override
    public int getPhase() {
        return 0;
    }
}
