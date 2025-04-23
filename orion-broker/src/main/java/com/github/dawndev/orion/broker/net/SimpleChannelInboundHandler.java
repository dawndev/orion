package com.github.dawndev.orion.broker.net;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.github.dawndev.orion.broker.lang.NettyUtils;
import com.github.dawndev.orion.broker.pojo.ChannelInactive;
import com.github.dawndev.orion.core.akka.SpringExtension;
import com.github.dawndev.orion.core.enums.ApplicationState;
import com.github.dawndev.orion.core.state.ApplicationStateManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 无状态，单例
@Component
//@Scope("prototype")
@ChannelHandler.Sharable
public class SimpleChannelInboundHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationStateManager applicationStateManager;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private SpringExtension springExtension;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (applicationStateManager.getCurrentState() != ApplicationState.RUNNING) {
            channel.close();
        }

        // 启动actor
        ActorRef ref = springExtension.actorOf(
                actorSystem,
                "channelActor",
                "channel-actor",
                ctx
        );
        AttributeKey<ActorRef> actorKey = NettyUtils.getChannelKey();
        channel.attr(actorKey).set(ref);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.tellChannelActor(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.tellChannelActor(ctx, new ChannelInactive(0));
    }

    private void tellChannelActor(ChannelHandlerContext ctx, Object msg) {
        AttributeKey<ActorRef> actorKey = NettyUtils.getChannelKey();
        ActorRef actor = ctx.channel().attr(actorKey).get();
        if (null != actor) {
            actor.tell(msg, ActorRef.noSender());
        }
    }

}