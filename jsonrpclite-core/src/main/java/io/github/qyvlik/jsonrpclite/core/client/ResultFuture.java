package io.github.qyvlik.jsonrpclite.core.client;

import java.util.concurrent.*;

public class ResultFuture<T> implements Future<T> {
    protected final CountDownLatch latch = new CountDownLatch(1);
    protected T result = null;

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
    public T get() throws InterruptedException, ExecutionException {
        latch.await();
        return this.result;
    }

    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return this.result;
        } else {
            throw new TimeoutException();
        }
    }

    public void setResult(T result) {
        if (latch.getCount() <= 0) {
            throw new RuntimeException("setResult failure latch already countDown!");
        }
        this.result = result;
        latch.countDown();
    }
}
