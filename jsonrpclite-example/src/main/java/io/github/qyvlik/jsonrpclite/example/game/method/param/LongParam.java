package io.github.qyvlik.jsonrpclite.example.game.method.param;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.method.RpcParam;

public class LongParam extends RpcParam {
    public LongParam(String paramName) {
        this.setTypeName("long");
        this.setParamName(paramName);
    }

    @Override
    protected boolean canConvertInternal(Object param) {
        if (param == null) {
            return false;
        }
        try {
            Long.parseLong(param.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
