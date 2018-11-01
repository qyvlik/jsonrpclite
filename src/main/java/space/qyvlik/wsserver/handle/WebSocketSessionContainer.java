package space.qyvlik.wsserver.handle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.web.socket.WebSocketSession;
import space.qyvlik.wsserver.jsonsub.sub.SubChannel;

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

    public void onSub(String channel, WebSocketSession session) {
        if (session == null) {
            return;
        }

        SubChannel subChannel = subscribeChannelMap.get(channel);
        if (subChannel == null) {
            subscribeChannelMap.putIfAbsent(channel, new SubChannel());
            subChannel = subscribeChannelMap.get(channel);
        }
        subChannel.onSub(session);
    }

    public void onUnSub(String channel, WebSocketSession session) {
        SubChannel subChannel = subscribeChannelMap.get(channel);
        if (subChannel == null) {
            return;
        }
        subChannel.onUnSub(session);
    }

    public List<WebSocketSession> getSessionListFromChannel(String channel) {
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
}
