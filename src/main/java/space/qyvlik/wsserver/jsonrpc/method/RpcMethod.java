package space.qyvlik.wsserver.jsonrpc.method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import space.qyvlik.wsserver.jsonrpc.entity.request.RequestObject;
import space.qyvlik.wsserver.jsonrpc.entity.response.ResponseObject;

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

    public boolean checkParams(RequestObject requestObject) {
        int paramSize = requestObject.getParams() != null
                ? requestObject.getParams().size()
                : 0;
        int minSize = rpcParams.getMinParamSize();
        int maxSize = rpcParams.getMaxParamSize();

        if (paramSize > maxSize || paramSize < minSize) {
            return false;
        }

        if (paramSize == 0) {
            return true;
        }

        for (int i = 0; i < paramSize; i++) {
            Object param = requestObject.getParams().get(i);
            RpcParam rpcParam = rpcParams.getParamTypeList().get(i);
            if (!rpcParam.canConvert(param)) {
                return false;
            }
        }
        return true;
    }

    public ResponseObject call(WebSocketSession session, RequestObject requestObject) {
        ResponseObject response = null;
        try {

            boolean checkParamResult = checkParams(requestObject);
            if (!checkParamResult) {
                response = new ResponseObject(500, "param type not match");
                return response;
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
