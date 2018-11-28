package io.github.qyvlik.jsonrpclite.core.jsonrpc.method;

import java.io.Serializable;

public class RpcParamCheckError implements Serializable {
    private String paramName;
    private String typeName;
    private Integer paramIndex;

    public RpcParamCheckError() {

    }

    public RpcParamCheckError(String paramName, String typeName, Integer paramIndex) {
        this.paramName = paramName;
        this.typeName = typeName;
        this.paramIndex = paramIndex;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getParamIndex() {
        return paramIndex;
    }

    public void setParamIndex(Integer paramIndex) {
        this.paramIndex = paramIndex;
    }
}
