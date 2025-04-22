package com.github.dawndev.orion.broker.pojo;

public class SessionData implements ChannelData {
    public final String sessionId;

    public SessionData(String sessionId) {
        this.sessionId = sessionId;
    }
}