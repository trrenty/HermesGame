package com.hermes.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.scripts.IScript;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class RopeScript implements IScript {

    private final Entity ropedObject;
    private final Vector2 ceiling;
    private final Vector2 ropePos;
    private Entity rope;

    private float initialRopeHeight;
    private float initialRopeWidth;

    public RopeScript(Entity ropedObject, Vector2 ceiling) {
        this(ropedObject, ceiling.x, ceiling.y);
    }

    public RopeScript(Entity ropedObject, float x, float y) {
        ceiling = new Vector2(x, y);
        ropePos = new Vector2();
        this.ropedObject = ropedObject;
    }


    @Override
    public void init(Entity entity) {
        this.rope = entity;
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        this.initialRopeHeight = dimensionsComponent.height;
        this.initialRopeWidth = dimensionsComponent.width / 2;

        ceiling.x -= initialRopeWidth;

    }

    @Override
    public void act(float delta) {

        TransformComponent ropedObjTransform = ComponentRetriever.get(ropedObject, TransformComponent.class);
        TransformComponent ropeTransform = ComponentRetriever.get(rope, TransformComponent.class);

        DimensionsComponent dimensionsComponent = ComponentRetriever.get(ropedObject, DimensionsComponent.class);

        ropeTransform.x = ropedObjTransform.x + dimensionsComponent.width / 2 - initialRopeWidth;
        ropeTransform.y = ropedObjTransform.y + dimensionsComponent.height;

        ropePos.set(ropeTransform.x, ropeTransform.y);

        float c = ropePos.dst(ceiling);
        float a = ceiling.dst(ceiling.x, ropePos.y);

        float angle = MathUtils.asin(a / c) * MathUtils.radiansToDegrees;

        if (ropeTransform.x > ceiling.x) {
            angle = 90 -angle;
        } else {
            angle = -90 + angle;
        }

        ropeTransform.rotation = angle;
        ropeTransform.scaleY = (c) / initialRopeHeight;

    }

    @Override
    public void dispose() {

    }
}