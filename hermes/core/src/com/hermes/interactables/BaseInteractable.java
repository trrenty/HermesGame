package com.hermes.interactables;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class BaseInteractable implements Interactable {
    protected String dialogueKey;
    protected String infoKey;
    protected Entity entity;
    protected String styleDialogue = "dialogue";


       //test
    private final float something = 12;

    public BaseInteractable(Entity entity){

        this.entity = entity;
        setInfoKey(BundleKeys.INTERACT);
        MainItemComponent mc = ComponentRetriever.get(entity, MainItemComponent.class);
        String tmp = mc.customVariables.getStringVariable("style");

        if (tmp != null) {
            styleDialogue = tmp;
        }
        tmp = mc.customVariables.getStringVariable("info");

        if (tmp != null) {
            setDialogueKey(tmp);
        }

    }



    public void setInfoKey(String infoKey) {
        this.infoKey = infoKey;
    }

    public void setDialogueKey(String dialogueKey) {
        this.dialogueKey = dialogueKey;
    }

    @Override
    public void beginContact() {
        GUIScene.INSTANCE.setDownLabel(infoKey, "E");
    }

    @Override
    public void endContact() {
        GUIScene.INSTANCE.hideLabels();
    }

    @Override
    public void interact() {
        if (dialogueKey == null) {
            GUIScene.INSTANCE.setDialogueText("...", styleDialogue);
        } else {
            GUIScene.INSTANCE.setDialogueLabel(dialogueKey, styleDialogue);
        }
        EntityActions.setQMarkVisible(entity, false);
    }
}
