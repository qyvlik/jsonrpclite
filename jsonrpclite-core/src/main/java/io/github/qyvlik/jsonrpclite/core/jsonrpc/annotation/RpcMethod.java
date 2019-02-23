package io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcMethod {
    String[] value();     // method names

    String group() default "";
}
