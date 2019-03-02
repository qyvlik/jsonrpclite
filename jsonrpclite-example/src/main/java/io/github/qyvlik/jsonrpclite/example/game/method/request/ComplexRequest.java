package io.github.qyvlik.jsonrpclite.example.game.method.request;

import java.io.Serializable;
import java.util.List;

public class ComplexRequest implements Serializable {
    private String param1;
    private String param2;
    private List param3;

    public ComplexRequest() {

    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public List getParam3() {
        return param3;
    }

    public void setParam3(List param3) {
        this.param3 = param3;
    }
}
