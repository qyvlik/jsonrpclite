package io.github.qyvlik.jsonrpclite.example.game.method;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation.RpcMethod;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation.RpcService;
import org.springframework.stereotype.Service;

@RpcService
@Service
public class LoginMethod {

    @RpcMethod(group = "game", value = "pub.login")
    public String pubLogin(String userName, String password) {
        return "success";
    }
}
