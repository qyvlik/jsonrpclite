package io.github.qyvlik.jsonrpclite.core.client;

import com.alibaba.fastjson.JSON;
import io.github.qyvlik.jsonrpclite.core.client.impl.RpcResponseFuture;
import io.github.qyvlik.jsonrpclite.core.client.impl.WSClient;
import io.github.qyvlik.jsonrpclite.core.client.impl.WSConnector;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;
import io.github.qyvlik.jsonrpclite.core.jsonsub.sub.SubRequestObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class RpcClient {
    private final AtomicLong rpcRequestCounter = new AtomicLong(0);
    private int sendTimeLimit = 500;
    private int bufferSizeLimit = 10000;
    private String wsUrl;
    private WSClient client;
    private WSConnector connector;

    public RpcClient(String wsUrl, int sendTimeLimit, int bufferSizeLimit) {
        this.sendTimeLimit = sendTimeLimit;
        this.bufferSizeLimit = bufferSizeLimit;
        this.wsUrl = wsUrl;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public int getSendTimeLimit() {
        return sendTimeLimit;
    }

    public void setSendTimeLimit(int sendTimeLimit) {
        this.sendTimeLimit = sendTimeLimit;
    }

    public int getBufferSizeLimit() {
        return bufferSizeLimit;
    }

    public void setBufferSizeLimit(int bufferSizeLimit) {
        this.bufferSizeLimit = bufferSizeLimit;
    }

    public boolean isOpen() {
        return client != null && client.isOpen();
    }

    public Future<Boolean> startup() {
        initWSConnect();
        return connector.startupAsync();
    }

    private void initWSConnect() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(10 * 1024 * 1024);
        connector = new WSConnector(wsUrl, container, new RpcClientTextHandler());
    }

    public void listenSub(String channel, Boolean subscribe, List params, ChannelMessageHandler handler) throws IOException {
        if (!isOpen()) {
            throw new RuntimeException("callRpcAsyncInternal failure webSocketSession is not open");
        }

        if (subscribe != null && subscribe) {
            client.getChannelCallback().put(channel, handler);
        } else {
            client.getChannelCallback().remove(channel);
        }

        SubRequestObject subRequestObject = new SubRequestObject();
        subRequestObject.setChannel(channel);
        subRequestObject.setSubscribe(subscribe);
        subRequestObject.setParams(params);

        client.sendText(new TextMessage(JSON.toJSONString(subRequestObject)));
    }

    public void callRpcAsync(String method, List params) throws Exception {
        if (!isOpen()) {
            throw new RuntimeException("callRpcAsync failure webSocketSession is not open");
        }

        Long id = rpcRequestCounter.getAndIncrement();
        RequestObject requestObject = new RequestObject();
        requestObject.setId(id);
        requestObject.setMethod(method);
        requestObject.setParams(params);
        callRpcAsyncInternal(requestObject, true);
    }

    public Future<ResponseObject> callRpc(String method, List params) throws Exception {
        if (!isOpen()) {
            throw new RuntimeException("callRpc failure webSocketSession is not open");
        }

        Long id = rpcRequestCounter.getAndIncrement();
        RequestObject requestObject = new RequestObject();
        requestObject.setId(id);
        requestObject.setMethod(method);
        requestObject.setParams(params);
        return callRpcAsyncInternal(requestObject, false);
    }

    private Future<ResponseObject> callRpcAsyncInternal(RequestObject requestObject,
                                                        boolean ignoreResponse)
            throws IOException {
        if (requestObject == null || requestObject.getId() == null) {
            throw new RuntimeException("callRpcAsyncInternal failure requestObject is null or requestObject's id is null");
        }

        RpcResponseFuture rpcResponseFuture = null;
        if (!ignoreResponse) {
            rpcResponseFuture = new RpcResponseFuture();
            client.getRpcCallback().put(requestObject.getId(), rpcResponseFuture);
        }

        client.sendText(new TextMessage(JSON.toJSONString(requestObject)));

        return rpcResponseFuture;
    }

    private class RpcClientTextHandler extends AbstractWebSocketHandler {

        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            client = new WSClient(session, getSendTimeLimit(), getBufferSizeLimit());
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message)
                throws Exception {
            if (client != null
                    && client.getSession().getId().equals(session.getId())
                    && client.isOpen()) {
                client.onTextMessage(message);
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            if (client != null) {
                client.onClose(session, status);
                client = null;
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            client.onError(session, exception);
        }
    }
}
