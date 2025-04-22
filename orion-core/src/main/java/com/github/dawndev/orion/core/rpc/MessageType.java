package com.github.dawndev.orion.core.rpc;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {

    ;

    private final int code;

    private static final Map<Integer, MessageType> CODE_MAP = new HashMap<>();
    static {
        for (MessageType status : values()) {
            CODE_MAP.put(status.code, status);
        }
    }

    MessageType(int code) {
        this.code = code;
    }

    public static MessageType fromCode(int code) {
        MessageType status = CODE_MAP.get(code);
        if (status == null) {
            throw new IllegalArgumentException("无效的状态编码: " + code);
        }
        return status;
    }
}
