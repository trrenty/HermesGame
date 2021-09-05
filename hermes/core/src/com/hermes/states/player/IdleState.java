package com.hermes.states.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.hermes.component.StateComponent;
import com.hermes.interactables.MultiInteractable;
import com.hermes.scripts.PlayerControllerScriptV2;
import com.hermes.states.EmptyState;

import javax.print.DocFlavor;

public class IdleState extends EmptyState {
    protected final StateComponent state;
    private int idleWait;
    private float enterTime;


    public IdleState(StateComponent state) {
        this.state = state;
    }

    @Override
    public void enter(Object... params) {
        enterTime = 0;
        for (Object param : params) {
            state.stateTime = (Float)param;
            enterTime = state.stateTime;
        }
        idleWait = MathUtils.random(7 , 10);
    }

    @Override
    public void update(float deltaTime) {
        if (!state.isGrounded()) {
            state.setFalling();
        }
        if (state.stateTime > 20) {
            state.setIdleWaitAngry();
        } else if (state.stateTime > enterTime + idleWait ) {
            state.setIdleWait(state.stateTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!Gdx.input.getInputProcessor().equals(MultiInteractable.EMPTY_INPUT_PROCESSOR)) {
                state.setRunning();
            }
        }

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            state.setJumping();
        } else if (keycode == Input.Keys.CONTROL_LEFT) {
            state.setCrouching();
        } else if (keycode == Input.Keys.Z) {
            state.setAttacking();
        } else if (keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT) {
            state.setRunning();
        }

        return true;
    }

}
