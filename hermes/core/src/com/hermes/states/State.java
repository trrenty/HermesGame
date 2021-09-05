package com.hermes.states;

public interface State {
    void update(float deltaTime);
    void enter(Object ... params);
    void exit();
    boolean keyDown(int keycode);
    boolean keyUp(int keycode);
    boolean keyTyped(char character);
    boolean touchDown(int screenX, int screenY, int pointer, int button);
    boolean touchUp(int screenX, int screenY, int pointer, int button);
    boolean touchDragged(int screenX, int screenY, int pointer) ;
    boolean mouseMoved(int screenX, int screenY);
    boolean scrolled(float amountX, float amountY);
}
