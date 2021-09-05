package com.hermes.interactables.npc.level2;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Timer;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.common.Filters;
import com.hermes.component.*;
import com.hermes.interactables.MultiInteractable;
import com.hermes.listener.InputListener;
import com.hermes.screens.game.LevelScreen;
import com.hermes.states.StateMachine;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class Bully extends MultiInteractable implements InputListener {
    private  StateMachine meanStateMachine;
    private  StateComponent stateComponent;
    private PlayerComponent pc;

    public Bully(LevelScreen screenV2, Entity entity) {
        super(screenV2, entity);
        Entity animationEntity = EntityActions.getAnimationFromEntity(entity);
        if (animationEntity != null) {
            stateComponent = ComponentRetriever.get(animationEntity, StateComponent.class);
            meanStateMachine = stateComponent.stateMachine;
            stateComponent.stateMachine = new StateMachine();
        }
        PhysicsBodyComponent pc = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
//        pc.filter.categoryBits = Filters.BIT_INTERACTABLE;
//        pc.scheduleRefresh();
//        pc.executeRefresh(entity);

        entity.remove(DamageComponent.class);
        entity.remove(HealthComponent.class);
        ActionOnRemoveComponent action = engine.createComponent(ActionOnRemoveComponent.class);
        action.onRemove = () -> openGates(false);
        entity.add(action);

    }

    @Override
    protected void init() {
        setEntityName("cap-bully");
        addDialogueInteraction(dialogueKey + "0");
        addDialogueInteraction(dialogueKey + "1");
        addDialogueInteraction(dialogueKey + "2");
        addDialogueInteraction(dialogueKey + "3");

        addInteraction(()  -> {
            EntityActions.addListenerToInputProcessor(this, false);
            setDialogueLabel(dialogueKey + "3");


            String text = GUIScene.INSTANCE.getFromBundle(dialogueKey + "4");
            Entity player = root.getChild(ChildrenNames.PLAYER_ID).getEntity();
            pc = ComponentRetriever.get(player, PlayerComponent.class);
            if (pc.inventory.findItem("Copper Coin") != null) {
                GUIScene.INSTANCE.setDownText(text);
            } else {
                GUIScene.INSTANCE.setDownText(text.substring(text.indexOf('\n') + 1));
            }
        });


    }

    @Override
    public boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.NUM_1) {
            setDialogueLabel(dialogueKey + "5");
            openGates(true);
            shouldContinue = true;
            pc.inventory.removeItem("Copper Coin");
            GUIScene.INSTANCE.removeItem("Copper Coin");
            GUIScene.INSTANCE.hideDownLabel();
            return true;

        } else if (keycode == Input.Keys.NUM_2) {
            setDialogueLabel(dialogueKey + "6");
            openGates(false);
            shouldContinue = true;
            GUIScene.INSTANCE.hideDownLabel();
            return true;
        } else if (keycode == Input.Keys.NUM_3){
            setDialogueLabel(dialogueKey + "7");
            entity.add(engine.createComponent(DamageComponent.class));
            entity.add(engine.createComponent(HealthComponent.class));
            endContact();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    entity.remove(InteractableComponent.class);
                    stateComponent.stateMachine = meanStateMachine;
                }
            }, 1f);
            return true;
        }
        return false;
    }

    private void openGates(boolean all) {
        Entity door1 = root.getChild("doorEnd0").getEntity();
        Entity door2 = root.getChild("doorEnd1").getEntity();
        if (all) {
            Entity door3 = root.getChild("doorOptional").getEntity();
            EntityActions.moveBy(door3, 0, 7, engine);
        }
        EntityActions.moveBy(door2, 0, 7, engine);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                EntityActions.moveBy(door1, 0, 7, engine);
            }
        }, 0.5f);
    }
}
