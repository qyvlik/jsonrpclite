package io.github.qyvlik.jsonrpclite.example.config;

import io.github.qyvlik.jsonrpclite.core.client.RpcClient;
import io.github.qyvlik.jsonrpclite.core.handle.WebSocketDispatch;
import io.github.qyvlik.jsonrpclite.core.handle.WebSocketFilter;
import io.github.qyvlik.jsonrpclite.core.handle.WebSocketSessionContainer;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.method.RpcMethod;
import io.github.qyvlik.jsonrpclite.example.client.GameClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class BeanConfig {

    @Bean("executor")
    public Executor executor() {
        return Executors.newCachedThreadPool();
    }

    @Bean("webSocketSessionContainer")
    public WebSocketSessionContainer webSocketSessionContainer() {
        return new WebSocketSessionContainer(2000, 20000);
    }

    @Bean("gameClient")
    public GameClient gameClient() {
        return new GameClient(new RpcClient("ws://localhost:8080/game", 2000, 20000));
    }

    @Bean("gameDispatch")
    public WebSocketDispatch gameDispatch(@Qualifier("executor") Executor executor,
                                          @Qualifier("webSocketSessionContainer") WebSocketSessionContainer webSocketSessionContainer,
                                          @Autowired List<RpcMethod> rpcMethodList,
                                          @Autowired List<WebSocketFilter> filters) {
        WebSocketDispatch webSocketDispatch = new WebSocketDispatch();

        webSocketDispatch.setGroup("game");
        webSocketDispatch.setExecutor(executor);
        webSocketDispatch.setWebSocketSessionContainer(webSocketSessionContainer);
        webSocketDispatch.addRpcMethodList(rpcMethodList);
        webSocketDispatch.addFilterList(filters);

        return webSocketDispatch;
    }
}
