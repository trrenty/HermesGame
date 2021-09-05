package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.hermes.common.GameManager;
import com.hermes.states.CharacterState;
import com.hermes.states.StateMachine;

public class StateComponent implements Component, Pool.Poolable {

    public StateMachine stateMachine = new StateMachine();
    private CharacterState state = CharacterState.IDLE;
    public float stateTime = 0f;
    public int touchingPlatforms = 0;
    public Entity owner = null;
    public Entity animationEntity = null;
    public boolean shouldPush = false;

    @Override
    public void reset() {
        shouldPush = false;
        owner = null;
        animationEntity = null;
        state = CharacterState.IDLE;
        stateTime = 0;
        stateMachine.clear();
        touchingPlatforms = 0;

    }

    public void setState(CharacterState state, Object ... params) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
        }
        stateMachine.changeState(state, params);

    }

    public void incrementTouchingPlatforms() {
        touchingPlatforms++;
    }

    public void decrementTouchingPlatforms() {
        touchingPlatforms--;
        if (touchingPlatforms < 0) {
            touchingPlatforms = 0;
        }
    }

    public void update(float deltaTime) {
        stateMachine.update(deltaTime);
    }

    public String getStateName() {
        return state.name();
    }

    public CharacterState getState() {
        return state;
    }

    public void setIdle(Object ... params) {
        setState(CharacterState.IDLE, params);
    }

    public void setJumping() { setState(CharacterState.TAKE_OFF); }

    public void setRunning(Object ... params) {
        setState(CharacterState.RUNNING, params);
    }

    public void setDead() {
        setState(CharacterState.DEATH);
        GameManager.INSTANCE.setPlayerDead();
    }
    public void setWallLatch() {
        setState(CharacterState.WALL_LATCH);
    }
    public void setIdleWait(Object ... params) {
        setState(CharacterState.IDLE_WAIT, params);
    }
    public void setIdleWaitAngry() {
        setState(CharacterState.IDLE_WAIT_ANGRY);
    }

    public void setFalling() {
        setState(CharacterState.FALLING);
    }

    public void setAttacking(Object ... params) {
        setState(CharacterState.ATTACK, params);
    }

    public void setCrouching() {
        setState(CharacterState.CROUCH);
    }

    public void setCrouchWalking() {
        setState(CharacterState.CROUCH_WALK);
    }

    public void setTakingDamage() {
        if (!isDead()) {
            setState(CharacterState.TAKING_DAMAGE);
        }
    }


    public void setPushing() {setState(CharacterState.PUSHING);}

    public boolean isRunning() {
        return state.isRunning();
    }

    public boolean isIdle() {
        return state.isIdle();
    }

    public boolean isJumping() {
        return state.isJumping();
    }

    public boolean isDead() {
        return state.isDead();
    }

    public boolean isFalling() {
        return state.isFalling();
    }

    public boolean isAttacking() {
        return state.isAttacking();
    }

    public boolean isCrouching() {
        return state.isCrouching();
    }

    public boolean isGrounded() {
        return touchingPlatforms > 0;
    }

    public boolean isPushing() {
        return state.isPushing();
    }

    public boolean isTakingDamage() {
        return state.isTakingDamage();
    }

    public boolean isIdleWaiting() { return state.isIdleWaiting(); }

    public boolean isAngrilyWaiting() {return state.isIdleWaitingButAngry();}

    public boolean isWallLatched() {
        return state.isWallLatched();
    }


    public boolean isCrouchWalking() {
        return state.isCrouchWalking();
    }

}