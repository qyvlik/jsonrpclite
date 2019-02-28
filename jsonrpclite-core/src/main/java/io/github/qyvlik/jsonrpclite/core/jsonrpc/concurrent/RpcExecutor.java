package io.github.qyvlik.jsonrpclite.core.jsonrpc.concurrent;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.Executor;

public interface RpcExecutor {

    Executor defaultExecutor();

    Executor getByRequest(WebSocketSession session, RequestObject requestObject);
}
