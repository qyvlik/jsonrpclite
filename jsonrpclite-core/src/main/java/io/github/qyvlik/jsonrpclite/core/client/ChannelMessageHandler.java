package io.github.qyvlik.jsonrpclite.core.client;

import io.github.qyvlik.jsonrpclite.core.jsonsub.pub.ChannelMessage;

public interface ChannelMessageHandler {
    void handle(ChannelMessage channelMessage);
}
