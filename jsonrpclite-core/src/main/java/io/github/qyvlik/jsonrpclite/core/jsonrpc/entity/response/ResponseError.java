package io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response;

import java.io.Serializable;

public class ResponseError implements Serializable {
    private Integer code;
    private String message;
    private Object data;

    public ResponseError() {

    }

    public ResponseError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                // ", data=" + data +
                '}';
    }
}
