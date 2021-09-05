package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Logger;
import com.hermes.common.EntityActions;
import com.hermes.component.DestinationComponent;
import com.hermes.config.GameConfig;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class DestinationSystem extends IteratingSystem {

    private static final Family FAMILY = Family.all(
            DestinationComponent.class,
            TransformComponent.class,
            PhysicsBodyComponent.class
    ).get();

    public DestinationSystem() {
        super(FAMILY);
    }

    private static final Logger log = new Logger(DestinationSystem.class.getName(), Logger.DEBUG);
    private final Vector2 velocity = new Vector2();


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DestinationComponent destinationComponent = ComponentRetriever.get(entity, DestinationComponent.class);

        if (destinationComponent.destinations.size <= 0) return;

        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        PhysicsBodyComponent bodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);

        if (destinationComponent.destinations.get(0).epsilonEquals(transformComponent.x, transformComponent.y, 0.1f)) {
            bodyComponent.body.setLinearVelocity(0, 0);
            EntityActions.checkIfDestinationIsOccupied(entity, destinationComponent, (PooledEngine)getEngine());
            destinationComponent.destinations.removeIndex(0);
            log.debug("removed destination comp - arrived. destinations: " + destinationComponent.destinations.size);


        } else if (bodyComponent.body.getLinearVelocity().len() < GameConfig.NPC_SPEED){
            velocity.x = destinationComponent.destinations.get(0).x - transformComponent.x;
            velocity.y = destinationComponent.destinations.get(0).y - transformComponent.y;
            velocity.nor();
            if (bodyComponent.bodyType == BodyDef.BodyType.DynamicBody.getValue()) {
                velocity.scl(bodyComponent.mass);
                bodyComponent.body.applyLinearImpulse(velocity, bodyComponent.centerOfMass, true);
            } else {
                velocity.scl(GameConfig.NPC_SPEED);
                bodyComponent.body.setLinearVelocity(velocity);
            }
        }
    }
}
