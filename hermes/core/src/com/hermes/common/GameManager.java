package com.hermes.common;

public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    private GameState state = GameState.RUNNING;


    private GameManager() {
    }

    public boolean isRunning() {return state.isRunning(); }
    public boolean isPaused() { return state.isPaused(); }
    public boolean isLevelDone() { return state.isLevelDone(); }
    public boolean isPlayerDead() { return state.isPlayerDead();}

    public void setRunning() { state = GameState.RUNNING;}
    public void setPaused() { state = GameState.PAUSED;}
    public void setLevelDone() { state = GameState.LEVEL_DONE; }
    public void setPlayerDead() { if (state != GameState.RESET) state = GameState.PLAYER_DEAD; }
    public void setReset() { state = GameState.RESET; }

    public boolean isReset() {
        return state.isReset();
    }


    public enum GameState {
        RUNNING, PAUSED, LEVEL_DONE, PLAYER_DEAD, RESET;
        public boolean isRunning() { return this == RUNNING;}
        public boolean isPaused() { return  this == PAUSED; }
        public boolean isLevelDone() { return this == LEVEL_DONE; }
        public boolean isPlayerDead() { return this == PLAYER_DEAD; }
        public boolean isReset() { return this == RESET; }
    }
}
