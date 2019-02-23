package io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcMethodInvoker {
    private Object object;
    private String group;
    private String rpcMethodName;
    private Method method;

    public RpcMethodInvoker(Object object, String group, String rpcMethodName, Method method) {
        this.object = object;
        this.group = group;
        this.rpcMethodName = rpcMethodName;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRpcMethodName() {
        return rpcMethodName;
    }

    public void setRpcMethodName(String rpcMethodName) {
        this.rpcMethodName = rpcMethodName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object invoker(Object... args)
            throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        return method.invoke(object, args);
    }
}
