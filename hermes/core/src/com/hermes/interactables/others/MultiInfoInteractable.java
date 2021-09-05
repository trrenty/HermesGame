package com.hermes.interactables.others;

import com.badlogic.ashley.core.Entity;
import com.hermes.interactables.MultiInteractable;
import com.hermes.interactables.SimpleMultiInteractable;
import com.hermes.screens.game.LevelScreen;

public class MultiInfoInteractable extends SimpleMultiInteractable {
    public MultiInfoInteractable(Entity entity) {
        super(entity);
        addAllDialogues(true);
    }

}
