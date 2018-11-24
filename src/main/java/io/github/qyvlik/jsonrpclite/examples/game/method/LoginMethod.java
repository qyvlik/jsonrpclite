package io.github.qyvlik.jsonrpclite.examples.game.method;

import com.google.common.collect.Lists;
import io.github.qyvlik.jsonrpclite.jsonrpc.method.RpcParams;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import io.github.qyvlik.jsonrpclite.examples.game.method.param.StringParam;
import io.github.qyvlik.jsonrpclite.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.jsonrpc.entity.response.ResponseObject;
import io.github.qyvlik.jsonrpclite.jsonrpc.method.RpcMethod;

@Service
public class LoginMethod extends RpcMethod {

    public LoginMethod() {
        super("pub.login",
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
