package com.hermes.interactables.npc.level1;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Timer;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.common.Filters;
import com.hermes.component.InteractableComponent;
import com.hermes.component.StateComponent;
import com.hermes.interactables.MultiInteractable;
import com.hermes.interactables.others.MultiInfoInteractable;
import com.hermes.screens.game.LevelScreen;
import com.hermes.system.passive.InitSystem;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class Mini extends MultiInteractable {
    private final LevelScreen screen;

    public Mini(LevelScreen screenV2, Entity entity) {
        super(screenV2, entity);
        this.screen = screenV2;
    }

    private static final Logger log = new Logger(Mini.class.getName(), Logger.DEBUG);
    @Override
    protected void init() {
        setEntityName("cap-mini");

        addInteraction(() -> {
            boolean enemyAlive;
            Entity entity = root.getChild(ChildrenNames.BULY).getChild(ChildrenNames.ANIMATION).getEntity();
            if (entity == null)  {
                enemyAlive = false;
                log.debug("entity null");
            }
            else {
                StateComponent stateComponent = ComponentRetriever.get(entity, StateComponent.class);
                if (stateComponent == null) {
                    enemyAlive = false;
                    log.debug("state null");
                }
                else {
                    enemyAlive = !stateComponent.isDead();
                    log.debug("is dead? -> " + !enemyAlive);
                }
            }

            if (enemyAlive) {
                setDialogueLabel(dialogueKey + MathUtils.random(1));
            } else {
                setDialogueLabel(dialogueKey + "2");
                disableInput();
                shouldContinue = true;
            }
        });
        addInteraction(() -> {setDialogueLabel(dialogueKey + "3"); shouldContinue = true;});
        addInteraction(() -> {setDialogueLabel(dialogueKey + "4"); shouldContinue = true;});
        addInteraction(() -> {
            // threadStuff
            threadManager.addToQueueAndAdvance(entity);
            GUIScene.INSTANCE.notifyPlayer(BundleKeys.COUNT_DOWN, "Mini");
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    GUIScene.INSTANCE.notifyPlayer(BundleKeys.AWAIT, "Mini");
                }
            }, 1.5f);
            // threadStuff

            Entity door = root.getChild(ChildrenNames.EXIT).getEntity();
            EntityActions.goTo(entity, door, engine, false);
            EntityActions.setQMarkVisible(entity, false);
            enableInput();

            Entity entity2 = root.getChild("infoMini").getEntity();
            if (entity2 != null) {
                EntityActions.setQMarkVisible(entity2, true);
                InitSystem.setContactBytes(entity2, Filters.BIT_INTERACTABLE, (short) (Filters.BIT_WORLD | Filters.BIT_PLAYER));
                InteractableComponent interactableComponent2 = engine.createComponent(InteractableComponent.class);
                interactableComponent2.setInteractable(new MultiInfoInteractable(entity2));
                entity2.add(interactableComponent2);

            }

            shouldContinue = true;
        });
    }

    @Override
    public void interact() {
        facePlayer();
        super.interact();
    }
}
