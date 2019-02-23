package io.github.qyvlik.jsonrpclite.example.game.method;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation.RpcMethod;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation.RpcService;
import org.springframework.stereotype.Service;

@RpcService
@Service
public class PingMethod {

    @RpcMethod(group = "game", value = "pub.ping")
    public Long pubPing() {
        return System.currentTimeMillis();
    }
}
