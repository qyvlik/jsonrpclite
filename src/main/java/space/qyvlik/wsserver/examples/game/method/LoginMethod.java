package space.qyvlik.wsserver.examples.game.method;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import space.qyvlik.wsserver.jsonrpc.entity.request.RequestObject;
import space.qyvlik.wsserver.jsonrpc.entity.response.ResponseObject;
import space.qyvlik.wsserver.jsonrpc.method.RpcMethod;
import space.qyvlik.wsserver.jsonrpc.method.RpcParam;
import space.qyvlik.wsserver.jsonrpc.method.RpcParams;

@Service
public class LoginMethod extends RpcMethod {

    public LoginMethod() {
        super("pub.login",
                new RpcParams(2, 2, Lists.newArrayList(
                        new NumberParam(),
                        new StringParam()
                )));
    }

    @Override
    protected ResponseObject callInternal(WebSocketSession session, RequestObject requestObject) {
        ResponseObject<String> responseObject = new ResponseObject<String>();
        responseObject.setResult("success");
        return responseObject;
    }

    static class StringParam extends RpcParam {

        public StringParam() {
            this.setTypeName("string");
            this.setParamName("userName");
        }

        @Override
        protected boolean canConvertInternal(Object param) {
            if (param == null) {
                return false;
            }
            if (!(param instanceof String)) {
                return false;
            }
            return true;
        }
    }

    static class NumberParam extends RpcParam {

        public NumberParam() {
            this.setTypeName("long");
            this.setParamName("userId");
        }

        @Override
        protected boolean canConvertInternal(Object param) {
            if (param == null) {
                return false;
            }
            if (!(param instanceof Number)) {
                return false;
            }
            return true;
        }
    }
}
