package com.hermes.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.component.HealthComponent;
import com.hermes.component.StateComponent;
import com.hermes.listener.InputListener;
import com.hermes.listener.ObservableInput;
import com.hermes.states.CharacterState;
import com.hermes.states.StateMachine;
import com.hermes.states.player.*;
import com.hermes.util.Pair;
import games.rednblack.editor.renderer.scripts.IScript;
import games.rednblack.editor.renderer.utils.ComponentRetriever;


public class PlayerControllerScriptV2 implements IScript, InputProcessor, ObservableInput {

    private final World world;
    private final PooledEngine engine;

    public PooledEngine getEngine() {
        return engine;
    }

    private Entity player;

    private StateMachine stateMachine;
    private StateComponent playerState;
    // listeners

    private final Array<Pair<InputListener, Boolean>> listeners = new Array<>();

    public PlayerControllerScriptV2(World world, PooledEngine engine) {
        this.world = world;
        this.engine = engine;
    }


    @Override
    public void init(Entity entity) {
        player = entity;

        Entity entity1 = EntityActions.getAnimationFromEntity(entity);

        if (entity1 != null) {
            playerState = ComponentRetriever.get(entity1, StateComponent.class);
        }
        stateMachine = playerState.stateMachine;
        playerState.owner = entity;


        stateMachine.addState(CharacterState.IDLE, new IdleState(playerState));
        stateMachine.addState(CharacterState.RUNNING, new RunState(playerState));
        stateMachine.addState(CharacterState.FALLING, new FallState(playerState));
        stateMachine.addState(CharacterState.TAKE_OFF, new JumpState(playerState));
        stateMachine.addState(CharacterState.CROUCH, new CrouchState(playerState));
        stateMachine.addState(CharacterState.CROUCH_WALK, new CrouchWalkState(playerState));
        stateMachine.addState(CharacterState.WALL_LATCH, new WallLatchedState(playerState));
        stateMachine.addState(CharacterState.PUSHING, new PushingState(playerState));
        stateMachine.addState(CharacterState.ATTACK, new AttackState(playerState, engine));
        stateMachine.addState(CharacterState.IDLE_WAIT, new IdleWaitingState(playerState));
        stateMachine.addState(CharacterState.IDLE_WAIT_ANGRY, new IdleWaitAngryState(playerState));
        stateMachine.addState(CharacterState.TAKING_DAMAGE, new TakingDamageState(playerState));
        stateMachine.addState(CharacterState.DEATH, new DeathState(playerState));
//        stateMachine.changeState(CharacterState.IDLE);
        playerState.setIdle();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void act(float delta) {
        HealthComponent hc = ComponentRetriever.get(player, HealthComponent.class);
        if (hc.health <= 0 && !playerState.isDead()) {
            playerState.setDead();
        }

    }

    @Override
    public void dispose() {
    }

    public Entity getPlayer() {
        return player;
    }

    public boolean isGrounded() {
        return playerState.isGrounded();
    }

    public StateComponent getPlayerState() {
        return playerState;
    }


    public World getWorld() {
        return world;
    }

    @Override
    public boolean keyDown(int keycode) {
        notifyListeners(keycode);
        if (keycode == Input.Keys.T) {
            GUIScene.INSTANCE.showTasks(true);
            return true;
        } else if (keycode == Input.Keys.P) {
            GUIScene.INSTANCE.addTask(BundleKeys.NEW_TASK);
            return true;
        }  else
            return stateMachine.getCurrentState().keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.T) {
            GUIScene.INSTANCE.showTasks(false);
            return true;
        }  else
        return stateMachine.getCurrentState().keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {

        return stateMachine.getCurrentState().keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return stateMachine.getCurrentState().touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return stateMachine.getCurrentState().touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return stateMachine.getCurrentState().touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return stateMachine.getCurrentState().mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return stateMachine.getCurrentState().scrolled(amountX, amountY);
    }

    @Override
    public void addListener(InputListener listener, boolean isPersistent) {
        Pair<InputListener, Boolean> listener2 = new Pair<>(listener, isPersistent);
        if (!listeners.contains(listener2, false)) {
            listeners.add(new Pair<>(listener, isPersistent));

        }
    }

    @Override
    public void removeListener(InputListener listener) {
        for (Pair<InputListener, Boolean> inputListenerBooleanPair : listeners) {
            if (inputListenerBooleanPair.getFirst().equals(listener)) {
                listeners.removeValue(inputListenerBooleanPair, true);
                return;
            }
        }
    }

    @Override
    public void notifyListeners(int keycode) {
        for (Pair<InputListener, Boolean> listener : listeners) {
            if (listener.getFirst().onKeyDown(keycode) && !listener.getSecond()) {
                listeners.removeValue(listener, true);
            }
        }
    }
}
