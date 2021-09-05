package com.hermes.common;

public interface Task {
    void update(float delta);
    boolean isDone();
    void cleanUp();
}
