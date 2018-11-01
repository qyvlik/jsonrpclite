package space.qyvlik.wsserver.jsonsub.sub;

import com.google.common.collect.Maps;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.Map;

public class SubChannel implements Serializable {
    private String channel;
    private Map<String, WebSocketSession> sessionMap = Maps.newConcurrentMap();

    public SubChannel() {

    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void onSub(WebSocketSession session) {
        sessionMap.put(session.getId(), session);
    }

    public void onUnSub(WebSocketSession session) {
        sessionMap.remove(session.getId());
    }

    public Map<String, WebSocketSession> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, WebSocketSession> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
