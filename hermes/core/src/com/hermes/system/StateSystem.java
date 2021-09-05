package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.hermes.assets.ChildrenNames;
import com.hermes.component.PlayerComponent;
import com.hermes.component.StateComponent;

import games.rednblack.editor.renderer.components.label.LabelComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class StateSystem extends IteratingSystem {

    public static final Family FAMILY = Family.all(
            StateComponent.class
    ).exclude(PlayerComponent.class).get();

    private static final Logger log = new Logger(StateSystem.class.getName(), Logger.DEBUG);

    public StateSystem() {
        super(FAMILY);
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent stateComponent = ComponentRetriever.get(entity, StateComponent.class);
        stateComponent.update(deltaTime);

        // todo: remove item wrapper when done
        ItemWrapper it = new ItemWrapper(entity);
        Entity et = it.getChild("Label").getEntity();
        if (et != null) {
            LabelComponent lc = ComponentRetriever.get(et, LabelComponent.class);
            lc.setText(stateComponent.getStateName());
        }

    }

}
