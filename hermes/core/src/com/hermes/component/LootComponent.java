package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class LootComponent implements Component, Pool.Poolable {

    public String itemName;
    public String layerName = "objects";


    @Override
    public void reset() {
        itemName = null;
        layerName = "objects";
    }
}
