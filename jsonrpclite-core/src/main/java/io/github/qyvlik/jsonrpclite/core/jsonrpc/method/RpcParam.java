package io.github.qyvlik.jsonrpclite.core.jsonrpc.method;

import java.io.Serializable;

public abstract class RpcParam implements Serializable {

    private String typeName;
    private String paramName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public boolean canConvert(Object param) {
        return canConvertInternal(param);
    }

    protected abstract boolean canConvertInternal(Object param);
}
