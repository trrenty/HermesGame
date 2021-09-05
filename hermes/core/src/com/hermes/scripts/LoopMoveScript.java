package com.hermes.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.hermes.config.GameConfig;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.components.ScriptComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.scripts.IScript;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class LoopMoveScript implements IScript {
    private Entity entity;
    public Vector2 initialPosition = new Vector2();
    public Vector2 destination = new Vector2();
    private float initialDistance;

    private boolean completedLoop = false;

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        initialPosition.set(tc.x, tc.y);
        MainItemComponent mc = ComponentRetriever.get(entity, MainItemComponent.class);
        Float destinationX = mc.customVariables.getFloatVariable("destinationX");
        if (destinationX == null) {
            entity.remove(ScriptComponent.class);
            return;
        }
        Float destinationY = mc.customVariables.getFloatVariable("destinationY");
        if (destinationY == null) {
            destinationY = tc.y;
        }
        destination.set(destinationX, destinationY);
        initialDistance = initialPosition.dst2(destination);
    }

    @Override
    public void act(float delta) {
        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        float dstEntityStart = initialPosition.dst2(tc.x, tc.y);

        boolean completed = dstEntityStart >= initialDistance;
        float percent = completed ? 1 : dstEntityStart / initialDistance;
        percent = Interpolation.linear.apply(percent) + 0.1f;

        if (MathUtils.isEqual(1, percent, 0.1f)) {
            float x = destination.x;
            float y = destination.y;
            destination.set(initialPosition);
            initialPosition.set(x, y);
            completedLoop = !completedLoop;
            return;
        }
        if (percent > 0.5) {
            percent = 1-percent;
        }
        PhysicsBodyComponent pc = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        pc.body.setLinearVelocity((completedLoop ? -GameConfig.MOVING_PLATFORM_SPEED : GameConfig.MOVING_PLATFORM_SPEED) * percent, 0);
    }

    @Override
    public void dispose() {

    }
}
