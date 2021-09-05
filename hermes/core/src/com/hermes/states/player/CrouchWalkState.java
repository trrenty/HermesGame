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

public class CrouchWalkState extends EmptyState {
    private final PhysicsBodyComponent playerBody;
    private final Entity player;
    private final StateComponent state;

    public CrouchWalkState(StateComponent state) {
        this.player = state.owner;
        playerBody  = ComponentRetriever.get(player, PhysicsBodyComponent.class);
        this.state = state;
    }
    @Override
    public void update(float deltaTime) {
        if (!state.isGrounded()) {
            state.setFalling();
        } else if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
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
            if (velocity.epsilonEquals(0, 0)){
                System.out.println("smth");
                state.setCrouching();
            }

        } else {
            state.setIdle();
        }
    }

}
