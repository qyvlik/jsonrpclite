package io.github.qyvlik.jsonrpclite.common;

public interface ITaskQueue<T> {

    String group();

    boolean submit(T task);

    void execTask(T task);

    int taskSize();

    void exec();
}