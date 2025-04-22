//package com.github.dawndev.orion.broker.actor;
//
//import akka.actor.AbstractFSM;
//import akka.actor.Props;
//import com.github.dawndev.orion.broker.enums.ChannelState;
//import com.github.dawndev.orion.broker.pojo.ChannelData;
//import com.github.dawndev.orion.broker.pojo.NoData;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class ChannelActor extends AbstractFSM<ChannelState, ChannelData> {
//
//    private final Logger logger = LoggerFactory.getLogger(getClass());
//
//
//    // 初始化
//    {
//        // 初始状态和数据
//        startWith(ChannelState.INITIAL, NoData.INSTANCE);
//
//        // INITIAL状态处理
//        when(ChannelState.INITIAL,
//                matchEvent(ChannelProtocol.Login.class, NoData.class, (login, noData) -> {
//                    logger.info("用户 {} 正在登录...", login.username);
//                    if (authenticate(login.username, login.password)) {
//                        return goTo(ChannelState.RUNNING)
//                                .using(new SessionData(generateSessionId(), Instant.now()))
//                                .replying("LOGIN_SUCCESS");
//                    } else {
//                        return stay().replying("LOGIN_FAILED");
//                    }
//                })
//                        .eventAnyEvent(event -> {
//                            log.warning("在初始状态下收到无效消息: {}", event);
//                            return stay().replying("CHANNEL_NOT_READY");
//                        })
//        );
//
//        // RUNNING状态处理 (保持不变)
//        when(ChannelState.RUNNING,
//                matchEvent(ChannelProtocol.ProcessMessage.class, SessionData.class, (msg, sessionData) -> {
//                    logger.info("处理消息: {}", msg.message);
//                    return stay().using(updateLastActive(sessionData));
//                })
//                // ... 其他RUNNING状态处理逻辑保持不变
//        );
//
//        // DISPOSED状态处理 (保持不变)
//        when(ChannelState.DISPOSED,
//                matchEventAny((event, data) -> {
//                    logger.warning("通道已废弃，忽略所有消息: {}", event);
//                    return stay().replying("CHANNEL_DISPOSED");
//                })
//        );
//
//        // 更新状态转换日志
//        onTransition(
//                matchState(ChannelState.INITIAL, ChannelState.RUNNING, (from, to) -> {
//                    logger.info("通道从初始状态切换到运行状态");
//                }).state(ChannelState.RUNNING, ChannelState.DISPOSED, (from, to) -> {
//                    logger.info("通道从运行状态切换到废弃状态");
//                        })
//        );
//
//        initialize();
//    }
//
//
//    // 创建Actor的工厂方法
//    public static Props props() {
//        return Props.create(ChannelActor.class);
//    }
//}
