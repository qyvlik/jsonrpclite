package space.qyvlik.wsserver.examples.game.method;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import space.qyvlik.wsserver.jsonrpc.entity.request.RequestObject;
import space.qyvlik.wsserver.jsonrpc.entity.response.ResponseObject;
import space.qyvlik.wsserver.jsonrpc.method.RpcMethod;
import space.qyvlik.wsserver.jsonrpc.method.RpcParams;

@Service
public class PingMethod extends RpcMethod {
    public PingMethod() {
        super("pub.ping", new RpcParams());
    }

    @Override
    protected ResponseObject callInternal(WebSocketSession session, RequestObject requestObject) {
        ResponseObject<Long> responseObject = new ResponseObject<>();
        responseObject.setResult(System.currentTimeMillis());
        return responseObject;
    }
}
