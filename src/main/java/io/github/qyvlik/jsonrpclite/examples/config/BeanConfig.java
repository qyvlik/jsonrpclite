package io.github.qyvlik.jsonrpclite.examples.config;

import io.github.qyvlik.jsonrpclite.handle.WebSocketFilter;
import io.github.qyvlik.jsonrpclite.handle.WebSocketSessionContainer;
import io.github.qyvlik.jsonrpclite.jsonrpc.method.RpcMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.qyvlik.jsonrpclite.handle.WebSocketDispatch;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class BeanConfig {

    @Autowired
    private List<RpcMethod> rpcMethodList;

    @Autowired
    private List<WebSocketFilter> filters;

    @Bean("executor")
    public Executor executor() {
        return Executors.newFixedThreadPool(4);
    }

    @Bean("scheduledExecutorService")
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean("webSocketSessionContainer")
    public WebSocketSessionContainer webSocketSessionContainer() {
        return new WebSocketSessionContainer();
    }

    @Bean("gameDispatch")
    public WebSocketDispatch gameDispatch(@Autowired Executor executor,
                                          @Autowired WebSocketSessionContainer webSocketSessionContainer) {
        WebSocketDispatch webSocketDispatch = new WebSocketDispatch();

        webSocketDispatch.setExecutor(executor);
        webSocketDispatch.setWebSocketSessionContainer(webSocketSessionContainer);
        webSocketDispatch.addRpcMethodList(rpcMethodList);
        webSocketDispatch.addFilterList(filters);

        return webSocketDispatch;
    }
}
