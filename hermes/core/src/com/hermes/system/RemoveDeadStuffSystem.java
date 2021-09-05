package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.hermes.assets.ChildrenNames;
import com.hermes.common.EntityActions;
import com.hermes.common.GameManager;
import com.hermes.component.*;
import com.hermes.interactables.BaseInteractable;
import com.hermes.interactables.others.ItemInteractable;
import com.hermes.screens.game.FirstLevelScreenV2;
import games.rednblack.editor.renderer.SceneLoader;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.PolygonComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.data.CompositeItemVO;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class RemoveDeadStuffSystem extends IteratingSystem {

    public static final Family FAMILY = Family.all(
            HealthComponent.class
    ).exclude(PlayerComponent.class).get();
    private final SceneLoader sceneLoader;

    private final Array<Entity> queue = new Array<>();

    public RemoveDeadStuffSystem(SceneLoader sceneLoader) {
        super(FAMILY);
        this.sceneLoader = sceneLoader;
    }

    private static final Logger log = new Logger(RemoveDeadStuffSystem.class.getName(), Logger.DEBUG);

    @Override
    public void update(float deltaTime) {
        removeComponentsOfQueuedEntities();
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent healthComponent = ComponentRetriever.get(entity, HealthComponent.class);
        if (healthComponent != null && healthComponent.health <= 0) {
//            log.debug("removing stuff");
            setDeadState(entity);
            EntityActions.dropLoot(entity, sceneLoader);
            queue.add(entity);
            ActionOnRemoveComponent action = ComponentRetriever.get(entity, ActionOnRemoveComponent.class);
            if (action != null) {
                log.debug("action happening!!");
                action.onRemove.doAction();
            }
        }
//            getEngine().removeEntity(entity);
    }

    private void setDeadState(Entity entity) {
        Entity animation = EntityActions.getAnimationFromEntity(entity);

        if (animation == null) return;
        StateComponent state = ComponentRetriever.get(animation, StateComponent.class);
        if (state != null) {
            state.setDead();
            state.stateMachine.clear();

        }
        state = ComponentRetriever.get(entity, StateComponent.class);
        if (state != null) {
            state.setDead();
            state.stateMachine.clear();

        }
    }

    private void removeComponentsOfQueuedEntities() {
        for (Entity entity : queue) {
            PhysicsBodyComponent physicsBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
            if (physicsBodyComponent != null) {
                physicsBodyComponent.body.getWorld().destroyBody(physicsBodyComponent.body);
            }

            entity.remove(PhysicsBodyComponent.class);
            entity.remove(HealthComponent.class);
            entity.remove(PolygonComponent.class);
            entity.remove(StateComponent.class);

            Entity ent = EntityActions.getAnimationFromEntity(entity);
            if (ent != null) {
                ent.remove(StateComponent.class);
            }

        }
    }
}