package com.hermes.common;

public class Filters {

    public static final short BIT_PLAYER = 1;
    public static final short BIT_WORLD = 1 << 1;
    public static final short BIT_OBSTACLE = 1 << 2;
    public static final short BIT_ENEMY = 1 << 3;
    public static final short BIT_INTERACTABLE = 1 << 4;

    private Filters() {
    }

}
