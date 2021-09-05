package com.hermes.states.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.hermes.common.EntityActions;
import com.hermes.common.Filters;
import com.hermes.component.StateComponent;
import com.hermes.config.GameConfig;
import com.hermes.screens.game.FirstLevelScreenV2;
import com.hermes.states.CharacterState;
import com.hermes.states.EmptyState;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.SpineDataComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.systems.action.Actions;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.h2d.extention.spine.SpineObjectComponent;

public class AttackState extends EmptyState {
    private final StateComponent state;
    private final PooledEngine engine;
    private Entity entityToAttack;
    private static final Logger log = new Logger(AttackState.class.getName(), Logger.DEBUG);
    private float attackTimer = GameConfig.ENEMY_ATTACK_RATE;
    private boolean raycastSent = false;

    private final Array<Fixture> fixturesHit = new Array<>();
    private final RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
        if (fixture.getFilterData().categoryBits == Filters.BIT_PLAYER) {
            fixturesHit.add(fixture);
        }
        return 1;
    };


    public AttackState(StateComponent state, PooledEngine engine) {
        this.state = state;
        this.engine = engine;
    }

    @Override
    public void enter(Object... params) {
        for (Object param : params) {
            if (param instanceof Entity) {
                this.entityToAttack = (Entity)param;
            }
        }
        attackTimer = GameConfig.ENEMY_ATTACK_RATE;
    }

    @Override
    public void update(float deltaTime) {
        attackTimer += deltaTime;

        if (attackTimer > GameConfig.ENEMY_ATTACK_RATE) {
            raycastSent = false;
            TransformComponent entityTransform = ComponentRetriever.get(state.owner, TransformComponent.class);
            TransformComponent victimTransform = ComponentRetriever.get(entityToAttack, TransformComponent.class);
            DimensionsComponent entityDimension = ComponentRetriever.get(state.owner, DimensionsComponent.class);
            DimensionsComponent victimDimension = ComponentRetriever.get(entityToAttack, DimensionsComponent.class);


            float distance = Math.abs((entityTransform.x + entityDimension.width/2) - (victimTransform.x + victimDimension.width/2));

            if (distance <= GameConfig.ENEMY_ATTACK_RANGE + entityDimension.width/2 + victimDimension.width/2) {
                attack(entityTransform, entityDimension);
                log.debug("attacked");

            } else {
                state.setRunning(entityToAttack);
            }
        } else if (attackTimer > 0.75f && !raycastSent) {
            TransformComponent entityTransform = ComponentRetriever.get(state.owner, TransformComponent.class);
            DimensionsComponent entityDimension = ComponentRetriever.get(state.owner, DimensionsComponent.class);
            rayCast(entityTransform, entityDimension);
            raycastSent = true;
        }

//        if (distance >= GameConfig.ENEMY_ATTACK_RANGE + entityDimension.width/2 + victimDimension.width/2
//                && attackTimer  > GameConfig.ENEMY_ATTACK_RATE) {
////            if (Actions.hasActions(state.owner)) Actions.removeActions(state.owner);
//            if (distance < 9) {
//                state.setRunning(entityToAttack);
//            } else {
//                state.setIdle();
//            }
//        }
//        else {
//            attack(entityTransform, entityDimension);
//        }
    }

    private void playAnimation() {
        SpineObjectComponent spineObjectComponent = ComponentRetriever.get(state.animationEntity, SpineObjectComponent.class);
        SpineDataComponent spineDataComponent = ComponentRetriever.get(state.animationEntity, SpineDataComponent.class);
        spineObjectComponent.state.setAnimation(0, state.getStateName(), false);
        spineObjectComponent.state.addAnimation(0, CharacterState.IDLE.name(), true, 0);
        spineDataComponent.currentAnimationName = state.getStateName();
    }

    private void attack(TransformComponent transform, DimensionsComponent dimComp) {
        if (attackTimer > GameConfig.ENEMY_ATTACK_RATE && !Actions.hasActions(state.owner)) {
            playAnimation();

            jump(transform);


            attackTimer = 0;
        }
    }

    private void jump(TransformComponent transform) {
        PhysicsBodyComponent bodyComponent = ComponentRetriever.get(state.owner, PhysicsBodyComponent.class);

        bodyComponent.body.setLinearVelocity(0, 0);
        bodyComponent.body.applyLinearImpulse(transform.scaleX * bodyComponent.mass * 4, 3, bodyComponent.centerX, bodyComponent.centerY, true);
    }

    private void rayCast(TransformComponent transform, DimensionsComponent dimComp) {
        PhysicsBodyComponent bodyComponent = ComponentRetriever.get(state.owner, PhysicsBodyComponent.class);
//            bodyComponent.body.setMassData(tmp);
//            Actions.addAction(engine, state.owner, Actions.forever(Actions.moveTo(transform.x, transform.y, 1, Interpolation.swing)));

        float centerX = bodyComponent.body.getPosition().x;
        centerX += transform.scaleX > 0 ? dimComp.width / 2 : -dimComp.width / 2;
        float centerY = bodyComponent.body.getPosition().y - 0.5f;
        bodyComponent.body.getWorld().rayCast(
                rayCastCallback,
                centerX,
                centerY,
                centerX + transform.scaleX * GameConfig.ENEMY_ATTACK_RANGE / 2,
                centerY);
        EntityActions.damageHitEntities(fixturesHit, engine);

        FirstLevelScreenV2.attacks.add(new float[] {centerX, centerY, centerX + transform.scaleX * GameConfig.ENEMY_ATTACK_RANGE / 2, centerY});
    }

}
