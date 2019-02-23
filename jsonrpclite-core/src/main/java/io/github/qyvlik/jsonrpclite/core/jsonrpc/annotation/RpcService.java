package io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
    String value() default "";
}
