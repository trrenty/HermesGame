package com.hermes.states.player;

import com.hermes.component.StateComponent;
import com.hermes.scripts.PlayerControllerScriptV2;

public class IdleWaitAngryState extends IdleState {


    public IdleWaitAngryState(StateComponent stateComponent) {
        super(stateComponent);
    }


    @Override
    public void update(float deltaTime) {
        if (!state.isGrounded()) {
            state.setFalling();
        }
        if (state.stateTime > 2.6f ) {
            state.setIdle();
        }
    }
}
