package com.hermes.system.passive;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.hermes.assets.ChildrenNames;
import com.hermes.common.Filters;
import com.hermes.common.GameManager;
import com.hermes.component.InteractableComponent;
import com.hermes.interactables.MultiInteractable;
import com.hermes.interactables.npc.level1.Grump;
import com.hermes.interactables.npc.level1.Loon;
import com.hermes.interactables.npc.level1.Mini;
import com.hermes.interactables.npc.level1.They;
import com.hermes.screens.game.LevelScreen;
import com.hermes.scripts.DoorSensorScript;
import com.hermes.scripts.RopeScript;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.systems.action.Actions;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class InitLevel1System extends InitSystem {
    public InitLevel1System(ItemWrapper root, LevelScreen screen) {
        super(root, screen);
    }

    @Override
    protected void initLevelSpecificThings() {
        initNpcs();
        initCage();



        // set contact byte to doors
        Entity entity = root.getChild(ChildrenNames.DOOR).getEntity();
        setContactBytes(entity, Filters.BIT_WORLD, (short) (Filters.BIT_PLAYER | Filters.BIT_OBSTACLE ));

        ItemWrapper item = root.getChild(ChildrenNames.DOOR_SENSOR);

        setContactBytes(item.getEntity(), Filters.BIT_WORLD, (short) (Filters.BIT_PLAYER | Filters.BIT_OBSTACLE ));
        if (item.getEntity() != null)
            item.addScript(new DoorSensorScript(entity, tm), (PooledEngine)getEngine());


        // anim to exit stuff
        entity = root.getChild("exitLight").getEntity();
        if (entity != null)
            Actions.addAction((PooledEngine)getEngine(), entity, Actions.forever(Actions.sequence(Actions.alpha(0.7f, 1f), Actions.alpha(1f, 1f))));

        // exit door
        entity = root.getChild(ChildrenNames.EXIT_DOOR).getEntity();
        setContactBytes(entity, Filters.BIT_WORLD, Filters.BIT_PLAYER);
        if (entity != null) {
            InteractableComponent interactableComponent = getEngine().createComponent(InteractableComponent.class);
            interactableComponent.setInteractable(new MultiInteractable(screen, entity) {
                @Override
                protected void init() {
                    addInteraction(GameManager.INSTANCE::setLevelDone);
                }
            });
            entity.add(interactableComponent);
        }

    }


    private void initNpcs() {
        // init the Loon
        ItemWrapper it = root.getChild(ChildrenNames.LOON);
        Entity entity = it.getEntity();

        if (entity != null) {
            setContactBytes(entity, Filters.BIT_INTERACTABLE, (short) (Filters.BIT_OBSTACLE |  Filters.BIT_PLAYER));
            InteractableComponent interactableComponent = getEngine().createComponent(InteractableComponent.class);

            MultiInteractable base = new Loon(screen, entity);
            base.setThreadManager(tm);
            MainItemComponent mainItem = ComponentRetriever.get(entity, MainItemComponent.class);
            base.setDialogueKey(mainItem.customVariables.getStringVariable("info"));

            interactableComponent.setInteractable(base);
            entity.add(interactableComponent);

            // threadStuff
            tm.submitRunnableForCountDown(entity);
            // threadStuff
        }

        // init the They
        entity = root.getChild(ChildrenNames.THEY).getEntity();

        if (entity != null) {
            setContactBytes(entity, Filters.BIT_INTERACTABLE, (short) (Filters.BIT_OBSTACLE |  Filters.BIT_PLAYER));
            InteractableComponent interactableComponent = getEngine().createComponent(InteractableComponent.class);

            MultiInteractable base = new They(screen, entity);
            base.setThreadManager(tm);
            MainItemComponent mainItem = ComponentRetriever.get(entity, MainItemComponent.class);
            base.setDialogueKey(mainItem.customVariables.getStringVariable("info"));

            interactableComponent.setInteractable(base);
            entity.add(interactableComponent);

            // threadStuff
            tm.submitRunnableForCountDown(entity);
            // threadStuff

        }


        // init the Mini
        entity = root.getChild(ChildrenNames.MINI).getEntity();

        if (entity != null) {
            setContactBytes(entity, Filters.BIT_INTERACTABLE, (short) (Filters.BIT_OBSTACLE |  Filters.BIT_PLAYER));
            InteractableComponent interactableComponent = getEngine().createComponent(InteractableComponent.class);

            MultiInteractable base = new Mini(screen, entity);
            base.setThreadManager(tm);
            MainItemComponent mainItem = ComponentRetriever.get(entity, MainItemComponent.class);
            base.setDialogueKey(mainItem.customVariables.getStringVariable("info"));

            interactableComponent.setInteractable(base);
            entity.add(interactableComponent);

            // threadStuff
            tm.submitRunnableForCountDown(entity);
            // threadStuff
        }

        // init the Mini
        entity = root.getChild(ChildrenNames.GRUMP).getEntity();

        if (entity != null) {
            setContactBytes(entity, Filters.BIT_INTERACTABLE, (short) (Filters.BIT_OBSTACLE |  Filters.BIT_PLAYER));
            InteractableComponent interactableComponent = getEngine().createComponent(InteractableComponent.class);

            interactableComponent.setInteractable(new Grump(screen, entity));
            entity.add(interactableComponent);
        }


//        sceneLoader.addComponentByTagName(ChildrenNames.NPC, NpcComponent.class);

    }

    private void initCage(){
        Entity entity = root.getChild(ChildrenNames.CAGE).getEntity();
        setContactBytes(entity, Filters.BIT_WORLD, (short) (Filters.BIT_WORLD | Filters.BIT_PLAYER | Filters.BIT_OBSTACLE));
        if (entity != null) {
        TransformComponent cageTransformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        PhysicsBodyComponent cageBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);

        ItemWrapper it = root.getChild(ChildrenNames.CAGE_ROPE);
        if (it.getEntity() != null) {
            it.addScript(new RopeScript(entity, cageTransformComponent.x, cageTransformComponent.y + 7.5f), (PooledEngine)getEngine());

        }

        entity = root.getChild(ChildrenNames.CAGE_WEIGHT).getEntity();
        setContactBytes(entity, Filters.BIT_WORLD, (short) (Filters.BIT_WORLD | Filters.BIT_PLAYER | Filters.BIT_OBSTACLE));


            TransformComponent cageWeightTransformComponent = ComponentRetriever.get(entity, TransformComponent.class);
            PhysicsBodyComponent cageWeightBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);

            it = root.getChild(ChildrenNames.WEIGHT_ROPE);
            it.addScript(new RopeScript(entity, cageWeightTransformComponent.x, cageWeightTransformComponent.y + 7.5f), (PooledEngine)getEngine());

            if (cageWeightBodyComponent == null || cageBodyComponent == null){
                log.debug("cage missing body");
                return;
            }
            PulleyJointDef pulleyJointDef = new PulleyJointDef();
            pulleyJointDef.bodyA = cageBodyComponent.body;
            pulleyJointDef.bodyB = cageWeightBodyComponent.body;

            pulleyJointDef.collideConnected = false;
            pulleyJointDef.localAnchorA.set(0,0);
            pulleyJointDef.localAnchorB.set(0,0);
            pulleyJointDef.groundAnchorA.set(cageTransformComponent.x, cageTransformComponent.y + 7.5f);
            pulleyJointDef.groundAnchorB.set(cageWeightTransformComponent.x, cageWeightTransformComponent.y + 7.5f);
            pulleyJointDef.lengthA = 5;
            pulleyJointDef.lengthB = 7.5f;

            sceneLoader.getWorld().createJoint(pulleyJointDef);

        }
    }
}
