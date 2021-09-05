package com.hermes.states;

public enum CharacterState {
    IDLE, TAKING_DAMAGE, IDLE_WAIT, IDLE_WAIT_ANGRY, RUNNING, TAKE_OFF, DEATH, FALLING, ATTACK, CROUCH, CROUCH_WALK, WALL_LATCH, PUSHING;

    public boolean isRunning() { return this == RUNNING;}
    public boolean isIdle() {return this == IDLE;}
    public boolean isJumping() {return this == TAKE_OFF;}
    public boolean isDead() {return  this == DEATH;}
    public boolean isFalling() {return this == FALLING;}
    public boolean isAttacking() {return this == ATTACK;}
    public boolean isCrouching() {return this == CROUCH;}
    public boolean isCrouchWalking() {return this == CROUCH_WALK;}
    public boolean isWallLatched() {return this == WALL_LATCH;}
    public boolean isPushing() { return this == PUSHING;}
    public boolean isTakingDamage() {return this == TAKING_DAMAGE;}
    public boolean isIdleWaiting() { return this == IDLE_WAIT; }
    public boolean isIdleWaitingButAngry() {return this == IDLE_WAIT_ANGRY;}

}