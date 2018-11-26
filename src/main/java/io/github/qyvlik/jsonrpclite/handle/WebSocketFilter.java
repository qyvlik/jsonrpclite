package io.github.qyvlik.jsonrpclite.handle;

import io.github.qyvlik.jsonrpclite.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.jsonsub.sub.SubRequestObject;
import org.springframework.web.socket.WebSocketSession;

public abstract class WebSocketFilter {
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


    public abstract boolean filter(WebSocketSession session, RequestObject requestObject);

    public abstract boolean filter(WebSocketSession session, SubRequestObject subRequestObject);
}
