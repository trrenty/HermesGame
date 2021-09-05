package com.hermes.states.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.StateComponent;
import com.hermes.config.GameConfig;
import com.hermes.scripts.PlayerControllerScriptV2;
import com.hermes.states.EmptyState;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class JumpState extends EmptyState {
    private final PhysicsBodyComponent playerBody;
    private final Entity player;
    private final StateComponent state;
    private int jumpIteration = 0;

    public JumpState(StateComponent state) {
        this.player = state.owner;
        this.state = state;
        playerBody  = ComponentRetriever.get(player, PhysicsBodyComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        Vector2 position = playerBody.body.getPosition();
        TransformComponent transformComponent = ComponentRetriever.get(player, TransformComponent.class);


        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && jumpIteration < GameConfig.PLAYER_MAX_SPEED_Y ) {
            playerBody.body.applyLinearImpulse(0, playerBody.body.getMass() , position.x, position.y, true);
            jumpIteration++;
        }
        Vector2 velocity = playerBody.body.getLinearVelocity();

        float playerSpeedIncrement = playerBody.body.getMass() / 2 * deltaTime * 60;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && velocity.x < GameConfig.PLAYER_MAX_SPEED_X / 2) {
            transformComponent.scaleX = transformComponent.scaleX > 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(playerSpeedIncrement, 0, position.x, position.y, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && velocity.x > -GameConfig.PLAYER_MAX_SPEED_X / 2) {
            transformComponent.scaleX = transformComponent.scaleX < 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(-playerSpeedIncrement, 0, position.x, position.y, true);
        }


        if (velocity.y < 0 && !state.isGrounded()) {
            state.setFalling();
        } else if (MathUtils.isEqual(velocity.y, 0, 0.01f)) {
            state.setWallLatch();
        }

    }

    private static final Logger log = new Logger(JumpState.class.getName(), Logger.DEBUG);

    @Override
    public void exit() {
        jumpIteration = 0;
    }

}
