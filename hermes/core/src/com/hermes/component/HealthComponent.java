package com.hermes.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class HealthComponent implements Component, Pool.Poolable {
    public float maxHealth = 4;
    public float health = 4;
    @Override
    public void reset() {
        health = 4;
    }

    public void damage(float damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public void heal(float value) {
        health += value;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
}
