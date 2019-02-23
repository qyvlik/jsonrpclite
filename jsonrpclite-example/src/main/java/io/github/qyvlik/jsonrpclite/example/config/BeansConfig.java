package io.github.qyvlik.jsonrpclite.example.config;

import io.github.qyvlik.jsonrpclite.core.client.RpcClient;
import io.github.qyvlik.jsonrpclite.core.handle.WebSocketDispatch;
import io.github.qyvlik.jsonrpclite.core.handle.WebSocketFilter;
import io.github.qyvlik.jsonrpclite.core.handle.WebSocketSessionContainer;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker.RpcDispatcher;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker.RpcExecutor;
import io.github.qyvlik.jsonrpclite.example.client.GameClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class BeansConfig {

    @Bean("executor")
    public Executor executor() {
        return Executors.newCachedThreadPool();
    }

    @Bean("webSocketSessionContainer")
    public WebSocketSessionContainer webSocketSessionContainer() {
        return new WebSocketSessionContainer(2000, 20000);
    }

    @Bean(value = "rpcDispatcher", initMethod = "initInvoker")
    public RpcDispatcher rpcDispatcher(
            @Autowired ApplicationContext applicationContext) {
        return new RpcDispatcher(applicationContext);
    }

    @Bean("rpcExecutor")
    public RpcExecutor rpcExecutor(
            @Qualifier("executor") Executor executor) {
        return new RpcExecutor() {
            @Override
            public Executor defaultExecutor() {
                return executor;
            }

            @Override
            public Executor getByRequest(WebSocketSession session, RequestObject requestObject) {
                return null;
            }
        };
    }

    @Bean("gameClient")
    public GameClient gameClient() {
        return new GameClient(new RpcClient("ws://localhost:8080/game", 2000, 20000));
    }

    @Bean("gameDispatch")
    public WebSocketDispatch gameDispatch(@Qualifier("webSocketSessionContainer") WebSocketSessionContainer webSocketSessionContainer,
                                          @Qualifier("rpcDispatcher") RpcDispatcher rpcDispatcher,
                                          @Qualifier("rpcExecutor") RpcExecutor rpcExecutor,
                                          @Autowired List<WebSocketFilter> filters) {
        WebSocketDispatch webSocketDispatch = new WebSocketDispatch();

        webSocketDispatch.setGroup("game");
        webSocketDispatch.setRpcExecutor(rpcExecutor);
        webSocketDispatch.setRpcDispatcher(rpcDispatcher);
        webSocketDispatch.setWebSocketSessionContainer(webSocketSessionContainer);
        webSocketDispatch.addFilterList(filters);

        return webSocketDispatch;
    }
}
