package io.github.qyvlik.jsonrpclite.core.jsonsub.sub;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SubRequestObject implements Serializable {
    private String channel;
    private Boolean subscribe;
    private List params;
    private Map<String, Object> paramMap;

    public SubRequestObject() {

    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public List getParams() {
        return params;
    }

    public void setParams(List params) {
        this.params = params;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
}
