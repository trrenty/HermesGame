package com.hermes.interactables;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;

public class SimpleMultiInteractable extends BaseInteractable{

    protected boolean shouldContinue;
    protected final Array<Runnable> listOfInteractions = new Array<>();

    public SimpleMultiInteractable(Entity entity) {
        super(entity);

    }



    @Override
    public void interact() {
        if (listOfInteractions.isEmpty()) {
            GUIScene.INSTANCE.setDialogueText("...");
        } else {
            Runnable interaction = listOfInteractions.first();
            interaction.run();
            if (shouldContinue) listOfInteractions.removeIndex(0);
            shouldContinue = false;
        }
    }

    protected void addAllDialoguesWithStartingAction(boolean shouldLoop, Runnable runnable) {
        Array<Runnable> copy = new Array<>();
        for (int j = 0; j < GUIScene.INSTANCE.getNrOfLabelsWithPrefix(dialogueKey); j++) {
            int finalJ = j;
            copy.add(() -> { runnable.run();GUIScene.INSTANCE.setDialogueLabel(dialogueKey+ finalJ, styleDialogue);shouldContinue = true;});
//            log.debug(j + "");
        }
        if (shouldLoop) {
            copy.add(() -> {
                EntityActions.setQMarkVisible(entity, false);
                addInteractions(copy);
                shouldContinue = true;});
        }
        addInteractions(copy);
    }

    protected void addAllDialogues(boolean shouldLoop) {
        addAllDialoguesWithStartingAction(shouldLoop, () -> {});
    }

    public void addInteraction(Runnable runnable) {
        listOfInteractions.add(runnable);
    }

    public void addInteractions(Array<Runnable> runnables) {
        listOfInteractions.addAll(runnables);
    }
}
