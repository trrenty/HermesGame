package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.StateComponent;
import games.rednblack.editor.renderer.components.SpineDataComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.h2d.extention.spine.SpineObjectComponent;

public class AnimationSystem extends IteratingSystem {

    public static final Family FAMILY = Family.all(
            SpineObjectComponent.class,
            SpineDataComponent.class,
            StateComponent.class
    ).get();

    public AnimationSystem() {
        super(FAMILY);
    }

    private static final Logger log = new Logger(Animation.class.getName(), Logger.DEBUG);

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent entityState = ComponentRetriever.get(entity, StateComponent.class);
        entityState.stateTime += deltaTime;
        SpineObjectComponent spineObjectComponent = ComponentRetriever.get(entity, SpineObjectComponent.class);
        spineObjectComponent.state.getData().setDefaultMix(0.20f);
        SpineDataComponent spineDataComponent = ComponentRetriever.get(entity, SpineDataComponent.class);

        if (!spineDataComponent.currentAnimationName.equals(entityState.getStateName())) {
            try {
                spineObjectComponent.state.setAnimation(0, entityState.getStateName(), !entityState.isDead());
            } catch (IllegalArgumentException e) {
                spineObjectComponent.state.setAnimation(0, "IDLE", false);
            }
            spineDataComponent.currentAnimationName = entityState.getStateName();
        }
    }
}
