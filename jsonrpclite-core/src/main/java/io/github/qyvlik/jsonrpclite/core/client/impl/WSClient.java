package io.github.qyvlik.jsonrpclite.core.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.github.qyvlik.jsonrpclite.core.client.ChannelMessageHandler;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;
import io.github.qyvlik.jsonrpclite.core.jsonsub.pub.ChannelMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.Map;

public class WSClient {
    private final Map<Long, RpcResponseFuture> rpcCallback = Maps.newConcurrentMap();
    private final Map<String, ChannelMessageHandler> channelCallback = Maps.newConcurrentMap();
    private ConcurrentWebSocketSessionDecorator decorator;
    private WebSocketSession session;

    public WSClient(WebSocketSession webSocketSession, int sendTimeLimit, int bufferSizeLimit) {
        this.session = webSocketSession;
        this.decorator = new ConcurrentWebSocketSessionDecorator(session, sendTimeLimit, bufferSizeLimit);
    }

    public Map<Long, RpcResponseFuture> getRpcCallback() {
        return rpcCallback;
    }

    public Map<String, ChannelMessageHandler> getChannelCallback() {
        return channelCallback;
    }

    public boolean isOpen() {
        return this.session != null && this.session.isOpen();
    }

    public void sendText(TextMessage textMessage) throws IOException {
        decorator.sendMessage(textMessage);
    }

    public void close() throws IOException {
        decorator.close();
    }

    public void onTextMessage(TextMessage message) {
        JSONObject resObj = JSON.parseObject(message.getPayload());
        if (StringUtils.isNotBlank(resObj.getString("channel"))) {
            ChannelMessageHandler channelMessageHandler
                    = channelCallback.get(resObj.getString("channel"));
            if (channelMessageHandler != null) {
                ChannelMessage channelMessage = resObj.toJavaObject(ChannelMessage.class);
                channelMessageHandler.handle(channelMessage);
            }
        } else {
            ResponseObject responseObject = resObj.toJavaObject(ResponseObject.class);
            RpcResponseFuture future = rpcCallback.remove(responseObject.getId());
            if (future != null) {
                future.setResult(responseObject);
            }
        }
    }

    public ConcurrentWebSocketSessionDecorator getDecorator() {
        return decorator;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void onClose(WebSocketSession session, CloseStatus status) {
        if (this.session != null && this.session.getId().equals(session.getId())) {
            this.session = null;
            this.decorator = null;
        }
        this.rpcCallback.clear();
        this.channelCallback.clear();
    }

    public void onError(WebSocketSession session, Throwable exception) {
        // todo
    }
}
