package com.hermes.config;


public class GameConfig {

    public static final float WIDTH = 960f;
    public static final float HEIGHT = 540f;    // PIXELS

    public static final float WORLD_WIDTH = 17; //WORLD UNITS
    public static final float WORLD_HEIGHT = 10; // units

    public static final float WORLD_CENTER_X = WORLD_WIDTH/2;
    public static final float WORLD_CENTER_Y = WORLD_HEIGHT/2;

    public static final float VIEWPORT_WIDTH = 20;
    public static final float VIEWPORT_HEIGHT = 12;

    public static final float VIEWPORT_CENTER_X = VIEWPORT_WIDTH / 2f;
    public static final float VIEWPORT_CENTER_Y = VIEWPORT_HEIGHT / 2f;

    public static final float PLAYER_WIDTH = 0.75f;
    public static final float PLAYER_HEIGHT = 1.25f;
    public static final float PLAYER_FRICTION = 0.7f;
    public static final float PLAYER_RESTITUTION = 0f;
    public static final float PLAYER_DENSITY = 2;

    public static final float GRAVITY = 9.81f;

    public static final float PLAYER_MAX_SPEED_X = 6f;
    public static final float PLAYER_MAX_SPEED_X_INV = 1 / PLAYER_MAX_SPEED_X;
    public static final float PLAYER_MAX_SPEED_Y = 10f;

    public static final float CAMERA_FOLLOW_OFFSET_WIDTH = 2f;
    public static final float CAMERA_FOLLOW_OFFSET_HEIGHT = 2.5f;
    public static final float CAMERA_VIEW_OFFSET = -1f;
    public static final float CAMERA_SPEED = PLAYER_MAX_SPEED_X * 2;


    public static final float PLAYER_ACCELERATION = 3f;
    public static final float PLAYER_DECELERATION = 4f;
    public static final float MAP_UNIT_SCALE = 1 / 64f;
    public static final float HUD_WIDTH = 1920f;
    public static final float HUD_HEIGHT = 1080f;
    public static final float MAX_PLAYER_JUMP_TIME = 0.1f;
    public static final float PLAYER_INVULNERABILITY = 1f;
    public static final float PLAYER_ATTACK_RATE = 0.5f;
    public static final float PLAYER_ATTACK_RANGE = 1f;
    public static final float NPC_SPEED = 6f;
    public static final float UI_ANIM_SPEED_FAST = 0.25f;
    public static final float MAX_ZOOM_OUT = 2f;
    public static final float ENEMY_MOV_SPEED = 3;
    public static final float ENEMY_ATTACK_RATE = 5f;
    public static final float ENEMY_ATTACK_RANGE = 2.5f;
    public static final float MOVING_PLATFORM_SPEED = 10;
    public static final float WALL_LATCH_TIME = 0.5f;
    public static final int PLAYER_HEALTH = 4;
    public static final float MAX_PLAYER_FORCE = 1.25f;
    public static final float PLAYER_RESPAWN_TIME = 2.5f;


    private GameConfig() {}
}
