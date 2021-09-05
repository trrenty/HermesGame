package com.hermes.system.passive;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.*;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.common.Filters;
import com.hermes.common.Inventory;
import com.hermes.component.*;
import com.hermes.interactables.BaseInteractable;
import com.hermes.interactables.MultiInteractable;
import com.hermes.interactables.others.ItemInteractable;
import com.hermes.interactables.others.LabelLinkedInfoInteractable;
import com.hermes.interactables.others.MultiInfoInteractable;
import com.hermes.interactables.others.PortalInteractable;
import com.hermes.screens.game.LevelScreen;
import com.hermes.scripts.*;
import com.hermes.states.CharacterState;
import com.hermes.states.enemy.AttackState;
import com.hermes.states.enemy.IdleState;
import com.hermes.states.enemy.RunState;
import com.hermes.states.enemy.TakingDamageState;
import com.hermes.thread.ThreadManager;
import games.rednblack.editor.renderer.SceneLoader;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.components.SpineDataComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.label.LabelComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;
import games.rednblack.h2d.extention.spine.SpineObjectComponent;

import java.util.ArrayList;
import java.util.List;

public class InitSystem extends EntitySystem {

    protected static final Logger log = new Logger(InitSystem.class.getName(), Logger.DEBUG);

    protected final ItemWrapper root;

    protected final SceneLoader sceneLoader;
    protected final LevelScreen screen;
    protected ThreadManager tm;


    public InitSystem(ItemWrapper root, LevelScreen screen) {
        this.root = root;

        this.sceneLoader = screen.getSceneLoader();
        this.screen = screen;
        tm = screen.getThreadManager();
    }

    @Override
    public void update(float deltaTime) {
        // player item

        addStateComponentToSpriteAnimations();

        initPlayer();

        initWorldItemsCollisionLayer();

        initLevelSpecificThings();

        getEngine().removeSystem(this);

    }

    protected void initLevelSpecificThings() {

    }

    private void addStateComponentToSpriteAnimations() {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(SpineDataComponent.class, SpineObjectComponent.class).get());
        for (Entity entity : entities) {
            MainItemComponent mc = ComponentRetriever.get(entity, MainItemComponent.class);
            if (mc.itemIdentifier.equals(ChildrenNames.ANIMATION)) {
                entity.add(getEngine().createComponent(StateComponent.class));
            }
        }
    }

    private void initWorldItemsCollisionLayer() {

        Entity entity;
        ItemWrapper item;
        // set byte to world
        ImmutableArray<Entity> entities3 = getEngine().getEntities();
        for (int i = 0; i< entities3.size(); i++) {
            MainItemComponent mainItemComponent = ComponentRetriever.get(entities3.get(i), MainItemComponent.class);
            log.debug(mainItemComponent.itemIdentifier);


            for (String tag : new ObjectSet.ObjectSetIterator<>(mainItemComponent.tags)) {
                switch (tag) {
                    case ChildrenNames.PLATFORM_TAG:
                        setContactBytes(entities3.get(i), Filters.BIT_WORLD, (short) (Filters.BIT_PLAYER | Filters.BIT_OBSTACLE | Filters.BIT_ENEMY | Filters.BIT_WORLD));
                        continue;
                    case ChildrenNames.SPIKE_TAG:
                        setContactBytes(entities3.get(i), Filters.BIT_ENEMY, Filters.BIT_PLAYER);
                        entities3.get(i).add(getEngine().createComponent(DamageComponent.class));
                        continue;
                    case ChildrenNames.MOVING_PLATFORM_TAG:
                        item = new ItemWrapper(entities3.get(i));
                        item.addScript(new LoopMoveScript(), (PooledEngine) getEngine());
                        continue;
                    case ChildrenNames.SPIKY_PLATFORM:
                        item = new ItemWrapper(entities3.get(i));
                        item.addScript(new SpikyPlatformScript(screen.getThreadManager()), (PooledEngine) getEngine());
                        continue;
                    case ChildrenNames.LICURICI:
                        entities3.get(i).add(getEngine().createComponent(LicuriciComponent.class));
//                        log.debug(zIndexComponent.layerName);
                        continue;
                    case ChildrenNames.HIDDEN:
                        EntityActions.setEntityVisibility(entities3.get(i), false);
                        log.debug(mainItemComponent.itemIdentifier);
//                        log.debug(zIndexComponent.layerName);
                        continue;
                    case ChildrenNames.WATER:
                        item = new ItemWrapper(entities3.get(i));
                        item.addScript(new WaterScript(), (PooledEngine)getEngine());
//                        log.debug(zIndexComponent.layerName);
                        continue;
                    case ChildrenNames.ITEM:
                        InteractableComponent lootInteractable = sceneLoader.getEngine().createComponent(InteractableComponent.class);
                        lootInteractable.setInteractable(new ItemInteractable(entities3.get(i), sceneLoader));
                        entities3.get(i).add(lootInteractable);
                        continue;
                    case ChildrenNames.OALE:
                        setContactBytes(entities3.get(i), Filters.BIT_ENEMY, (short) -1);
                        HealthComponent healthComponent = getEngine().createComponent(HealthComponent.class);
                        healthComponent.health = 1;
                        healthComponent.maxHealth = 1;
                        entities3.get(i).add(healthComponent);
                        continue;
                    case ChildrenNames.LOOTABLE:
                        String itemName = mainItemComponent.customVariables.getStringVariable("itemName");
                        if (itemName != null) {
                            LootComponent lootComponent = getEngine().createComponent(LootComponent.class);
                            lootComponent.itemName = itemName;
                            entities3.get(i).add(lootComponent);
                        }
                        continue;
                    case ChildrenNames.INTERACTABLES:
                        setContactBytes(entities3.get(i), Filters.BIT_INTERACTABLE, (short) (Filters.BIT_WORLD | Filters.BIT_PLAYER));
                        InteractableComponent interactableComponent = getEngine().createComponent(InteractableComponent.class);
                        interactableComponent.setInteractable(new BaseInteractable(entities3.get(i)));
                        log.debug("here?????");
                        entities3.get(i).add(interactableComponent);
                        log.debug("added interactable");
                        continue;
                    case ChildrenNames.MULTI_INTERACTABLE:
                        setContactBytes(entities3.get(i), Filters.BIT_INTERACTABLE, (short) (Filters.BIT_WORLD | Filters.BIT_PLAYER));
                        InteractableComponent interactableComponent2 = getEngine().createComponent(InteractableComponent.class);

                        String labelName = mainItemComponent.customVariables.getStringVariable("label");
                        BaseInteractable multi = null;
                        if (labelName != null) {
                            String labelContentKey = mainItemComponent.customVariables.getStringVariable("labelInfo");
                            Entity labelEntity = root.getChild(labelName).getEntity();
                            if (labelEntity!= null) {
                                LabelComponent lc = ComponentRetriever.get(labelEntity, LabelComponent.class);
                                if (lc != null) {
                                    multi = new LabelLinkedInfoInteractable(entities3.get(i), lc, labelContentKey);
                                }
                            }
                        }
                        if (multi == null) {
                            multi = new MultiInfoInteractable(entities3.get(i));
                        }

                        interactableComponent2.setInteractable(multi);
                        entities3.get(i).add(interactableComponent2);
                        continue;
                    case ChildrenNames.CHECKPOINT_TAG:
                        entities3.get(i).add(getEngine().createComponent(CheckpointComponent.class));
                        continue;
                    case ChildrenNames.NPC:
                        entities3.get(i).add(getEngine().createComponent(NpcComponent.class));
                        continue;
                    case ChildrenNames.ROCK:
                        setContactBytes(entities3.get(i), Filters.BIT_OBSTACLE, (short) -1);
                        continue;
                    case ChildrenNames.ZOOM_OUT_TAG:
                        entities3.get(i).add(getEngine().createComponent(CameraZoomOutComponent.class));
                        setContactBytes(entities3.get(i), Filters.BIT_ENEMY, Filters.BIT_PLAYER);
                        continue;
                    case ChildrenNames.PARALLAX:

                        float parallaxX = mainItemComponent.customVariables.getFloatVariable("parallaxX") == null ? 1 : mainItemComponent.customVariables.getFloatVariable("parallaxX");
                        float parallaxY = mainItemComponent.customVariables.getFloatVariable("parallaxY") == null ? 1 : mainItemComponent.customVariables.getFloatVariable("parallaxY");
                        boolean shouldRepeat = mainItemComponent.customVariables.getIntegerVariable("shouldRepeat") == null || mainItemComponent.customVariables.getIntegerVariable("shouldRepeat") != 0;
                        boolean shouldScale = !(mainItemComponent.customVariables.getIntegerVariable("shouldScale") == null || mainItemComponent.customVariables.getIntegerVariable("shouldScale") == 0);
                        log.debug(parallaxX + " " + parallaxY + " " + shouldRepeat + " " + shouldScale);
                        initParallaxFor(entities3.get(i), parallaxX, parallaxY, shouldRepeat, shouldScale);

                        continue;
                    case ChildrenNames.THREAD:
                        entities3.get(i).add(getEngine().createComponent(ThreadComponent.class));
                        continue;
                    case ChildrenNames.ENEMY:
                        setContactBytes(entities3.get(i), Filters.BIT_ENEMY, (short) (Filters.BIT_WORLD | Filters.BIT_PLAYER | Filters.BIT_ENEMY));
                        entities3.get(i).add(getEngine().createComponent(HealthComponent.class));

                        Entity animationEntity = EntityActions.getAnimationFromEntity(entities3.get(i));
                        if (animationEntity != null) {
                            StateComponent stateComponent = ComponentRetriever.get(animationEntity, StateComponent.class);

                            stateComponent.owner = entities3.get(i);
                            stateComponent.animationEntity = animationEntity;
                            stateComponent.stateMachine.addState(CharacterState.IDLE, new IdleState(stateComponent, (PooledEngine) getEngine()));
                            stateComponent.stateMachine.addState(CharacterState.ATTACK, new AttackState(stateComponent, (PooledEngine) getEngine()));
                            stateComponent.stateMachine.addState(CharacterState.RUNNING, new RunState(stateComponent, (PooledEngine) getEngine()));
                            stateComponent.stateMachine.addState(CharacterState.TAKING_DAMAGE, new TakingDamageState(stateComponent));
                            stateComponent.setIdle();

                            entities3.get(i).add(stateComponent);
                            entities3.get(i).add(getEngine().createComponent(EnemyComponent.class));
                        }
                        entities3.get(i).add(getEngine().createComponent(DamageComponent.class));
                        continue;
                    case ChildrenNames.PORTAL:
                        setContactBytes(entities3.get(i), Filters.BIT_INTERACTABLE, (short) (Filters.BIT_WORLD | Filters.BIT_PLAYER));
                        InteractableComponent interactableComponent3 = getEngine().createComponent(InteractableComponent.class);
                        interactableComponent3.setInteractable(new PortalInteractable(entities3.get(i), root, sceneLoader));
                        entities3.get(i).add(interactableComponent3);


                }
            }
        }

        // add component to state debug
        entity = root.getChild(ChildrenNames.STATE_DEBUG).getEntity();
        if (entity != null) {
            entity.add(getEngine().createComponent(StateDebugComponent.class));
        }



    }

    private void initPlayer() {
        ItemWrapper item = root.getChild(ChildrenNames.PLAYER_ID);
        // add  collision detection
        // player entity
        Entity entity = item.getEntity();

        HealthComponent hc = getEngine().createComponent(HealthComponent.class);

        for (int i = 0; i < hc.health; i++) {
            GUIScene.INSTANCE.addHealth();
        }

        entity.add(hc);
//        entity.add(getEngine().createComponent(StateComponent.class));
        entity.add(getEngine().createComponent(PlayerComponent.class));
        setContactBytes(entity, Filters.BIT_PLAYER, (short) -1);
        addGroundSensorToEntity(entity);
        // add player control
        PlayerControllerScriptV2 controller = new PlayerControllerScriptV2(sceneLoader.getWorld(), sceneLoader.getEngine());
        item.addScript(controller, (PooledEngine)getEngine());

        item.addScript(new PlayerCollisionScript((PooledEngine)getEngine()), (PooledEngine)getEngine());

    }

    private void initParallaxFor(Entity entity, float parallaxX, float parallaxY) {
        initParallaxFor(entity, parallaxX, parallaxY, true, false);
    }

    private void initParallaxFor(Entity entity, float parallaxX, float parallaxY, boolean shouldRepeat, boolean shouldScale) {
        TransformComponent transformComponent;
        transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        ParallaxComponent parallaxComponent = getEngine().createComponent(ParallaxComponent.class);
        parallaxComponent.parallaxCoefficientX = parallaxX;
        parallaxComponent.parallaxCoefficientY = parallaxY;
        parallaxComponent.shouldRepeat = shouldRepeat;
        parallaxComponent.shouldScale = shouldScale;
        parallaxComponent.initialPosition.set(transformComponent.x, transformComponent.y);
        entity.add(parallaxComponent);
    }



    public static void setContactBytes(Entity entity, short category, short mask) {
        if (entity == null) return;
        PhysicsBodyComponent bodyComponent;
        bodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        if (bodyComponent == null) return;

        for (Fixture fixture : bodyComponent.body.getFixtureList()) {
            fixture.getFilterData().categoryBits = category;
            fixture.getFilterData().maskBits = mask;
        }
    }

    private static void addGroundSensorToEntity(Entity entity) {
        PhysicsBodyComponent physicsBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        if (physicsBodyComponent == null || dimensionsComponent == null) return;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
                dimensionsComponent.width / 2 - 0.1f,
                0.1f,
                new Vector2(transformComponent.originX - dimensionsComponent.width/2, transformComponent.originY - dimensionsComponent.height),
                0);
        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.isSensor = true;
        def.filter.categoryBits = Filters.BIT_PLAYER;
        def.filter.maskBits = Filters.BIT_WORLD | Filters.BIT_OBSTACLE;

        physicsBodyComponent.body.createFixture(def).setUserData(Filters.BIT_PLAYER);
        shape.dispose();
    }
}
