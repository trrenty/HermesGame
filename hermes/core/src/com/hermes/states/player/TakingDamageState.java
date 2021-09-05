package com.hermes.states.player;

import com.hermes.component.StateComponent;
import com.hermes.scripts.PlayerControllerScriptV2;
import com.hermes.states.EmptyState;


public class TakingDamageState extends EmptyState {


    private final StateComponent state;

    public TakingDamageState(StateComponent state) {
        this.state = state;
    }

    @Override
    public void update(float deltaTime) {
        if (state.stateTime > 0.5f) {
            state.setIdle();
        }
    }
}
