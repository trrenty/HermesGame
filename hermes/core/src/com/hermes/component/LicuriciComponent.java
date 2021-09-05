package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class LicuriciComponent implements Component, Pool.Poolable {

    public Vector2 moveSpeed = new Vector2();
    public float rotation;
    public float changeRotationTime;
    public float changeDirectionTime;
    public Vector2 initialPosition = new Vector2();

    public LicuriciComponent() {
        reset();
    }
    @Override
    public void reset() {
        moveSpeed.set(MathUtils.random(4, 7), MathUtils.random(1, 2));
        rotation = MathUtils.random(90, 360);
        changeDirectionTime = MathUtils.random(3, 5);
        changeRotationTime = changeDirectionTime - 0.1f;
        initialPosition.set(0, 0);
    }
}
