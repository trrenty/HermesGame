package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.hermes.common.Action;

public class ActionOnRemoveComponent implements Component, Pool.Poolable {
    public Action onRemove = null;


    @Override
    public void reset() {
        onRemove = null;
    }
}
