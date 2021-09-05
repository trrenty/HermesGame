package com.hermes.states.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.hermes.component.StateComponent;
import com.hermes.config.GameConfig;
import com.hermes.scripts.PlayerControllerScriptV2;
import com.hermes.states.EmptyState;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class RunState extends EmptyState {
    private final PhysicsBodyComponent playerBody;
    private final Entity player;
    private final StateComponent state;
    private float buttonPressedTimer = 0;

    public RunState(StateComponent state) {
        this.state = state;

        this.player = state.owner;
        playerBody  = ComponentRetriever.get(player, PhysicsBodyComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        if (state.shouldPush) {
            state.setPushing();
            return;
        }
        Vector2 velocity = playerBody.body.getLinearVelocity();
        Vector2 position = playerBody.body.getPosition();

        TransformComponent transformComponent = ComponentRetriever.get(player, TransformComponent.class);
        buttonPressedTimer += deltaTime;

        float playerSpeedIncrement = playerBody.body.getMass() / 2 * deltaTime * 60;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && GameConfig.PLAYER_MAX_SPEED_X > velocity.x) {
            buttonPressedTimer = 0;
            transformComponent.scaleX = transformComponent.scaleX > 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(playerSpeedIncrement, 0, position.x, position.y, true);
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                state.setCrouchWalking();
            }
        } else if ((Gdx.input.isKeyPressed(Input.Keys.LEFT)) && -GameConfig.PLAYER_MAX_SPEED_X < velocity.x) {
            buttonPressedTimer = 0;
            transformComponent.scaleX = transformComponent.scaleX < 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(-playerSpeedIncrement, 0, position.x, position.y, true);
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                state.setCrouchWalking();
            }
        }
        if (!state.isGrounded()) {
            state.setFalling();
        } else if (buttonPressedTimer > 0.5f) {
            state.setIdle();
        }

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            state.setJumping();
        } else if (keycode == Input.Keys.Z) {
            state.setAttacking();
        }
        return true;
    }
}
