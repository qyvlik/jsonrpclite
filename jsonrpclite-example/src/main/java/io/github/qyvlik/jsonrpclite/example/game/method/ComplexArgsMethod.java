package io.github.qyvlik.jsonrpclite.example.game.method;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation.RpcMethod;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation.RpcService;
import io.github.qyvlik.jsonrpclite.example.game.method.request.ComplexRequest;
import org.springframework.stereotype.Service;

@RpcService
@Service
public class ComplexArgsMethod {

    @RpcMethod(group = "game", value = "complex")
    public String complex(ComplexRequest request) {
        return "param1:" + request.getParam1() + ",param2:" + request.getParam2() + ",param3:" + request.getParam3();
    }
}
