package com.github.dawndev.orion.core.modular;

import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractAkkaShardRegionModular {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActorSystem actorSystem;


}