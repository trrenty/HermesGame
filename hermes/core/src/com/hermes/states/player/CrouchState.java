package com.hermes.states.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.hermes.component.StateComponent;
import com.hermes.scripts.PlayerControllerScriptV2;
import com.hermes.states.CharacterState;
import com.hermes.states.EmptyState;

public class CrouchState extends EmptyState {
    private final StateComponent state;

    public CrouchState(StateComponent state) {
        this.state = state;
    }
    @Override
    public void update(float deltaTime) {
        if (!state.isGrounded()) {
            state.setFalling();
        } else if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                state.setCrouchWalking();
            }
        } else {
            state.setIdle();
        }
    }

}
