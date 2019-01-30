package io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RequestObject implements Serializable {
    private Long id;
    private Boolean ignore;                     // ignore response
    private String method;                      // method
    private List params;                        // params
    private Map<String, Object> paramMap;

    public RequestObject() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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
