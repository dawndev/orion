package com.github.dawndev.orion.broker.lang;

import akka.actor.ActorRef;
import com.github.dawndev.orion.core.lang.SystemUtils;
import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;

public class NettyUtils {

    private static final Logger log = LoggerFactory.getLogger(NettyUtils.class);

    public static boolean isEpollAvailable() {
        return SystemUtils.isLinuxPlatform() && Epoll.isAvailable();
    }

    /**
     * 获取连接id
     * <p>
     *     当前 JVM 实例中唯一标识一个 Channel。
     *     非全局唯一性
     * </p>
     */
    public static String getChannelId(Channel channel) {
        return channel.id().asShortText();
    }

    public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
        return isa;
    }

    public static String socketAddress2String(final SocketAddress addr) {
        StringBuilder sb = new StringBuilder();
        InetSocketAddress inetSocketAddress = (InetSocketAddress)addr;
        sb.append(inetSocketAddress.getAddress().getHostAddress());
        sb.append(":");
        sb.append(inetSocketAddress.getPort());
        return sb.toString();
    }

    /**
     * 获取客户端的ip地址
     *
     * @param channel
     * @return
     */
    public static String getRemoteIP(Channel channel) {
        InetSocketAddress ipSocket = (InetSocketAddress)channel.remoteAddress();
        String remoteHost = ipSocket.getAddress().getHostAddress();
        return remoteHost;
    }


    public static Selector openSelector() throws IOException {
        Selector result = null;

        if (SystemUtils.isLinuxPlatform()) {
            try {
                final Class<?> providerClazz = Class.forName("sun.nio.ch.EPollSelectorProvider");
                if (providerClazz != null) {
                    try {
                        final Method method = providerClazz.getMethod("provider");
                        if (method != null) {
                            final SelectorProvider selectorProvider = (SelectorProvider)method.invoke(null);
                            if (selectorProvider != null) {
                                result = selectorProvider.openSelector();
                            }
                        }
                    } catch (final Exception e) {
                        log.warn("Open ePoll Selector for linux platform exception", e);
                    }
                }
            } catch (final Exception e) {
                // ignore
            }
        }

        if (result == null) {
            result = Selector.open();
        }

        return result;
    }

    public static AttributeKey<ActorRef> getChannelKey() {
        return AttributeKey.valueOf("CHANNEL_ACTOR");
    }
}
