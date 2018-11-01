package space.qyvlik.wsserver.handle;

import org.springframework.web.socket.WebSocketSession;
import space.qyvlik.wsserver.jsonrpc.entity.request.RequestObject;
import space.qyvlik.wsserver.jsonsub.sub.SubRequestObject;

public interface WebSocketFilter {
    boolean filter(WebSocketSession session, RequestObject requestObject);

    boolean filter(WebSocketSession session, SubRequestObject subRequestObject);
}
