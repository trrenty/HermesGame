package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.hermes.common.Inventory;

public class PlayerComponent implements Component, Pool.Poolable {

    public Inventory inventory = new Inventory();
    public Vector2 checkPoint = new Vector2();

    @Override
    public void reset() {
        inventory.emptyInventory();
        checkPoint.set(0, 0);
    }
}
