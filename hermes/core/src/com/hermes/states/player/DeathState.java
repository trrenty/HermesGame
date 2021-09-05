package com.hermes.states.player;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Logger;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.common.GameManager;
import com.hermes.component.HealthComponent;
import com.hermes.component.PlayerComponent;
import com.hermes.component.StateComponent;
import com.hermes.config.GameConfig;
import com.hermes.states.EmptyState;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class DeathState extends EmptyState {
    private final StateComponent state;
    private float timer;
    private static final Logger log = new Logger(DeathState.class.getName(), Logger.DEBUG);
    private boolean labelHidden = true;

    @Override
    public void enter(Object... params) {
        log.debug("entered state");
        timer = 0;
    }

    @Override
    public void exit() {
        log.debug("exit state");
    }

    public DeathState(StateComponent stateComponent) {
        this.state = stateComponent;
    }

    @Override
    public void update(float deltaTime) {
        if (GameManager.INSTANCE.isRunning()) {
            state.setIdle();
        }
        timer += deltaTime;
        if (timer > GameConfig.PLAYER_RESPAWN_TIME && labelHidden) {
            GUIScene.INSTANCE.setDownLabel(BundleKeys.RESPAWN);
            labelHidden = false;
            log.debug("a");
        }

    }

    @Override
    public boolean keyDown(int keycode) {
        log.debug("key pressed");
        if (timer > GameConfig.PLAYER_RESPAWN_TIME) {
            log.debug("timer > 5");
            EntityActions.respawnPlayer(state);
            labelHidden = true;
            GUIScene.INSTANCE.hideLabels();
            return true;
        }
        return false;
    }


}
