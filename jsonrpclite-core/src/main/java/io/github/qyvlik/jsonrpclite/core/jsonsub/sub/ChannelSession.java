package io.github.qyvlik.jsonrpclite.core.jsonsub.sub;

import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;

public class ChannelSession implements Serializable {
    private WebSocketSession webSocketSession;
    private SubRequestObject subRequestObject;

    public ChannelSession() {

    }

    public ChannelSession(WebSocketSession webSocketSession, SubRequestObject subRequestObject) {
        this.webSocketSession = webSocketSession;
        this.subRequestObject = subRequestObject;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public SubRequestObject getSubRequestObject() {
        return subRequestObject;
    }

    public void setSubRequestObject(SubRequestObject subRequestObject) {
        this.subRequestObject = subRequestObject;
    }
}
