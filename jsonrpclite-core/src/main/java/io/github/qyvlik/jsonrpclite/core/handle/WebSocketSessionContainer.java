package io.github.qyvlik.jsonrpclite.core.handle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.qyvlik.jsonrpclite.core.jsonsub.sub.ChannelSession;
import io.github.qyvlik.jsonrpclite.core.jsonsub.sub.SubChannel;
import io.github.qyvlik.jsonrpclite.core.jsonsub.sub.SubRequestObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.List;
import java.util.Map;


public class WebSocketSessionContainer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, ConcurrentWebSocketSessionDecorator> sessionDecoratorMap = Maps.newConcurrentMap();
    private Map<String, WebSocketSession> sessionMap = Maps.newConcurrentMap();
    private Map<String, SubChannel> subscribeChannelMap = Maps.newConcurrentMap();

    public void onOpen(WebSocketSession session) {
        if (session == null) {
            return;
        }
        sessionMap.put(session.getId(), session);
    }

    public void onClose(WebSocketSession session) {

        if (session == null) {
            return;
        }

        for (SubChannel subChannel : subscribeChannelMap.values()) {
            subChannel.onUnSub(session);
        }
        sessionMap.remove(session.getId());
        sessionDecoratorMap.remove(session.getId());
    }

    public void onSub(SubRequestObject subRequestObject, WebSocketSession session) {
        if (session == null) {
            return;
        }

        SubChannel subChannel = subscribeChannelMap.get(subRequestObject.getChannel());
        if (subChannel == null) {
            subscribeChannelMap.putIfAbsent(subRequestObject.getChannel(), new SubChannel());
            subChannel = subscribeChannelMap.get(subRequestObject.getChannel());
        }
        subChannel.onSub(subRequestObject, session);
    }

    public void onUnSub(String channel, WebSocketSession session) {
        SubChannel subChannel = subscribeChannelMap.get(channel);
        if (subChannel == null) {
            return;
        }
        subChannel.onUnSub(session);
    }

    public List<ChannelSession> getSessionListFromChannel(String channel) {
        SubChannel subChannel = subscribeChannelMap.get(channel);
        if (subChannel == null) {
            return null;
        }
        return Lists.newArrayList(subChannel.getSessionMap().values());
    }

    public List<WebSocketSession> getAllSessionList() {
        return Lists.newArrayList(sessionMap.values());
    }

    public Map<String, SubChannel> getSubscribeChannelMap() {
        return subscribeChannelMap;
    }

    public boolean safeSend(WebSocketSession session, WebSocketMessage webSocketMessage) {
        if (!session.isOpen()) {
            return false;
        }
        try {
            sessionDecoratorMap.computeIfAbsent(
                    session.getId(),
                    k -> new ConcurrentWebSocketSessionDecorator(session, 1000, 100000))
                    .sendMessage(webSocketMessage);
            return true;
        } catch (Exception e) {
            logger.error("safeSend error:{}", e.getMessage());
            return false;
        }
    }
}
