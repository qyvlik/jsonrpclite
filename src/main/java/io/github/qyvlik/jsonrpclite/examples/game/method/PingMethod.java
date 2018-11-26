package io.github.qyvlik.jsonrpclite.examples.game.method;

import io.github.qyvlik.jsonrpclite.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.jsonrpc.entity.response.ResponseObject;
import io.github.qyvlik.jsonrpclite.jsonrpc.method.RpcMethod;
import io.github.qyvlik.jsonrpclite.jsonrpc.method.RpcParams;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class PingMethod extends RpcMethod {
    public PingMethod() {
        super("example", "pub.ping", new RpcParams());
    }

    @Override
    protected ResponseObject callInternal(WebSocketSession session, RequestObject requestObject) {
        ResponseObject<Long> responseObject = new ResponseObject<>();
        responseObject.setResult(System.currentTimeMillis());
        return responseObject;
    }
}
