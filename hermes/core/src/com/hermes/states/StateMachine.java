package com.hermes.states;

import com.hermes.component.StateComponent;

import java.util.HashMap;

public class StateMachine {


    private final HashMap<CharacterState, State> states = new HashMap<>();
    private State currentState = EmptyState.INSTANCE;

    public StateMachine() {

    }

    public State getCurrentState() {
        return currentState;
    }

    public void addState(CharacterState state, State newState) {
        states.put(state, newState);
    }

    public void clear() {
        states.clear();
        currentState = EmptyState.INSTANCE;
    }

    public void removeState(CharacterState state) {
        states.remove(state);
    }

    public void changeState(CharacterState state, Object... params) {

        State nextState = states.get(state);
        if (nextState!=null && nextState == currentState) {
            if (params != null) {
                currentState.enter(params);
            }
            return;
        }
        if (nextState == null) {
            nextState = EmptyState.INSTANCE;
        }
        currentState.exit();

        nextState.enter(params);
        currentState = nextState;
    }

    public void reset() {
        states.clear();
        currentState = EmptyState.INSTANCE;
    }

    public void update(float deltaTime) {
        currentState.update(deltaTime);
    }

    public boolean keyDown(int keycode) {
        return currentState.keyDown(keycode);
    }

    boolean keyUp(int keycode) {
        return currentState.keyUp(keycode);
    }

    boolean keyTyped(char character) {
        return currentState.keyTyped(character);
    }

    boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return currentState.touchDown(screenX, screenY, pointer, button);
    }

    boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return currentState.touchUp(screenX, screenY, pointer, button);
    }

    boolean touchDragged(int screenX, int screenY, int pointer) {
        return currentState.touchDragged(screenX, screenY, pointer);
    }

    boolean mouseMoved(int screenX, int screenY) {
        return currentState.mouseMoved(screenX, screenY);
    }

    boolean scrolled(float amountX, float amountY) {
        return currentState.scrolled(amountX, amountY);
    }

}
