package com.github.dawndev.orion.broker.actor;

import akka.actor.DeadLetter;
import akka.actor.AbstractActor;
import com.github.dawndev.orion.core.annotation.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Actor("deadLetterListener")
public class DeadLetterListener extends AbstractActor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DeadLetter.class, deadLetter -> {
            logger.warn("死信捕获:, 消息: {}, 发送者: {}, 目标：{}", deadLetter.message(), deadLetter.sender(), deadLetter.recipient());
        }).matchAny((it) -> {
            // pass
        }).build();
    }
}
