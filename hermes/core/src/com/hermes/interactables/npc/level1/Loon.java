package com.hermes.interactables.npc.level1;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Timer;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.common.Inventory;
import com.hermes.common.Item;
import com.hermes.component.DestinationComponent;
import com.hermes.component.PlayerComponent;
import com.hermes.config.GameConfig;
import com.hermes.interactables.MultiInteractable;
import com.hermes.screens.game.FirstLevelScreenV2;
import com.hermes.screens.game.LevelScreen;
import com.hermes.thread.ThreadManager;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class Loon extends MultiInteractable {


    public Loon(LevelScreen screenV2, Entity entity) {
        super(screenV2, entity);
    }

    @Override
    protected void init() {
        setEntityName("cap-loon");

        Entity door = root.getChild(ChildrenNames.EXIT).getEntity();
        addInteraction(() -> {
            disableInput();
            setDialogueLabel(dialogueKey+"0"); shouldContinue = true;});
        addInteraction(() -> { setDialogueLabel(dialogueKey+"1");shouldContinue = true;});
        addInteraction(() -> { cameraFollower.setFocus(door); shouldContinue = true;});
        addInteraction(() -> {
            cameraFollower.setFocus(root.getChild(ChildrenNames.PLAYER_ID).getEntity());
            GUIScene.INSTANCE.addTask(BundleKeys.FIND_EXIT_TASK);
            shouldContinue = true;
        });
        addInteraction(() -> { setDialogueLabel(dialogueKey+"2"); GUIScene.INSTANCE.addTask(BundleKeys.HELP_LOON); shouldContinue = true;});
        addInteraction(() -> {
            if (foundHat()) return;
            EntityActions.goTo(entity, 60, 21, engine);
            EntityActions.setQMarkVisible(entity, false);
            enableInput();
//            shouldContinue = true;
//            EntityActions.setQMarkVisible(entity, false);
        });
        addInteraction(() -> {
            // threadStuff
            threadManager.addToQueueAndAdvance(entity);
            GUIScene.INSTANCE.notifyPlayer(BundleKeys.COUNT_DOWN, "Loon");
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    GUIScene.INSTANCE.notifyPlayer(BundleKeys.AWAIT, "Loon");
                }
            }, 1.5f);
            // threadStuff
            EntityActions.goTo(entity, door, engine, false);
            enableInput();
            shouldContinue = true;
        });
        addInteraction(() -> { setDialogueLabel(dialogueKey+"4"); shouldContinue = true; });
        addInteraction(() -> { setDialogueLabel(BundleKeys.READY); shouldContinue = true; });
        addInteraction(() -> {
            // threadStuff
            threadManager.countDown();
            GUIScene.INSTANCE.notifyPlayer(BundleKeys.COUNT_DOWN, "Player");
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    GUIScene.INSTANCE.notifyPlayer(BundleKeys.BARRIER_UP);
                }
            }, 1.5f);
            // threadStuff

            Entity mini = root.getChild(ChildrenNames.MINI).getEntity();
            Entity they = root.getChild(ChildrenNames.THEY).getEntity();

            PhysicsBodyComponent pcMini = ComponentRetriever.get(mini, PhysicsBodyComponent.class);
            PhysicsBodyComponent pcThey = ComponentRetriever.get(they, PhysicsBodyComponent.class);
            PhysicsBodyComponent pcLoon = ComponentRetriever.get(entity, PhysicsBodyComponent.class);

            if (pcLoon.body.getPosition().dst(pcMini.body.getPosition()) > 10 ||
                    pcLoon.body.getPosition().dst(pcThey.body.getPosition()) > 10) {
                setDialogueLabel(dialogueKey+"5");
            } else {
                EntityActions.goTo(entity, door, engine, true);
                EntityActions.goTo(mini, door, engine, true);
                EntityActions.goTo(they, door, engine, true);
                setDialogueLabel(dialogueKey+"6");
                shouldContinue = true;
            }

        });

    }

    @Override
    public void interact() {
        facePlayer();
        super.interact();
    }

    private boolean foundHat() {
        Entity player = root.getChild(ChildrenNames.PLAYER_ID).getEntity();
        Inventory inventory = ComponentRetriever.get(player, PlayerComponent.class).inventory;
        Item hat = inventory.findItem("Loon's Hat");
        if (hat != null) {
            GUIScene.INSTANCE.removeItem(hat.name);
            GUIScene.INSTANCE.setDialogueLabel(dialogueKey+"3");
            inventory.removeItem(hat);
            GUIScene.INSTANCE.checkTask(BundleKeys.HELP_LOON);
            shouldContinue = true;
            return true;
        }
        return false;
    }
}
