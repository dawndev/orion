package com.github.dawndev.orion.broker.actor;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import com.github.dawndev.orion.broker.lang.NettyUtils;
import com.github.dawndev.orion.broker.pojo.ChannelInactive;
import com.github.dawndev.orion.core.annotation.Actor;
import com.github.dawndev.orion.core.proto.AppMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import akka.actor.Cancellable;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;


@Actor("channelActor")
public class ChannelActor extends AbstractFSM<ChannelState, ChannelData> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // netty连接上下文，只能用于发送消息！
    private ChannelHandlerContext ctx;
    private Cancellable heartbeatTask;

    public ChannelActor() {

    }

    public ChannelActor(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    // 初始化
    {
        // 初始状态和数据
        startWith(ChannelState.INITIAL, EmptyData.INSTANCE);

        // INITIAL状态处理
        when(ChannelState.INITIAL,
                matchEvent(AppMessage.BaseMessage.class, EmptyData.class, (request, noData) -> {
                    Channel channel = ctx.channel();
                    String channelId = NettyUtils.getChannelId(channel);
                    String remoteIP = NettyUtils.getRemoteIP(channel);
                    logger.info("ChannelActor 用户 {} 正在登录... code: {}", remoteIP, request.getCode());
                    //if (authenticate(login.username, login.password)) {
                        return goTo(ChannelState.RUNNING)
                                .using(new SessionData(channelId, remoteIP));
                                //.replying("LOGIN_SUCCESS");
//                    } else {
//                        return stay().replying("LOGIN_FAILED");
//                    }
                }).event(HeartbeatMessage.class, (request, __) -> {
                    logger.info("actor 在登陆状态下, 超时了");
                    return goTo(ChannelState.DISPOSED);
                })
//                        .anyEvent((event, data) -> {
//                    logger.warn("ChannelActor在初始状态下收到无效消息: {}", event);
//                            return stay().replying("CHANNEL_NOT_READY");
//                        })
        );

        // RUNNING状态处理
        when(ChannelState.RUNNING,
                matchEvent(AppMessage.BaseMessage.class, SessionData.class, (msg, sessionData) -> {
                    logger.info("ChannelActor处理消息: {}", msg.getCode());
                    return stay().using(updateLastActive(sessionData));
                })
//                        .anyEvent((event, data) -> {
//                    logger.warn("ChannelActor在运行状态下收到无效消息: {}", event);
//                    return stay().replying("CHANNEL_NOT_READY");
//                })
        );

        // DISPOSED状态处理 (保持不变)
        when(ChannelState.DISPOSED,
                matchAnyEvent((event, data) -> {
                    logger.warn("ChannelActor已废弃，忽略所有消息: {}", event);
                    return stay();
                })
        );

        // 更新状态转换日志
        onTransition(
                matchState(null, ChannelState.DISPOSED, (from, to) -> {
                    logger.info("ChannelActor切换到废弃状态, 延迟5s关闭actor");

                    // Actor关闭
                    getContext().getSystem().scheduler()
                            .scheduleOnce(
                                    Duration.create(5, TimeUnit.SECONDS),
                                    getSelf(),
                                    PoisonPill.getInstance(),
                                    getContext().system().dispatcher(),
                                    getSelf()
                            );
                }).state(ChannelState.INITIAL, ChannelState.RUNNING, (from, to) -> {
                    logger.info("ChannelActor从初始状态切换到运行状态");
                })
        );


        whenUnhandled(
                matchEvent(ChannelInactive.class, (msg, __) -> {
                            if (stateName() == ChannelState.DISPOSED) {
                                return stay();
                            }
                            logger.info("{}状态下收到ChannelInactive:{}", stateName(), msg.getValue());
                            return goTo(ChannelState.DISPOSED);
                                    //.replying("ENTER_DISPOSED");
                        }
                ).event(HeartbeatMessage.class, (msg, __) -> {
                    logger.info("{}状态下收到HeartbeatMessage", stateName());
                    return stay();
                }).anyEvent((event, data) -> {
                    logger.warn("ChannelActor在{}状态下收到无效消息: {} = {}", stateName(), event.getClass().getName(), event);
                    return stay();
                })
        );
        initialize();
    }



    @Override
    public void preStart() throws Exception {
//        // 超时断线
        super.preStart();
        // 定时发送心跳消息
        heartbeatTask = getContext().system().scheduler().scheduleWithFixedDelay(
                Duration.create(10, TimeUnit.SECONDS), // 初始延迟为10
                Duration.create(10, TimeUnit.SECONDS), // 10秒发送一次心跳
                getSelf(), new HeartbeatMessage(), getContext().system().dispatcher(), getSelf());
    }

    /**
     * 结束处理
     */
    @Override
    public void postStop() {
        super.postStop();
        // 取消定时任务
        heartbeatTask.cancel();
    }

    private SessionData updateLastActive(SessionData oldData) {
        return new SessionData(oldData.getSessionId(), oldData.getRemoteIp());
    }

    public static class HeartbeatMessage implements Serializable {}

}

// 状态定义
enum ChannelState {
    INITIAL,      // 初始
    RUNNING,     // 运行中
    DISPOSED     // 废弃
}

interface ChannelData extends Serializable {

}

class EmptyData implements ChannelData {
    public static final EmptyData INSTANCE = new EmptyData();
    private EmptyData() {}
}

class SessionData implements ChannelData {

    private final String sessionId;
    private final String remoteId;

    public SessionData(String sessionId, String remoteId) {
        this.sessionId = sessionId;
        this.remoteId = remoteId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRemoteIp() {
        return remoteId;
    }
}