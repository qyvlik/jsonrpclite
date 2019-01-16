package io.github.qyvlik.jsonrpclite.core.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseError;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.method.RpcMethod;
import io.github.qyvlik.jsonrpclite.core.jsonsub.pub.ChannelMessage;
import io.github.qyvlik.jsonrpclite.core.jsonsub.sub.SubRequestObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class WebSocketDispatch extends TextWebSocketHandler {
    private final List<WebSocketFilter> filterList = Lists.newLinkedList();
    private final Map<String, RpcMethod> methodMap = Maps.newConcurrentMap();
    private String group;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private WebSocketSessionContainer webSocketSessionContainer;
    private Executor executor;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public WebSocketSessionContainer getWebSocketSessionContainer() {
        return webSocketSessionContainer;
    }

    public void setWebSocketSessionContainer(WebSocketSessionContainer webSocketSessionContainer) {
        this.webSocketSessionContainer = webSocketSessionContainer;
    }

    public void addRpcMethodList(List<RpcMethod> rpcMethodList) {
        if (rpcMethodList == null || rpcMethodList.size() == 0) {
            return;
        }
        for (RpcMethod rpcMethod : rpcMethodList) {
            addRpcMethod(rpcMethod);
        }
    }

    public boolean addRpcMethod(RpcMethod rpcMethod) {
        if (StringUtils.isNotBlank(this.getGroup())
                && this.getGroup().equals(rpcMethod.getGroup())) {

            methodMap.put(rpcMethod.getMethod(), rpcMethod);
            return true;
        }

        return false;
    }

    public void addFilterList(List<WebSocketFilter> filterList) {
        if (filterList == null || filterList.size() == 0) {
            return;
        }
        for (WebSocketFilter filter : filterList) {
            addFilter(filter);
        }
    }

    public boolean addFilter(WebSocketFilter filter) {
        if (StringUtils.isNotBlank(this.getGroup())
                && this.getGroup().equals(filter.getGroup())) {
            filterList.add(filter);
            return true;
        }
        return false;
    }

    public List<WebSocketFilter> getFilterList() {
        return filterList;
    }

    public Map<String, RpcMethod> getMethodMap() {
        return methodMap;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        ResponseObject<String> response = new ResponseObject<>();
        String payload = message.getPayload();
        if (StringUtils.isBlank(payload) || (!payload.startsWith("{") && !payload.startsWith("["))) {
            response.setError(new ResponseError(400, "payload is not json"));
            safeSend(session, response);
            return;
        }

        if (payload.startsWith("[")) {
            response.setError(new ResponseError(400, "not support batch call"));
            safeSend(session, response);
            return;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(payload);
        } catch (Exception e) {
            response.setError(new ResponseError(400, "payload is not json"));
            safeSend(session, response);
            return;
        }

        try {
            if (jsonObject.containsKey("method")) {
                RequestObject requestObject = jsonObject.toJavaObject(RequestObject.class);
                handleRpc(session, requestObject);
            } else if (jsonObject.containsKey("channel")) {
                SubRequestObject subRequestObject = jsonObject.toJavaObject(SubRequestObject.class);
                handleSub(session, subRequestObject);
            } else {
                response.setError(new ResponseError(400,
                        "payload not match type in system"));
                safeSend(session, response);
            }
        } catch (Exception e) {
            response.setError(new ResponseError(500, e.getMessage()));
            safeSend(session, response);
        }
    }

    private void handleRpc(WebSocketSession session, RequestObject requestObject) {
        for (WebSocketFilter filter : filterList) {
            boolean filterResult = filter.filter(session, requestObject);
            if (!filterResult) {
                return;
            }
        }

        final RpcMethod rpcMethod = methodMap.get(requestObject.getMethod());
        ResponseObject response = new ResponseObject();
        response.setId(requestObject.getId());
        response.setMethod(requestObject.getMethod());

        if (rpcMethod == null) {
            response.setError(new ResponseError(404,
                    "method " + requestObject.getMethod() + " not found"));
            safeSend(session, response);
            return;
        }

        Executor methodInternalExecutor = rpcMethod.getExecutorByRequest(requestObject);

        Executor methodExecutor = methodInternalExecutor != null ?
                methodInternalExecutor : executor;

        methodExecutor.execute(new Runnable() {
            @Override
            public void run() {
                safeSend(session, rpcMethod.call(session, requestObject));
            }
        });
    }

    private void handleSub(WebSocketSession session, SubRequestObject subRequestObject) {
        for (WebSocketFilter filter : filterList) {
            boolean filterResult = filter.filter(session, subRequestObject);
            if (!filterResult) {
                return;
            }
        }

        if (webSocketSessionContainer == null) {
            return;
        }

        if (subRequestObject.getSubscribe() == null || !subRequestObject.getSubscribe()) {
            webSocketSessionContainer.onUnSub(subRequestObject.getChannel(), session);
        } else {
            webSocketSessionContainer.onSub(subRequestObject, session);
        }

        ChannelMessage<String> channelMessage = new ChannelMessage<>();
        channelMessage.setChannel(subRequestObject.getChannel());

        if (subRequestObject.getSubscribe() == null || !subRequestObject.getSubscribe()) {
            channelMessage.setResult("unsubscribe");
        } else {
            channelMessage.setResult("subscribe");
        }
        safeSend(session, channelMessage);
    }


    private boolean safeSend(WebSocketSession session, Object obj) {

        if (webSocketSessionContainer != null) {
            return webSocketSessionContainer.safeSend(session, new TextMessage(JSON.toJSONString(obj)));
        }

        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(JSON.toJSONString(obj)));
            }
            return true;
        } catch (Exception e) {
            logger.error("safeSend:{}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (webSocketSessionContainer != null) {
            webSocketSessionContainer.onOpen(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        if (webSocketSessionContainer != null) {
            webSocketSessionContainer.onClose(session);
        }
    }
}
