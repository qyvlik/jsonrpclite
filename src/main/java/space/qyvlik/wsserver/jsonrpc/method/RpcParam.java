package space.qyvlik.wsserver.jsonrpc.method;

import java.io.Serializable;

public abstract class RpcParam implements Serializable {
    private Class clazz;

    public RpcParam(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public boolean canConvert(Object param) {
        return canConvertInternal(getClazz(), param);
    }

    protected abstract boolean canConvertInternal(Class clazz, Object param);
}
