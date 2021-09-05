package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.hermes.common.Task;

public class TaskHolderComponent implements Component, Pool.Poolable {
    public Task task = null;
    @Override
    public void reset() {
        task = null;
    }

    public boolean isDone() {
        return task.isDone();
    }

    public void update(float delta) {
        task.update(delta);
    }

    public void cleanUp() {
        task.cleanUp();
    }
}
