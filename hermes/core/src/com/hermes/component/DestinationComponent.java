package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class DestinationComponent implements Component, Pool.Poolable {
    public Array<Vector2> destinations = new Array<>();
    private static final Logger log = new Logger(DestinationComponent.class.getName(), Logger.DEBUG);
    public void addDestination(float x, float y) {
        destinations.add(new Vector2(x, y));

    }

    public void addDestination(Entity entity) {
        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);

        if (tc != null && dc != null) {
            addDestination(tc.x + dc.width/2, tc.y + dc.height / 2);
        }
    }

    @Override
    public void reset() {
        destinations.clear();
    }
}
