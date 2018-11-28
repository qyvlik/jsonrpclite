package io.github.qyvlik.jsonrpclite.core.jsonrpc.method;

import java.io.Serializable;
import java.util.List;

public class RpcParams implements Serializable {
    private int maxParamSize;
    private int minParamSize;
    private List<RpcParam> paramTypeList;

    public RpcParams() {

    }

    public RpcParams(int maxParamSize, int minParamSize, List<RpcParam> paramTypeList) {
        this.maxParamSize = maxParamSize;
        this.minParamSize = minParamSize;
        this.paramTypeList = paramTypeList;
    }

    public RpcParams(List<RpcParam> paramTypeList) {
        this.paramTypeList = paramTypeList;
        this.maxParamSize = paramTypeList.size();
        this.minParamSize = paramTypeList.size();
    }

    public int getMaxParamSize() {
        return maxParamSize;
    }

    public void setMaxParamSize(int maxParamSize) {
        this.maxParamSize = maxParamSize;
    }

    public int getMinParamSize() {
        return minParamSize;
    }

    public void setMinParamSize(int minParamSize) {
        this.minParamSize = minParamSize;
    }

    public List<RpcParam> getParamTypeList() {
        return paramTypeList;
    }

    public void setParamTypeList(List<RpcParam> paramTypeList) {
        this.paramTypeList = paramTypeList;
    }
}
