package io.github.qyvlik.jsonrpclite.example.game.method.param;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.method.RpcParam;

import java.math.BigDecimal;

public class DecimalParam extends RpcParam {
    public DecimalParam(String paramName) {
        this.setTypeName("decimal");
        this.setParamName(paramName);
    }

    @Override
    protected boolean canConvertInternal(Object param) {
        if (param == null) {
            return false;
        }
        try {
            new BigDecimal(param.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}