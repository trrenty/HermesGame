package com.hermes.states.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.hermes.common.EntityActions;
import com.hermes.common.Filters;
import com.hermes.component.StateComponent;
import com.hermes.config.GameConfig;
import com.hermes.screens.game.FirstLevelScreenV2;
import com.hermes.scripts.PlayerControllerScriptV2;
import com.hermes.states.EmptyState;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class AttackState extends EmptyState {
    private static final Logger log = new Logger(AttackState.class.getName(), Logger.DEBUG);
    private final Entity player;
    private final StateComponent state;
    private final World world;
    private final Engine engine;
    private float attackRate = GameConfig.PLAYER_ATTACK_RATE + 1;
    private final Array<Fixture> fixturesHit = new Array<>();
    private final RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
        if (fixture.getFilterData().categoryBits == Filters.BIT_ENEMY) {
            fixturesHit.add(fixture);
        }
        return 1;
    };

    public AttackState(StateComponent state, Engine engine) {
        this.player = state.owner;
        this.engine = engine;
        this.state = state;
        this.world = ComponentRetriever.get(state.owner, PhysicsBodyComponent.class).body.getWorld();
    }

    @Override
    public void update(float deltaTime) {
        if (attackRate > GameConfig.PLAYER_ATTACK_RATE) {
            if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
                TransformComponent transform = ComponentRetriever.get(player, TransformComponent.class);

                PhysicsBodyComponent bodyComp = ComponentRetriever.get(player, PhysicsBodyComponent.class);
                DimensionsComponent dimComp = ComponentRetriever.get(player, DimensionsComponent.class);

                float distance = bodyComp.body.getLinearVelocity().x / (GameConfig.GRAVITY/3);

                float centerX = bodyComp.body.getPosition().x;
                centerX += transform.scaleX > 0 ? dimComp.width / 2 : -dimComp.width / 2;
                float centerY = bodyComp.body.getPosition().y;
                world.rayCast(
                        rayCastCallback,
                        centerX,
                        centerY,
                        centerX + transform.scaleX * GameConfig.PLAYER_ATTACK_RANGE + distance,
                        centerY);

                FirstLevelScreenV2.attacks.add(new float[] {centerX, centerY, centerX  + transform.scaleX * GameConfig.PLAYER_ATTACK_RANGE + distance, centerY});

                attackRate = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//                EntityActions.damageHitEntities(fixturesHit, controller.getEngine());
                state.setRunning();
            } else {
//                EntityActions.damageHitEntities(fixturesHit, controller.getEngine());
                state.setIdle();
            }
        } else {
            attackRate += deltaTime;
            if (attackRate > GameConfig.PLAYER_ATTACK_RATE / 2 && fixturesHit.size > 0) {
                EntityActions.damageHitEntities(fixturesHit, (PooledEngine)engine);
            }
        }

    }
}
