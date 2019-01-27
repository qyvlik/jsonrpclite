package io.github.qyvlik.jsonrpclite.core.client;

import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;

import java.util.concurrent.*;

public class RpcResponseFuture implements Future<ResponseObject> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private ResponseObject responseObject = null;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @Override
    public ResponseObject get() throws InterruptedException, ExecutionException {
        latch.await();
        return this.responseObject;
    }

    @Override
    public ResponseObject get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return this.responseObject;
        } else {
            throw new TimeoutException();
        }
    }

    public void setResponseObject(ResponseObject responseObject) {
        this.responseObject = responseObject;
        latch.countDown();
    }
}
