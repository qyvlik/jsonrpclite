package io.github.qyvlik.jsonrpclite.example.game.method;

import com.google.common.collect.Lists;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.method.RpcMethod;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.method.RpcParams;
import io.github.qyvlik.jsonrpclite.example.game.method.param.StringParam;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class LoginMethod extends RpcMethod {

    public LoginMethod() {
        super("game", "pub.login",
                new RpcParams(2, 2, Lists.newArrayList(
                        new StringParam("userName"),
                        new StringParam("password")
                )));
    }

    @Override
    protected ResponseObject callInternal(WebSocketSession session, RequestObject requestObject) {
        ResponseObject<String> responseObject = new ResponseObject<String>();
        responseObject.setResult("success");
        return responseObject;
    }
}
