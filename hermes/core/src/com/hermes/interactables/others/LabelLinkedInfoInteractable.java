package com.hermes.interactables.others;

import com.badlogic.ashley.core.Entity;

import com.hermes.assets.gui.GUIScene;
import com.hermes.interactables.SimpleMultiInteractable;
import games.rednblack.editor.renderer.components.label.LabelComponent;

public class LabelLinkedInfoInteractable extends SimpleMultiInteractable {

    public LabelLinkedInfoInteractable(Entity entity, LabelComponent label, String labelKey) {
        super(entity);
        addAllDialoguesWithStartingAction(true, () -> { label.setText(GUIScene.INSTANCE.getFromBundle(labelKey)); });
    }
}
