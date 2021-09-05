package com.hermes.interactables.npc.level1;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Timer;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.interactables.MultiInteractable;
import com.hermes.screens.game.FirstLevelScreenV2;
import com.hermes.screens.game.LevelScreen;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class They extends MultiInteractable {
    public They(LevelScreen screenV2, Entity entity) {
        super(screenV2, entity);
    }

    @Override
    protected void init() {
        setEntityName("cap-they");
        addInteraction(() -> {
            disableInput();
            setDialogueLabel(dialogueKey+"0"); shouldContinue = true;});
        addInteraction(() -> { setDialogueLabel(dialogueKey+"1");shouldContinue = true;});
        addInteraction(() -> { setDialogueLabel(dialogueKey+"2");shouldContinue = true;});
        addInteraction(() -> {
            enableInput();
            // threadStuff
            threadManager.addToQueueAndAdvance(entity);
            GUIScene.INSTANCE.notifyPlayer(BundleKeys.COUNT_DOWN, "They");
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    GUIScene.INSTANCE.notifyPlayer(BundleKeys.AWAIT, "They");
                }
            }, 1.5f);
            // threadStuff
            Entity door = root.getChild(ChildrenNames.EXIT).getEntity();
            EntityActions.goTo(entity, door, engine, false);

            EntityActions.setQMarkVisible(entity, false);
            shouldContinue = true;
        });
    }

    @Override
    public void interact() {
        facePlayer();
        super.interact();
    }
}
