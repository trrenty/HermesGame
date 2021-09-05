package com.hermes.states.player;

import com.hermes.component.StateComponent;
import com.hermes.scripts.PlayerControllerScriptV2;

public class IdleWaitingState extends IdleState {

    private float idleTime;
    public IdleWaitingState(StateComponent stateComponent) {
        super(stateComponent);
    }

    @Override
    public void enter(Object... params) {
        idleTime = 0;
        for (Object param : params) {
            idleTime = (Float)param;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!state.isGrounded()) {
            state.setFalling();
        }
        if (state.stateTime > 3 ) {
            state.setIdle(idleTime + state.stateTime);
        }
    }
}
