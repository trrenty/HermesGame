package com.hermes.states.player;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class WallLatchedState extends EmptyState {
    private final PhysicsBodyComponent playerBody;
    private final Entity player;
    private final StateComponent state;
    private float timer;

    public WallLatchedState(StateComponent state) {
        this.state = state;
        player = state.owner;
        playerBody  = ComponentRetriever.get(player, PhysicsBodyComponent.class);
    }

    @Override
    public void enter(Object... params) {
        timer = 0;
    }

    @Override
    public void update(float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(player, TransformComponent.class);
        Vector2 velocity = playerBody.body.getLinearVelocity();
        Vector2 position = playerBody.body.getPosition();
        if (state.isGrounded()) {
            state.setIdle();
            return;
        }
        timer += deltaTime;

        float playerSpeedIncrement = playerBody.body.getMass() / 2 * deltaTime * 60;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && velocity.x < GameConfig.PLAYER_MAX_SPEED_X / 2) {
            transformComponent.scaleX = transformComponent.scaleX > 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(playerSpeedIncrement, 0, position.x, position.y, true);
            timer = 0;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && velocity.x > -GameConfig.PLAYER_MAX_SPEED_X / 2) {
            transformComponent.scaleX = transformComponent.scaleX < 0? transformComponent.scaleX : -transformComponent.scaleX;
            playerBody.body.applyLinearImpulse(-playerSpeedIncrement, 0, position.x, position.y, true);
            timer = 0;
        } else if (timer < GameConfig.WALL_LATCH_TIME) {
            playerBody.body.applyLinearImpulse(transformComponent.scaleX * playerSpeedIncrement, 0, position.x, position.y, true);
        }
        velocity = playerBody.body.getLinearVelocity();
        if (velocity.y < -0.3) {
            state.setFalling();
        }

    }

    private static final Logger log = new Logger(JumpState.class.getName(), Logger.DEBUG);

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            PhysicsBodyComponent pc = ComponentRetriever.get(player, PhysicsBodyComponent.class);
            TransformComponent tc = ComponentRetriever.get(player, TransformComponent.class);
            pc.body.applyLinearImpulse(-tc.scaleX * pc.body.getMass() * 3, pc.body.getMass(),pc.centerX, pc.centerY, true);
            state.setJumping();
        } else         if (keycode == Input.Keys.NUM_2) {
            log.debug(state.touchingPlatforms + "");
        }
        return true;
    }
}