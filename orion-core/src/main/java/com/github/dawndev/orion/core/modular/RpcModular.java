package com.github.dawndev.orion.core.modular;

import com.github.dawndev.orion.core.annotation.AutoProxy;
import com.github.dawndev.orion.core.annotation.Modular;
import com.github.dawndev.orion.core.annotation.Rpc;
import com.github.dawndev.orion.core.lang.method.MethodInvocationWrap;
import com.github.dawndev.orion.core.rpc.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Optional;

@Modular(initMethod = "init")
class RpcModular {

    @Autowired
    private ApplicationContext context;

    private EnumMap<MsgType, MethodInvocationWrap> rpcHandlers = new EnumMap<>(MsgType.class);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void init() {
        String[] handlerBeanNames = context.getBeanNamesForAnnotation(AutoProxy.class);
        for (String beanName : handlerBeanNames) {
            Object beanObj = context.getBean(beanName);
            Method[] methods = beanObj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Optional.ofNullable(method.getAnnotation(Rpc.class)).ifPresent((handler) -> {
                    MsgType msgType = handler.msgCode();
                    String alias = handler.alias();
                    String innerChecker = handler.checker();
                    long exp = handler.exp();
                    //beanObj.getClass().getDeclaredMethod()
                    if (rpcHandlers.containsKey(msgType)) {
                        logger.error("重复注册MsgType, {}", msgType);
                    }
                    rpcHandlers.put(msgType, new MethodInvocationWrap(beanObj, alias, null, method));
                });
            }
        }
    }
}