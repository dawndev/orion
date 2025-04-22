package com.github.dawndev.orion.broker.pojo;

import java.io.Serializable;

public class ChannelInactive implements Serializable {
    private int value;
    public ChannelInactive(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
