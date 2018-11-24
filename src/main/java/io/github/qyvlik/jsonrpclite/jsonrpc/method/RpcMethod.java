package io.github.qyvlik.jsonrpclite.jsonrpc.method;

import io.github.qyvlik.jsonrpclite.jsonrpc.entity.response.ResponseError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import io.github.qyvlik.jsonrpclite.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.jsonrpc.entity.response.ResponseObject;

public abstract class RpcMethod {
    private String method;
    private RpcParams rpcParams;

    public RpcMethod(String method, RpcParams rpcParams) {
        this.method = method;
        this.rpcParams = rpcParams;
    }

    public String getMethod() {
        return method;
    }

    public RpcParams getRpcParams() {
        return rpcParams;
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
