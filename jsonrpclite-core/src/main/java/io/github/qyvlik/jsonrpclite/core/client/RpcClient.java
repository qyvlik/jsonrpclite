package io.github.qyvlik.jsonrpclite.core.client;

import com.alibaba.fastjson.JSON;
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
    private int sendTimeLimit = 1000;
    private int bufferSizeLimit = 10000;
    private String wsUrl;
    private OnlineClient onlineClient;
    private WSConnector connector;

    public RpcClient(String wsUrl, int sendTimeLimit, int bufferSizeLimit) {
        this.sendTimeLimit = sendTimeLimit;
        this.bufferSizeLimit = bufferSizeLimit;
        this.wsUrl = wsUrl;
    }

    @Deprecated
    public RpcClient(String wsUrl) {
        this(wsUrl, 1000, 10000);
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
        return onlineClient != null && onlineClient.isOpen();
    }

    public Future<Boolean> startup() {
        init();
        return connector.startupAsync();
    }

    private void init() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(10 * 1024 * 1024);
        connector = new WSConnector(wsUrl, container, new RpcClientTextHandler());
    }

    public void listenSub(String channel, Boolean subscribe, List params, ChannelMessageHandler handler) throws IOException {
        if (!isOpen()) {
            throw new RuntimeException("callRpcAsyncInternal failure webSocketSession is not open");
        }

        if (subscribe != null && subscribe) {
            onlineClient.getChannelCallback().put(channel, handler);
        } else {
            onlineClient.getChannelCallback().remove(channel);
        }

        SubRequestObject subRequestObject = new SubRequestObject();
        subRequestObject.setChannel(channel);
        subRequestObject.setSubscribe(subscribe);
        subRequestObject.setParams(params);

        onlineClient.sendText(new TextMessage(JSON.toJSONString(subRequestObject)));
    }

    public void callRpcAsync(String method, List params) throws Exception {
        callRpcAsync(method, params, true);
    }

    /**
     * @param method         rpc method
     * @param params         rpc params
     * @param ignoreResponse ignore rpc response
     * @return
     * @throws Exception
     */
    @Deprecated
    public Future<ResponseObject> callRpcAsync(String method, List params, boolean ignoreResponse) throws Exception {
        if (!isOpen()) {
            throw new RuntimeException("callRpcAsync failure webSocketSession is not open");
        }

        Long id = rpcRequestCounter.getAndIncrement();
        RequestObject requestObject = new RequestObject();
        requestObject.setId(id);
        requestObject.setMethod(method);
        requestObject.setParams(params);
        return callRpcAsyncInternal(requestObject, ignoreResponse);
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
            onlineClient.getRpcCallback().put(requestObject.getId(), rpcResponseFuture);
        }

        onlineClient.sendText(new TextMessage(JSON.toJSONString(requestObject)));

        return rpcResponseFuture;
    }

    private class RpcClientTextHandler extends AbstractWebSocketHandler {

        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            onlineClient = new OnlineClient(session, getSendTimeLimit(), getBufferSizeLimit());
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message)
                throws Exception {
            if (onlineClient != null
                    && onlineClient.getSession().getId().equals(session.getId())
                    && onlineClient.isOpen()) {
                onlineClient.onTextMessage(message);
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            onlineClient.onClose(session, status);
            onlineClient = null;
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            onlineClient.onError(session, exception);
        }
    }
}
