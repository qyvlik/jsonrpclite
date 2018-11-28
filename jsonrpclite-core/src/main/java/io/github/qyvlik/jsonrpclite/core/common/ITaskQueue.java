package io.github.qyvlik.jsonrpclite.core.common;

public interface ITaskQueue<T> {

    String group();

    boolean submit(T task);

    void execTask(T task);

    int taskSize();

    void exec();
}
