package io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker;

public class RpcInvokeException extends RuntimeException {

    private InvokeError invokeError;

    public RpcInvokeException(String message, InvokeError invokeError) {
        super(message);
        this.invokeError = invokeError;
    }

    public RpcInvokeException(String message, Throwable cause, InvokeError invokeError) {
        super(message, cause);
        this.invokeError = invokeError;
    }

    public InvokeError getInvokeError() {
        return invokeError;
    }

    public enum InvokeError {
        GroupNotExist,
        MethodNotExist,
        LostParam,
        ParamNotMatch,
        SystemError,
        CallError,
        Unknown
    }
}
