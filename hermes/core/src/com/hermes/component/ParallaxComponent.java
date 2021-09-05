package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class  ParallaxComponent implements Component, Pool.Poolable {
    public float parallaxCoefficientX;
    public float parallaxCoefficientY;
    public Vector2 initialPosition = new Vector2();
    public boolean shouldRepeat = true;
    public boolean shouldScale = false;

    @Override
    public void reset() {
        parallaxCoefficientX = 0;
        parallaxCoefficientY = 0;
        initialPosition.set(0, 0);
        shouldRepeat = true;
        shouldScale = false;
    }
}
