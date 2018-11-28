package io.github.qyvlik.jsonrpclite.core.jsonsub.pub;

import java.io.Serializable;

public class ChannelMessage<T> implements Serializable {
    private String channel;
    private T result;
    private ChannelError error;

    public ChannelMessage() {

    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ChannelError getError() {
        return error;
    }

    public void setError(ChannelError error) {
        this.error = error;
    }
}
