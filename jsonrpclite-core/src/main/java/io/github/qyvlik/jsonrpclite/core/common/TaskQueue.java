package io.github.qyvlik.jsonrpclite.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class TaskQueue<T> implements ITaskQueue<T> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private LinkedBlockingQueue<T> runnableQueue;

    public TaskQueue() {
        runnableQueue = new LinkedBlockingQueue<T>();
    }

    @Override
    public boolean submit(T runnable) {
        return runnableQueue.add(runnable);
    }

    @Override
    public int taskSize() {
        return runnableQueue.size();
    }

    @Override
    public void exec() {
        while (true) {
            try {
                T runnable = runnableQueue.take();
                if (runnable != null) {
                    execTask(runnable);
                }
            } catch (Exception e) {
                logger.info("error:{}", e);
            }
        }
    }
}
