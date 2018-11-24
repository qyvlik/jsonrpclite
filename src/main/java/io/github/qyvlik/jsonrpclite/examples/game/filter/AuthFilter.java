package io.github.qyvlik.jsonrpclite.examples.game.filter;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import io.github.qyvlik.jsonrpclite.handle.WebSocketFilter;
import io.github.qyvlik.jsonrpclite.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.jsonsub.sub.SubRequestObject;

@Service
public class AuthFilter implements WebSocketFilter {

    @Override
    public boolean filter(WebSocketSession session, RequestObject requestObject) {
        if (requestObject.getMethod().startsWith("pub.")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean filter(WebSocketSession session, SubRequestObject subRequestObject) {
        if (subRequestObject.getChannel().startsWith("pub.")) {
            return true;
        }
        return false;
    }
}
