package com.hermes.states.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.StateComponent;
import com.hermes.config.GameConfig;
import com.hermes.scripts.PlayerControllerScriptV2;
import com.hermes.states.EmptyState;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class PushingState extends EmptyState {
    private final PhysicsBodyComponent playerBody;
    private final Entity player;
    private final StateComponent state;
    private float impulse;
    private float timer = 0;

    public PushingState(StateComponent state) {
        this.player = state.owner;
        playerBody  = ComponentRetriever.get(player, PhysicsBodyComponent.class);
        this.state = state;
        impulse = playerBody.mass;

    }

    @Override
    public void enter(Object... params) {
        impulse = playerBody.mass;
        timer = 0;
    }

    @Override
    public void exit() {
        playerBody.body.setLinearVelocity(0, 0);
    }

    @Override
    public void update(float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(player, TransformComponent.class);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            transformComponent.scaleX = transformComponent.scaleX < 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(impulse, 0, playerBody.centerX, playerBody.centerY, true);
            timer = 0;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            transformComponent.scaleX = transformComponent.scaleX > 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(-impulse, 0, playerBody.centerX, playerBody.centerY, true);
            timer = 0;
        } else {
            timer += deltaTime;
            if (timer > 0.5f) {
                state.shouldPush = false;
            }
        }

        if (!state.shouldPush) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                state.setRunning();
            } else {
                state.setIdle();
            }
        }

        if (!state.isGrounded()) {
            state.setFalling();
        }

        if (impulse < GameConfig.MAX_PLAYER_FORCE * 60 * deltaTime) {
            impulse += 2 * deltaTime;
        }



    }

    private static final Logger log = new Logger(JumpState.class.getName(), Logger.DEBUG);
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.NUM_2) {
            log.debug(state.isGrounded() + "");
        }
        return false;
    }
}
