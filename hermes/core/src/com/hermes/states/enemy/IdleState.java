package com.hermes.states.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.NpcComponent;
import com.hermes.component.PlayerComponent;
import com.hermes.component.StateComponent;
import com.hermes.states.EmptyState;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

public class IdleState extends EmptyState {
    protected final StateComponent state;
    private final ImmutableArray<Entity> npcs;
    private final ImmutableArray<Entity> players;

    private float searchTimer = 0;
    private static final Logger log = new Logger(IdleState.class.getName(), Logger.DEBUG);
    private float transitionTimer;

    public IdleState(StateComponent state, PooledEngine engine) {
        this.state = state;
        npcs = engine.getEntitiesFor(Family.all(NpcComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    @Override
    public void enter(Object ... params) {
        searchTimer = 0;
        transitionTimer = MathUtils.random(3) + 2;
    }

    @Override
    public void update(float deltaTime) {

        lookForVictims(deltaTime);

        transitionTimer -= deltaTime;
        if (transitionTimer < 0) {
            state.setRunning();
        }


    }

    protected void lookForVictims(float deltaTime) {
        searchTimer+= deltaTime;

        if (searchTimer > 1) {
            TransformComponent transformComponent = ComponentRetriever.get(state.owner, TransformComponent.class);

            if (attackEntityInRange(transformComponent, players)) return;
            if (attackEntityInRange(transformComponent, npcs)) return;

            searchTimer = 0;
        }
    }

    private boolean attackEntityInRange(TransformComponent transformComponent, ImmutableArray<Entity> entities) {
        for (Entity entity : entities) {
            TransformComponent npcTransform = ComponentRetriever.get(entity, TransformComponent.class);
            if (Math.abs(transformComponent.x - npcTransform.x) < 6 && Math.abs(transformComponent.y - npcTransform.y) < 3) {
                state.setRunning(entity);
                return true;
            }
        }
        return false;
    }
}
