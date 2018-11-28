package io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response;

import java.io.Serializable;

// http://www.jsonrpc.org/specification
public class ResponseObject<T> implements Serializable {
    private Long id;
    private String method;
    private T result;
    private ResponseError error;

    public ResponseObject() {

    }

    public ResponseObject(T result) {
        this.result = result;
    }

    public ResponseObject(Long id, String method, Integer code, String message) {
        this.id = id;
        this.method = method;
        this.error = new ResponseError(code, message);
    }

    public ResponseObject(Integer code, String message) {
        this.error = new ResponseError(code, message);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ResponseError getError() {
        return error;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ResponseObject{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", result=" + result +
                ", error=" + error +
                '}';
    }
}
