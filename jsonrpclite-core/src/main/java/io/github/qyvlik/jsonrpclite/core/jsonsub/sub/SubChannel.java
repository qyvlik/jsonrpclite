package io.github.qyvlik.jsonrpclite.core.jsonsub.sub;

import com.google.common.collect.Maps;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.Map;

public class SubChannel implements Serializable {
    private String channel;
    private Map<String, ChannelSession> sessionMap = Maps.newConcurrentMap();

    public SubChannel() {

    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void onSub(SubRequestObject subRequestObject, WebSocketSession session) {
        sessionMap.put(session.getId(), new ChannelSession(session, subRequestObject));
    }

    public void onUnSub(WebSocketSession session) {
        sessionMap.remove(session.getId());
    }

    public Map<String, ChannelSession> getSessionMap() {
        return sessionMap;
    }
}
