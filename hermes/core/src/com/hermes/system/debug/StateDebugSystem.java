package com.hermes.system.debug;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hermes.assets.ChildrenNames;
import com.hermes.common.EntityActions;
import com.hermes.component.PlayerComponent;
import com.hermes.component.StateComponent;
import com.hermes.component.StateDebugComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.label.LabelComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class StateDebugSystem extends EntitySystem {

    private Entity label;
    private Entity player;
    private Viewport viewport;

    public static final Family LabelFamily = Family.all(
            LabelComponent.class,
            StateDebugComponent.class
    ).get();

    public static final Family PlayerFamily = Family.all(
            PlayerComponent.class
    ).get();

    public StateDebugSystem(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void addedToEngine(Engine engine) {
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(LabelFamily);
        if (entities == null || entities.size() == 0) return;
        label = entities.first();

        if (player == null) {
            ImmutableArray<Entity> players =getEngine().getEntitiesFor(PlayerFamily);
            if (players == null || players.size() == 0) return;
            player = players.first();
            player = EntityActions.getAnimationFromEntity(player);
            return;
        }

        StateComponent stateComponent = ComponentRetriever.get(player, StateComponent.class);
        LabelComponent labelComponent = ComponentRetriever.get(label, LabelComponent.class);
        TransformComponent transformComponent = ComponentRetriever.get(label, TransformComponent.class);

        if (stateComponent == null) return;
        labelComponent.setText(stateComponent.getStateName());
        labelComponent.text.append(Math.round(stateComponent.stateTime * 100.0)/100.0);

        transformComponent.x = viewport.getCamera().position.x - viewport.getWorldWidth() / 2;
        transformComponent.y = viewport.getCamera().position.y - viewport.getWorldHeight() / 2;

    }
}
