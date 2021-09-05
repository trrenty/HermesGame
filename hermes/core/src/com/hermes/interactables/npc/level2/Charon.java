package com.hermes.interactables.npc.level2;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.common.GameManager;
import com.hermes.common.Item;
import com.hermes.common.Task;
import com.hermes.component.HealthComponent;
import com.hermes.component.PlayerComponent;
import com.hermes.interactables.MultiInteractable;
import com.hermes.screens.game.LevelScreen;
import com.hermes.system.TaskSystem;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;


public class Charon extends MultiInteractable {
    public Charon(LevelScreen screenV2, Entity entity) {
        super(screenV2, entity);
    }

    @Override
    protected void init() {
        setEntityName("cap-charon");

        addDialogueInteraction(BundleKeys.CHARON + "0");
        addDialogueInteraction(BundleKeys.CHARON + "1");
        addDialogueInteraction(BundleKeys.CHARON + "2");
        addInteraction(() -> {
            setDialogueLabel(BundleKeys.CHARON + "3");
            ItemWrapper it = root.getChild(ChildrenNames.LEVEL2_START_DOOR);
            if (it.getEntity()!= null) {
                EntityActions.moveBy(it.getEntity(), 0, -5, engine);
                CheckBox label = GUIScene.INSTANCE.addTask(BundleKeys.COIN_TASK, 0, 1);
                engine.getSystem(TaskSystem.class).addTaskToProcess(new Task() {
                    float timer = 300;
                    int lastInt = (int)timer;
                    @Override
                    public void update(float delta) {
                        timer-= delta;
                        if (lastInt != (int)timer) {
                            lastInt = (int)timer;
                            int mins = lastInt / 60;
                            int secs = lastInt - mins * 60;
                            GUIScene.INSTANCE.setText(label, BundleKeys.COIN_TASK, mins, secs);
                        }

                    }

                    @Override
                    public boolean isDone() {
                        return lastInt <= 0;
                    }

                    @Override
                    public void cleanUp() {
                        Entity player = root.getChild(ChildrenNames.PLAYER_ID).getEntity();
                        HealthComponent hc = ComponentRetriever.get(player, HealthComponent.class);
                        hc.damage(99);
                        PlayerComponent pc = ComponentRetriever.get(player, PlayerComponent.class);
                        pc.checkPoint.set(5, 1);
                        GameManager.INSTANCE.setReset();
                    }
                });
            }
            shouldContinue = true;
        });
        addInteraction(()-> {
            Entity player = root.getChild(ChildrenNames.PLAYER_ID).getEntity();
            if (player != null) {
                PlayerComponent pc = ComponentRetriever.get(player, PlayerComponent.class);
                Item coin = pc.inventory.findItem("Coin");
                if (coin != null) {
                    if (coin.amount == 1) {
                        setDialogueLabel(BundleKeys.CHARON + "5");
                        shouldContinue = true;
                    } else {
                        setDialogueLabel(BundleKeys.CHARON + "6");
                        shouldContinue = true;
                    }
                } else {
                    setDialogueLabel(BundleKeys.CHARON + "4");
                }
            } else {
                setDialogueLabel(BundleKeys.CHARON + "4");
            }
        });
    }
}
