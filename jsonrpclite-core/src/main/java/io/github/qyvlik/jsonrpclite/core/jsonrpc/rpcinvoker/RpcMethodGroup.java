package io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request.RequestObject;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseError;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcMethodGroup implements Serializable {
    private String group;
    private Map<String, RpcMethodInvoker> invokerMap;

    public RpcMethodGroup(String group) {
        this.group = group;
        this.invokerMap = new ConcurrentHashMap<>();
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, RpcMethodInvoker> getInvokerMap() {
        return invokerMap;
    }

    public void setInvokerMap(Map<String, RpcMethodInvoker> invokerMap) {
        this.invokerMap = invokerMap;
    }

    public void add(RpcMethodInvoker invoker) {
        invokerMap.put(invoker.getRpcMethodName(), invoker);
    }

    public ResponseObject<Object> callRpcMethod(WebSocketSession session, RequestObject requestObject) {
        ResponseObject<Object> response = new ResponseObject<Object>();
        response.setId(requestObject.getId());
        response.setMethod(requestObject.getMethod());

        try {
            Object result = invoke(session, requestObject.getMethod(), requestObject.getParams());
            response.setResult(result);
        } catch (RpcInvokeException e) {
            ResponseError error = new ResponseError();
            switch (e.getInvokeError()) {
                case CallError:
                    error.setCode(500);
                    error.setMessage(e.getMessage());
                    break;
                case LostParam:
                    error.setCode(400);
                    error.setMessage(e.getMessage());
                    break;
                case SystemError:
                    error.setCode(500);
                    error.setMessage(e.getMessage());
                    break;
                case GroupNotExist:
                    error.setCode(404);
                    error.setMessage(e.getMessage());
                    break;
                case ParamNotMatch:
                    error.setCode(400);
                    error.setMessage(e.getMessage());
                    break;
                case MethodNotExist:
                    error.setCode(404);
                    error.setMessage(e.getMessage());
                    break;
                case Unknown:
                default:
                    error.setCode(500);
                    error.setMessage(e.getMessage());
                    break;
            }
            response.setError(error);
        }

        return response;
    }


    private Object invoke(WebSocketSession session, String rpcMethodName, List params)
            throws RpcInvokeException {
        RpcMethodInvoker invoker = invokerMap.get(rpcMethodName);

        if (invoker == null) {
            throw new RpcInvokeException("method :" + rpcMethodName + " not exist", RpcInvokeException.InvokeError.MethodNotExist);
        }

        // todo check if need session

        try {
            return invoker.invoker(params.toArray());
        } catch (IllegalAccessException e) {
            throw new RpcInvokeException("invoker failure", e, RpcInvokeException.InvokeError.SystemError);
        } catch (IllegalArgumentException e) {
            throw new RpcInvokeException("invoker failure", e, RpcInvokeException.InvokeError.ParamNotMatch);
        } catch (Exception e) {
            throw new RpcInvokeException("invoker failure", e, RpcInvokeException.InvokeError.CallError);
        }
    }
}
