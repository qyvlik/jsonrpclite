package io.github.qyvlik.jsonrpclite.core.jsonrpc.rpcinvoker;


import io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation.RpcMethod;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.annotation.RpcService;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcDispatcher {

    private ApplicationContext applicationContext;
    private Map<String, RpcMethodGroup> groupMap;

    public RpcDispatcher(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.groupMap = new ConcurrentHashMap<>();
    }

    public RpcMethodGroup getGroup(String group) {
        return groupMap.get(group);
    }

    public void initInvoker() {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(RpcService.class);
        Collection<Object> beanList = map.values();

        if (beanList.isEmpty()) {
            return;
        }

        for (Object bean : beanList) {

            Class clazz = bean.getClass();

            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                if (!method.isAnnotationPresent(RpcMethod.class)) {
                    continue;
                }

                RpcMethod rpcMethodMetaInfo = method.getAnnotation(RpcMethod.class);

                if (rpcMethodMetaInfo == null) {
                    continue;
                }

                String[] rpcMethodNames = rpcMethodMetaInfo.value();

                for (String rpcMethodName : rpcMethodNames) {
                    RpcMethodInvoker rpcMethodInvoker = new RpcMethodInvoker(
                            bean, rpcMethodMetaInfo.group(), rpcMethodName, method);
                    this.add(rpcMethodInvoker);
                }
            }
        }
    }

    private void add(RpcMethodInvoker invoker) {
        RpcMethodGroup rpcMethodGroup = groupMap.computeIfAbsent(
                invoker.getGroup(), k -> new RpcMethodGroup(invoker.getGroup()));
        rpcMethodGroup.add(invoker);
    }

}
