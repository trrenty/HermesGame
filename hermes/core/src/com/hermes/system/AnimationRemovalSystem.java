package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.hermes.common.Action;
import com.hermes.component.ActionOnRemoveComponent;
import games.rednblack.editor.renderer.components.sprite.SpriteAnimationComponent;
import games.rednblack.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class AnimationRemovalSystem extends IteratingSystem {
    public static final Family FAMILY = Family.all(
            ActionOnRemoveComponent.class,
            SpriteAnimationComponent.class,
            SpriteAnimationStateComponent.class
    ).get();
    public AnimationRemovalSystem() {
        super(FAMILY);
    }

    private static final Logger log = new Logger(AnimationRemovalSystem.class.getName(), Logger.DEBUG);

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteAnimationStateComponent sas = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);
        SpriteAnimationComponent sa = ComponentRetriever.get(entity, SpriteAnimationComponent.class);

        if (sas.allRegions.size * 1f / sa.fps <= sas.time) {
            Action onRemoveAction = ComponentRetriever.get(entity, ActionOnRemoveComponent.class).onRemove;
            if (onRemoveAction != null) {
                onRemoveAction.doAction();
            }
            getEngine().removeEntity(entity);
//            log.debug("removed");
        }
    }
}
