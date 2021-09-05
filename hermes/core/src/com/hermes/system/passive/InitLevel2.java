package com.hermes.system.passive;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.hermes.assets.ChildrenNames;
import com.hermes.common.Filters;
import com.hermes.component.ActionOnRemoveComponent;
import com.hermes.component.DamageComponent;
import com.hermes.component.InteractableComponent;
import com.hermes.interactables.Interactable;
import com.hermes.interactables.npc.level2.Bully;
import com.hermes.interactables.npc.level2.Charon;
import com.hermes.screens.game.LevelScreen;
import com.hermes.scripts.SensorScript;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class InitLevel2 extends InitSystem {
    public InitLevel2(ItemWrapper root, LevelScreen screen) {
        super(root, screen);
    }

    @Override
    protected void initLevelSpecificThings() {
        ItemWrapper it = root.getChild(ChildrenNames.ROCK_DESTROYER);
        if (it.getEntity() != null) {
            SensorScript ss = new SensorScript() {
                @Override
                public void beginContact(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {
                    float velocityX = contactFixture.getBody().getLinearVelocity().x;
                    float velocityY = contactFixture.getBody().getLinearVelocity().y;
                    float mass = contactFixture.getBody().getMass();

                    float energyOnImpactX = velocityX * velocityX * mass * 0.5f;
                    float energyOnImpactY = velocityY * velocityY * mass * 0.5f;

                    if (energyOnImpactY >= 100 || energyOnImpactX >= 100) {
                        ItemWrapper it = root.getChild(ChildrenNames.SUSTAIN_PLATFORM);
                        if (it.getEntity() != null) {
                            sceneLoader.getEngine().removeEntity(it.getEntity());
                        }
                    }
                }
            };
            it.addScript(ss, (PooledEngine)getEngine());
        }

        for (int i = 0; i < 5; i++) {
            Entity ent = root.getChild("spikedObstacle" + i).getEntity();
            if (ent == null) break;
            PhysicsBodyComponent pc = ComponentRetriever.get(ent, PhysicsBodyComponent.class);
            DimensionsComponent dimensionsComponent = ComponentRetriever.get(ent, DimensionsComponent.class);
            TransformComponent tc = ComponentRetriever.get(ent, TransformComponent.class);
            if (pc!= null) {

                PolygonShape shape = new PolygonShape();
                Vector2 position = new Vector2(pc.centerOfMass);
                position.y += 1;
                shape.setAsBox(
                        dimensionsComponent.width * tc.scaleX / 2,
                        dimensionsComponent.height * tc.scaleY / 4,
                        position,
                        0);
                FixtureDef def = new FixtureDef();
                def.shape = shape;
                def.filter.categoryBits = Filters.BIT_OBSTACLE;
                def.filter.maskBits = Filters.BIT_PLAYER;

                Entity dmgEnt = getEngine().createEntity().add(getEngine().createComponent(DamageComponent.class));
                getEngine().addEntity(dmgEnt);

                pc.body.createFixture(def).setUserData(dmgEnt);
                shape.dispose();

            }
        }

        initNpcs();

        initCoinBehaviour();
    }

    private void initNpcs() {
        ItemWrapper it;
        // init charon

        it = root.getChild(ChildrenNames.CHARON);
        if (it.getEntity() != null) {
            setContactBytes(it.getEntity(), Filters.BIT_INTERACTABLE, (short) (Filters.BIT_OBSTACLE |  Filters.BIT_PLAYER));
            InteractableComponent interactableComponent = getEngine().createComponent(InteractableComponent.class);

            Interactable base = new Charon(screen, it.getEntity());
            interactableComponent.setInteractable(base);
            it.getEntity().add(interactableComponent);
        }

        // init Bully
        it = root.getChild("Bully");
        if (it.getEntity() != null) {
            InteractableComponent interactableComponent = getEngine().createComponent(InteractableComponent.class);

            Interactable base = new Bully(screen, it.getEntity());
            interactableComponent.setInteractable(base);
            it.getEntity().add(interactableComponent);
        }
    }

    private void initCoinBehaviour() {
        ItemWrapper it;
        it = root.getChild("coin1");
        if (it.getEntity()!= null) {
            ActionOnRemoveComponent action = getEngine().createComponent(ActionOnRemoveComponent.class);
            action.onRemove = () -> {
                for (int i = 0; i < 5; i++) {
                    Entity ent = root.getChild("spikedObstacle" + i).getEntity();
                    if (ent == null) break;
//                    TransformComponent pc = ComponentRetriever.get(ent, TransformComponent.class);
//                    if (pc!= null) {
//                        pc.flipY = !pc.flipY;
//                    }
                    PhysicsBodyComponent pc = ComponentRetriever.get(ent, PhysicsBodyComponent.class);
                    if (pc!=null) {
                        pc.body.setTransform(pc.body.getPosition(), pc.body.getAngle() + MathUtils.PI);
                    }
                }
            };
            it.getEntity().add(action);
        }
    }
}
