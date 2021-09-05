package com.hermes.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.hermes.thread.SpikyPlatformRunnable;
import com.hermes.thread.ThreadManager;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.scripts.IScript;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class SpikyPlatformScript implements IScript {
    private final Vector2 initialPosition = new Vector2();
    private final Vector2 endPosition = new Vector2();
    private float moveSpeed = MathUtils.random(15f, 20f);

    private static final Logger log = new Logger(SpikyPlatformRunnable.class.getName(), Logger.DEBUG);

    // threadStuff
    private final ThreadManager threadManager;
    private Entity entity;

    public SpikyPlatformScript(ThreadManager threadManager) {
        this.threadManager = threadManager;
    }


    @Override
    public void init(Entity entity) {
        this.entity = entity;
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        initialPosition.set(transformComponent.x, transformComponent.y);
        endPosition.set(8.5f, transformComponent.y);
        threadManager.submitRunnableForBarrier(entity);
    }

    @Override
    public void act(float delta) {
        TransformComponent currentPosition = ComponentRetriever.get(entity, TransformComponent.class);
        PhysicsBodyComponent bodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        if (threadManager.movesToRight() && currentPosition.x < endPosition.x ){
            bodyComponent.body.setLinearVelocity(moveSpeed, 0f);
        } else if (!threadManager.movesToRight() && currentPosition.x > initialPosition.x) {
            bodyComponent.body.setLinearVelocity(-moveSpeed, 0f);
        }   else if (!bodyComponent.body.getLinearVelocity().epsilonEquals(0, 0)) {
            bodyComponent.body.setLinearVelocity(0f, 0f);
            threadManager.addToQueueAndAdvance(entity);
            moveSpeed = MathUtils.random(3f, 7f);

        }
    }

    @Override
    public void dispose() {

    }
}
