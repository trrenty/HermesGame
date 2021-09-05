package com.hermes.states.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.StateComponent;
import com.hermes.config.GameConfig;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class RunState extends IdleState {
    private Entity entityToWalkTowards;
    private static final Logger log = new Logger(AttackState.class.getName(), Logger.DEBUG);

    private float walkTimer;
    private float walkDirection;


    public RunState(StateComponent state, PooledEngine engine) {
        super(state, engine);
    }

    @Override
    public void enter(Object... params) {
        entityToWalkTowards = null;
        for (Object param : params) {
            if (param instanceof Entity) {
                this.entityToWalkTowards = (Entity)param;
            }
        }

        walkTimer = MathUtils.random(2f) + 1;
        walkDirection = MathUtils.randomSign();
    }

    @Override
    public void update(float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(state.owner, TransformComponent.class);

        if (entityToWalkTowards == null) {
            walkRandomly(deltaTime, transformComponent);
        } else {
            goToVictim(transformComponent);
        }

    }

    private void goToVictim(TransformComponent transformComponent) {
        TransformComponent victimTransform = ComponentRetriever.get(entityToWalkTowards, TransformComponent.class);
        DimensionsComponent entityDimension = ComponentRetriever.get(state.owner, DimensionsComponent.class);
        DimensionsComponent victimDimension = ComponentRetriever.get(entityToWalkTowards, DimensionsComponent.class);


        float distance = Math.abs((transformComponent.x + entityDimension.width/2) - (victimTransform.x + victimDimension.width/2));

        if (distance <= GameConfig.ENEMY_ATTACK_RANGE + entityDimension.width/2+ victimDimension.width/2) {
            state.setAttacking(entityToWalkTowards);
        } else if (distance < 9) {
            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(state.owner, PhysicsBodyComponent.class);
            float velocityX = transformComponent.x > victimTransform.x ? -GameConfig.ENEMY_MOV_SPEED : GameConfig.ENEMY_MOV_SPEED;
            transformComponent.scaleX = Math.signum(velocityX);
            bodyComponent.body.setLinearVelocity(velocityX, bodyComponent.body.getLinearVelocity().y);

        } else {
            state.setIdle();
        }
    }

    private void walkRandomly(float deltaTime, TransformComponent transformComponent) {
        lookForVictims(deltaTime);

        walkTimer -= deltaTime;

        PhysicsBodyComponent bodyComponent = ComponentRetriever.get(state.owner, PhysicsBodyComponent.class);

        if (bodyComponent != null) {
            if (walkTimer > 0) {
                transformComponent.scaleX = walkDirection;
                bodyComponent.body.setLinearVelocity(GameConfig.ENEMY_MOV_SPEED / 2 * walkDirection, 0);
//                log.debug("changing direction");

            } else {
                state.setIdle();
            }
        }
    }
}
