package io.github.qyvlik.jsonrpclite.handle;

import org.springframework.web.socket.WebSocketSession;
import io.github.qyvlik.jsonrpclite.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.jsonsub.sub.SubRequestObject;

public interface WebSocketFilter {
    boolean filter(WebSocketSession session, RequestObject requestObject);

    boolean filter(WebSocketSession session, SubRequestObject subRequestObject);
}
