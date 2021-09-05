package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class DamageComponent implements Component, Pool.Poolable {

    public float damage = 1;

    @Override
    public void reset() {
        damage = 1;
    }

}
