package io.github.qyvlik.jsonrpclite.examples.game.method.param;


import io.github.qyvlik.jsonrpclite.jsonrpc.method.RpcParam;

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
