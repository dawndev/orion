package com.github.dawndev.orion.broker.modular;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.event.EventStream;
import com.github.dawndev.orion.core.akka.SpringExtension;
import com.github.dawndev.orion.core.annotation.Modular;
import com.github.dawndev.orion.core.modular.AbstractModular;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Modular
@Component
public class DeadLettersModular extends AbstractModular {

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private SpringExtension springExtension;

    public void init() {
        // pass
    }

    @Override
    public void start() {
        ActorRef ref = springExtension.actorOf(
                actorSystem,
                "deadLetterListener",
                "dead-letter-listener"
        );
        EventStream eventStream = actorSystem.getEventStream();
        eventStream.subscribe(ref, AllDeadLetters.class);
    }
}
