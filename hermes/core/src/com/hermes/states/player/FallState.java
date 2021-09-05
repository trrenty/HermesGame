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
import com.hermes.states.CharacterState;
import com.hermes.states.EmptyState;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class FallState extends EmptyState {
    private final PhysicsBodyComponent playerBody;
    private final Entity player;
    private final StateComponent state;

    public FallState(StateComponent state) {

        this.state = state;
        this.player = state.owner;
        playerBody  = ComponentRetriever.get(player, PhysicsBodyComponent.class);
    }

    @Override
    public void update(float deltaTime) {

        TransformComponent transformComponent = ComponentRetriever.get(player, TransformComponent.class);
        Vector2 velocity = playerBody.body.getLinearVelocity();
        Vector2 position = playerBody.body.getPosition();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && velocity.x < GameConfig.PLAYER_MAX_SPEED_X / 2) {
            transformComponent.scaleX = transformComponent.scaleX > 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(playerBody.body.getMass(), 0, position.x, position.y, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && velocity.x > -GameConfig.PLAYER_MAX_SPEED_X / 2) {
            transformComponent.scaleX = transformComponent.scaleX < 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(-playerBody.body.getMass() / 2, 0, position.x, position.y, true);
        }

        if (state.isGrounded()) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                state.setRunning();
            } else  {
                state.setIdle();
            }
        } else if (MathUtils.isEqual(velocity.y, 0, 0.01f)) {
            state.setWallLatch();
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
