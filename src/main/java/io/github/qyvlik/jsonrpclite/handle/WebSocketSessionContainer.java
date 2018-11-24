package io.github.qyvlik.jsonrpclite.handle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.qyvlik.jsonrpclite.jsonsub.sub.ChannelSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import io.github.qyvlik.jsonrpclite.jsonsub.sub.SubChannel;
import io.github.qyvlik.jsonrpclite.jsonsub.sub.SubRequestObject;

import java.util.List;
import java.util.Map;


public class WebSocketSessionContainer {
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

    public boolean safeSend(WebSocketSession session, TextMessage textMessage) {
        try {
            synchronized (session) {
                session.sendMessage(textMessage);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
