package io.github.qyvlik.jsonrpclite.core.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseError;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker.RpcDispatcher;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker.RpcExecutor;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker.RpcMethodGroup;
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
import java.util.concurrent.Executor;

public class WebSocketDispatch extends TextWebSocketHandler {
    private final List<WebSocketFilter> filterList = Lists.newLinkedList();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RpcDispatcher rpcDispatcher;
    private String group;
    private RpcExecutor rpcExecutor;
    private WebSocketSessionContainer webSocketSessionContainer;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public RpcDispatcher getRpcDispatcher() {
        return rpcDispatcher;
    }

    public void setRpcDispatcher(RpcDispatcher rpcDispatcher) {
        this.rpcDispatcher = rpcDispatcher;
    }

    public RpcMethodGroup getRpcMethodGroup() {
        return rpcDispatcher.getGroup(this.group);
    }

    public RpcExecutor getRpcExecutor() {
        return rpcExecutor;
    }

    public void setRpcExecutor(RpcExecutor rpcExecutor) {
        this.rpcExecutor = rpcExecutor;
    }

    public WebSocketSessionContainer getWebSocketSessionContainer() {
        return webSocketSessionContainer;
    }

    public void setWebSocketSessionContainer(WebSocketSessionContainer webSocketSessionContainer) {
        this.webSocketSessionContainer = webSocketSessionContainer;
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

        RpcMethodGroup rpcMethodGroup = getRpcMethodGroup();

        if (rpcMethodGroup == null) {
            ResponseObject response = new ResponseObject();
            response.setId(requestObject.getId());
            response.setMethod(requestObject.getMethod());
            response.setError(new ResponseError(400, "group " + getGroup() + " not exist"));
            safeSend(session, response);
            return;
        }

        Executor methodInternalExecutor = rpcExecutor.getByRequest(session, requestObject);

        Executor methodExecutor = methodInternalExecutor != null ?
                methodInternalExecutor : rpcExecutor.defaultExecutor();

        methodExecutor.execute(new Runnable() {
            @Override
            public void run() {

                ResponseObject responseObject = rpcMethodGroup.callRpcMethod(session, requestObject);
                if (requestObject.getIgnore() == null || !requestObject.getIgnore()) {
                    safeSend(session, responseObject);
                }
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
