package com.hermes.interactables.npc.level1;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Timer;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.interactables.MultiInteractable;
import com.hermes.listener.InputListener;
import com.hermes.screens.game.LevelScreen;
import games.rednblack.editor.renderer.SceneLoader;

public class Grump extends MultiInteractable implements InputListener {
    private final SceneLoader sceneLoader;

    public Grump(LevelScreen screenV2, Entity entity) {
        super(screenV2, entity);
        sceneLoader = screenV2.getSceneLoader();
    }

    @Override
    protected void init() {
        setEntityName("cap-grump");
        addAllDialogues(false);
        addInteraction(() -> {
            EntityActions.addListenerToInputProcessor(this, false);
            setDialogueLabel(dialogueKey + 9);
            GUIScene.INSTANCE.setDownLabel(BundleKeys.YES_OR_NO);
        });
        listOfInteractions.insert(8, () -> {
            Entity player = root.getChild(ChildrenNames.PLAYER_ID).getEntity();
            EntityActions.teleportEntity(player, 3, 21, sceneLoader);
            shouldContinue =true;
        });
    }


    @Override
    public boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.Y) {
            EntityActions.dropLoot(entity, sceneLoader);
            shouldContinue = true;
            interact();
            return true;

        } else if (keycode == Input.Keys.N) {
            addInteraction(() -> {setDialogueLabel(BundleKeys.FUCK_YOU); shouldContinue = true;});
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Entity player = root.getChild(ChildrenNames.PLAYER_ID).getEntity();
                    EntityActions.teleportEntity(player, 3, 21, sceneLoader);
                    Entity door = root.getChild(ChildrenNames.GRUMP_DOOR).getEntity();
                    EntityActions.setEntityVisibility(door, true);
                }
            }, 0.5f);
            listOfInteractions.removeIndex(0);
            interact();
            return true;
        } else {
            return false;
        }
    }
}
