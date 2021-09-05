package com.hermes.common;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.GUIScene;
import com.hermes.component.*;
import com.hermes.config.GameConfig;
import com.hermes.interactables.others.ItemInteractable;
import com.hermes.listener.InputListener;
import com.hermes.listener.ObservableInput;
import games.rednblack.editor.renderer.SceneLoader;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.components.NodeComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.data.CompositeItemVO;
import games.rednblack.editor.renderer.data.SpriteAnimationVO;
import games.rednblack.editor.renderer.factory.ActionFactory;
import games.rednblack.editor.renderer.systems.action.Actions;
import games.rednblack.editor.renderer.systems.action.data.ActionData;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;


public class EntityActions {
    private static final Logger log = new Logger(EntityActions.class.getName(), Logger.DEBUG);
    public static final String INTANGIBLE = "intangible";
    public static final String MOVE_TO = "moveTo";

    private ActionFactory factory;


    public static HashMap<String, ActionData> actionsData = new HashMap<>();

    public EntityActions(ActionFactory factory) {
        this.factory = factory;
        init();
    }

    private void init() {
        actionsData.put(INTANGIBLE, factory.loadFromLibrary(INTANGIBLE, false, null));
    }

    public static void addActionToEntity(String action, Entity entity, PooledEngine engine) {
        Actions.addAction(engine, entity, actionsData.get(action));
        actionsData.get(action).restart();



    }

    public static void moveTo(Entity entity, SceneLoader loader, float x, float y) {
        ObjectMap<String, Object> params = new ObjectMap<>();
        params.put("position", new Vector2(x, y));
        Actions.addAction(loader.getEngine(), entity, loader.getActionFactory().loadFromLibrary(MOVE_TO, params));
    }
    public static void removeActionFromEntity(String action, Entity entity) {
        Actions.removeAction(entity, actionsData.get(action));

    }

    public static void moveBy(Entity entity, float x, float y, PooledEngine engine) {
        if (x == 0 && y == 0) return;
        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        if (tc != null) {
            addDestination(entity, tc.x + x, tc.y + y, engine);
        }
    }

    public static void goTo(Entity entity, float x, float y, PooledEngine engine) {
        DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
        addDestination(entity, x - dc.width/2, y - dc.height/2, engine);
    }

    public static void goTo(Entity entity, Entity destinationEntity, PooledEngine engine, boolean overlap) {
        TransformComponent transformComponent = ComponentRetriever.get(destinationEntity, TransformComponent.class);
        DimensionsComponent dc = ComponentRetriever.get(destinationEntity, DimensionsComponent.class);
        if (overlap) {
            goTo(entity, transformComponent.x + dc.width / 2, transformComponent.y + dc.height / 2, engine);
        } else {
            goTo(entity, transformComponent.x - dc.width, transformComponent.y + dc.height / 2, engine );
        }
    }

    public static void addDestination(Entity entity, float x, float y, PooledEngine engine) {
        DestinationComponent dc = ComponentRetriever.get(entity, DestinationComponent.class);
        if (dc == null) {
            dc = engine.createComponent(DestinationComponent.class);
            entity.add(dc);
        }

        dc.addDestination(x, y);
        log.debug("added destination to: " + x + " " + y + " to " + entity);
        log.debug("destinations: " + dc.destinations.size);
        checkIfDestinationIsOccupied(entity, dc, engine);

    }

    public static void checkIfDestinationIsOccupied(Entity entity, DestinationComponent destinationComponent, PooledEngine engine) {
        PhysicsBodyComponent pc = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        if (pc != null) {
            World world = pc.body.getWorld();
            float x = destinationComponent.destinations.peek().x;
            float y = destinationComponent.destinations.peek().y;
            world.QueryAABB(
                    (fixture) -> {
                        if (fixture.getBody().getUserData() != null) {
                            Entity entity1 = (Entity)fixture.getBody().getUserData();
                            if (entity != entity1 && ComponentRetriever.get(entity1, NpcComponent.class) != null){
                                MainItemComponent mc = ComponentRetriever.get(entity1, MainItemComponent.class);
                                log.debug("occupied by " + mc.itemIdentifier);
                                goTo(entity, entity1, engine, false);
                            }
                            return false;

                        } else return true;
                    },
                    x-1f, y-1f,
                    x+1f, y+1f);
        }
    }

    public static void respawnPlayer(StateComponent state) {
        PlayerComponent pc = ComponentRetriever.get(state.owner, PlayerComponent.class);
        PhysicsBodyComponent bodyComponent = ComponentRetriever.get(state.owner, PhysicsBodyComponent.class);
        if (bodyComponent != null) {
            if (pc != null && !pc.checkPoint.epsilonEquals(0, 0)) {
                DimensionsComponent dm = ComponentRetriever.get(state.owner, DimensionsComponent.class);
                bodyComponent.body.setTransform(pc.checkPoint.x + dm.width, pc.checkPoint.y + dm.height, bodyComponent.body.getAngle());
            } else {
                bodyComponent.body.setTransform(2, 3, bodyComponent.body.getAngle());
            }
            bodyComponent.body.setAwake(true);
        }
        ComponentRetriever.get(state.owner, HealthComponent.class).heal(GameConfig.PLAYER_HEALTH);
        GUIScene.INSTANCE.addHealth(GameConfig.PLAYER_HEALTH);
        GameManager.INSTANCE.setRunning();
        state.setIdle();
    }

    public static void teleportEntity(Entity entity, Vector2 destination, SceneLoader sceneLoader) {
        teleportEntity(entity, destination.x, destination.y, sceneLoader);
    }

    public static void teleportEntity(Entity entity, float x, float y, SceneLoader sceneLoader) {
        if (entity == null) return;

        PhysicsBodyComponent pc = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);


        // width: 2 height : 4
        SpriteAnimationVO anim = new SpriteAnimationVO();
        anim.animationName = "teleport";
        anim.x = pc.body.getPosition().x - dc.width * tc.scaleX / 2;
        anim.y = pc.body.getPosition().y - dc.height * tc.scaleY / 2;
        anim.originX = 0;
        anim.originY = 0;
        anim.scaleX = dc.width * tc.scaleX / 2.16f;
        anim.scaleY = dc.height * tc.scaleY / 4f;

        anim.layerName = "detailsFront";

        Entity animation = sceneLoader.getEntityFactory().createEntity(sceneLoader.getRoot(), anim);
        ActionOnRemoveComponent rac = sceneLoader.getEngine().createComponent(ActionOnRemoveComponent.class);
        rac.onRemove = () -> {
            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
            if (bodyComponent != null) {
                bodyComponent.body.setTransform(x, y, 0);
                bodyComponent.body.applyLinearImpulse(0, -0.1f, bodyComponent.centerX, bodyComponent.centerY, true);
                bodyComponent.body.setLinearVelocity(0, 0);
                bodyComponent.body.setAngularVelocity(0);
            }
        };
        animation.add(rac);
        sceneLoader.getEngine().addEntity(animation);


    }

    public static void teleportEntity2(Entity entity, float x, float y) {
        if (entity == null) return;
        PhysicsBodyComponent bodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        if (bodyComponent != null) {
            bodyComponent.body.setTransform(x, y, bodyComponent.body.getAngle());
            bodyComponent.body.applyLinearImpulse(0, -0.1f, bodyComponent.centerX, bodyComponent.centerY, true);
        }
    }

    public static void dropLootAt(Entity entity, float x, float y, SceneLoader sceneLoader) {
        LootComponent lootComponent = ComponentRetriever.get(entity, LootComponent.class);

        if (lootComponent == null) return;

        CompositeItemVO item = sceneLoader.loadVoFromLibrary(lootComponent.itemName);
        item.layerName = lootComponent.layerName;
        item.x = x;
        item.y = y;

        Entity loot = sceneLoader.getEntityFactory().createEntity(sceneLoader.getRoot(), item);

        InteractableComponent interactableComponent = sceneLoader.getEngine().createComponent(InteractableComponent.class);
        interactableComponent.setInteractable(new ItemInteractable(loot, sceneLoader));

        loot.add(interactableComponent);

        sceneLoader.getEntityFactory().initAllChildren(sceneLoader.getEngine(), loot, item.composite);
        sceneLoader.getEngine().addEntity(loot);
    }

    public static void dropLoot(Entity entity, SceneLoader sceneLoader) {

        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);

        dropLootAt(entity,
                transformComponent.x + dimensionsComponent.width / 2,
                transformComponent.y + dimensionsComponent.height / 2,
                sceneLoader);

    }

    public static void setEntityVisibility(Entity entity, boolean visible) {
        if (entity != null) {
            MainItemComponent mc = ComponentRetriever.get(entity, MainItemComponent.class);
            if (mc != null) {
                mc.visible = visible;
            }
            PhysicsBodyComponent pc = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
            if (pc != null) {
                pc.body.setActive(visible);
//                log.debug(pc.body.isActive() + "");
            }

        }
    }

    public static void addListenerToInputProcessor(InputListener listener, boolean isPersistent) {
        InputProcessor inputProcessor = Gdx.input.getInputProcessor();
        if (inputProcessor instanceof ObservableInput) {
            ((ObservableInput) inputProcessor).addListener(listener, isPersistent);
        }
    }

    public static void setQMarkVisible(Entity entity, boolean visible) {
        ItemWrapper it = new ItemWrapper(entity);
        Entity entity2 = it.getChild(ChildrenNames.QUESTION_MARK).getEntity();
        if (entity2 == null) return;
        MainItemComponent mainItemComponent = ComponentRetriever.get(entity2, MainItemComponent.class);
        mainItemComponent.visible = visible;
    }

    public static void damageHitEntities(Array<Fixture> fixturesHit, PooledEngine engine) {
        for (Fixture fixture : fixturesHit) {
            Entity entity = (Entity)fixture.getBody().getUserData();
            HealthComponent healthComponent = ComponentRetriever.get(entity, HealthComponent.class);
            if (healthComponent != null) {
                healthComponent.damage(1);
                log.debug("player hp: " + healthComponent.health);

            } else return;
            PhysicsBodyComponent physicsBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
            if (physicsBodyComponent != null) {
                physicsBodyComponent.body.applyLinearImpulse(new Vector2(2 * physicsBodyComponent.body.getMass(), 0), physicsBodyComponent.centerOfMass, true);
            }

            Entity animEntity = getAnimationFromEntity(entity);
            if (animEntity != null) {
                ComponentRetriever.get(animEntity, StateComponent.class).setTakingDamage();
            }

            addActionToEntity(INTANGIBLE, entity, engine);

            if (fixture.getFilterData().categoryBits == Filters.BIT_PLAYER) {
                GUIScene.INSTANCE.removeHealth();
            }
        }
        fixturesHit.clear();
    }

    public static Entity getChildOfEntity(Entity entity, String childName) {
        NodeComponent nc = ComponentRetriever.get(entity, NodeComponent.class);
        if (nc != null) {
            for (Entity child : nc.children) {
                MainItemComponent childMain = ComponentRetriever.get(child, MainItemComponent.class);
                if (childMain.itemIdentifier.equals(childName)) {
                    return child;
                }
            }
        }
        return null;
    }

    public static Entity getAnimationFromEntity(Entity entity) {
        NodeComponent nc = ComponentRetriever.get(entity, NodeComponent.class);
        if (nc != null) {
            for (Entity child : nc.children) {
                StateComponent state = ComponentRetriever.get(child, StateComponent.class);
                if (state != null) {
                    return child;
                }
            }
        }
        return null;
    }


}
