package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.LicuriciComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.systems.action.Actions;
import games.rednblack.editor.renderer.systems.action.data.ActionData;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

import java.util.HashMap;


public class LicuriciSystem extends IteratingSystem implements EntityListener {

    private static final HashMap<Integer, Interpolation> INTERPOLATION_MAP = new HashMap<>();

    public static final Family FAMILY = Family.all(
            LicuriciComponent.class,
            TransformComponent.class
    ).get();

    public LicuriciSystem() {
        super(FAMILY);
        init();
    }

    private void init() {
        INTERPOLATION_MAP.put(0, Interpolation.circle);

        INTERPOLATION_MAP.put(1, Interpolation.exp5);
        INTERPOLATION_MAP.put(2, Interpolation.swing);

    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        LicuriciComponent licuriciComponent = ComponentRetriever.get(entity, LicuriciComponent.class);
        if (tc.x < 0) {
            tc.x = licuriciComponent.initialPosition.x;
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        LicuriciComponent licuriciComponent = ComponentRetriever.get(entity, LicuriciComponent.class);
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);

        licuriciComponent.initialPosition.set(transformComponent.x, transformComponent.y);
        addLicuriciBehaviour(entity, licuriciComponent);

    }

    @Override
    public void entityRemoved(Entity entity) {

    }

    private void addLicuriciBehaviour(Entity entity, LicuriciComponent lC) {
        int rand1 = MathUtils.random(INTERPOLATION_MAP.size() - 1);
        int rand2 = MathUtils.random(INTERPOLATION_MAP.size() - 1);

        Interpolation i3 = INTERPOLATION_MAP.get(rand1);
        Interpolation i4 = INTERPOLATION_MAP.get(rand2);

        float rand3 = MathUtils.random(1f, 2f);

        ActionData actionData = Actions.forever(Actions.parallel(
                Actions.sequence(
                        Actions.moveBy(-lC.moveSpeed.x, lC.moveSpeed.y, lC.changeDirectionTime),
                        Actions.moveBy(-lC.moveSpeed.x, -lC.moveSpeed.y, lC.changeDirectionTime)),
                Actions.sequence(
                        Actions.rotateBy(lC.rotation, lC.changeRotationTime, i3),
                        Actions.rotateBy(-lC.rotation/rand3, lC.changeRotationTime, i4))
        ));
        Actions.addAction((PooledEngine)getEngine(), entity, actionData);
    }
}
