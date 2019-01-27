package io.github.qyvlik.jsonrpclite.example.game.filter;

import io.github.qyvlik.jsonrpclite.core.handle.WebSocketFilter;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.core.jsonsub.sub.SubRequestObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class AuthFilter extends WebSocketFilter {

    public AuthFilter() {
        setGroup("game");
    }

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
