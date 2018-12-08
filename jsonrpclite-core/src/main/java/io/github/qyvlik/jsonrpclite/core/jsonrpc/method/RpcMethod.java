package io.github.qyvlik.jsonrpclite.core.jsonrpc.method;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseError;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.Executor;

public abstract class RpcMethod {
    private String group;
    private String method;
    private RpcParams rpcParams;
    private Executor executor;

    public RpcMethod(String group, String method, RpcParams rpcParams) {
        this.group = group;
        this.method = method;
        this.rpcParams = rpcParams;
    }

    public String getGroup() {
        return group;
    }

    public String getMethod() {
        return method;
    }

    public RpcParams getRpcParams() {
        return rpcParams;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public RpcParamCheckError checkParams(RequestObject requestObject) {
        int paramSize = requestObject.getParams() != null
                ? requestObject.getParams().size()
                : 0;

        for (int i = 0; i < paramSize; i++) {
            Object param = requestObject.getParams().get(i);
            RpcParam rpcParam = rpcParams.getParamTypeList().get(i);
            if (!rpcParam.canConvert(param)) {
                return new RpcParamCheckError(
                        rpcParam.getParamName(), rpcParam.getTypeName(), i);
            }
        }
        return null;
    }

    public ResponseObject call(WebSocketSession session, RequestObject requestObject) {
        ResponseObject response = null;
        try {
            int paramSize = requestObject.getParams() != null
                    ? requestObject.getParams().size()
                    : 0;

            int minSize = rpcParams.getMinParamSize();
            int maxSize = rpcParams.getMaxParamSize();

            if (paramSize > maxSize || paramSize < minSize) {
                return new ResponseObject(
                        requestObject.getId(),
                        requestObject.getMethod(),
                        500,
                        "paramSize >= " + minSize + " or <= " + maxSize);
            }

            RpcParamCheckError checkParamResult = checkParams(requestObject);
            if (checkParamResult != null) {

                ResponseError error =
                        new ResponseError(500, "param type not match");
                error.setData(checkParamResult);

                ResponseObject responseObject = new ResponseObject();
                responseObject.setId(requestObject.getId());
                responseObject.setMethod(requestObject.getMethod());
                responseObject.setError(error);

                return responseObject;
            }

            response = callInternal(session, requestObject);
        } catch (Exception e) {
            response = new ResponseObject(500, e.getMessage());
        }

        if (response.getId() == null) {
            response.setId(requestObject.getId());
        }

        if (StringUtils.isBlank(response.getMethod())) {
            response.setMethod(requestObject.getMethod());
        }

        return response;
    }

    protected abstract ResponseObject callInternal(WebSocketSession session, RequestObject requestObject);
}
