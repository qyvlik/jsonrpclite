package space.qyvlik.wsserver.examples.game.filter;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import space.qyvlik.wsserver.handle.WebSocketFilter;
import space.qyvlik.wsserver.jsonrpc.entity.request.RequestObject;
import space.qyvlik.wsserver.jsonsub.sub.SubRequestObject;

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
