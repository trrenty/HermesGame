package com.hermes.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Logger;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.common.Filters;
import com.hermes.component.*;
import com.hermes.config.GameConfig;
import com.hermes.states.CharacterState;
import com.hermes.states.State;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.NodeComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.physics.PhysicsContact;
import games.rednblack.editor.renderer.scripts.IScript;
import games.rednblack.editor.renderer.systems.action.Actions;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class PlayerCollisionScript implements PhysicsContact, IScript {
    private static final Logger log = new Logger(PlayerCollisionScript.class.getName(), Logger.DEBUG);
    private Entity player;
    private final PooledEngine engine;
    private float lastHit = 0;
    private InteractableComponent current;
    private StateComponent playerState;


    public PlayerCollisionScript(PooledEngine engine) {
        this.engine = engine;
    }

    @Override
    public void beginContact(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {
//        log.debug("contact: " + contactFixture.getUserData());
        checkForDamage(contactEntity, contactFixture, contact);
        checkForInteractable(contactEntity);
        checkForCheckPoint(contactEntity);
        checkTouchingPlatform(contactFixture, ownFixture, true);

        float velocityX = contactFixture.getBody().getLinearVelocity().x;
        float velocityY = contactFixture.getBody().getLinearVelocity().y;
        float mass = contactFixture.getBody().getMass();

        float energyOnImpactX = velocityX * velocityX * mass * 0.5f;
        float energyOnImpactY = velocityY * velocityY * mass * 0.5f;

        if ((energyOnImpactX >= 100 || energyOnImpactY >= 100) &&
        !ownFixture.getBody().getLinearVelocity().epsilonEquals(contactFixture.getBody().getLinearVelocity())) {
            HealthComponent hc = ComponentRetriever.get(player, HealthComponent.class);
            log.debug(energyOnImpactX + " " + energyOnImpactY);
            int damage = Math.max((int)energyOnImpactX / 100, (int)energyOnImpactY / 100);
            log.debug(damage + "");
            hc.damage(damage);
            GUIScene.INSTANCE.removeHealth(damage);
            playerState.setTakingDamage();
        }



        if (contactFixture.getFilterData().categoryBits == Filters.BIT_OBSTACLE) {
            TransformComponent playerTc = ComponentRetriever.get(player, TransformComponent.class);
            TransformComponent contactTc = ComponentRetriever.get(contactEntity, TransformComponent.class);

            if (MathUtils.isEqual(playerTc.y, contactTc.y, 0.5f)) {
                playerState.shouldPush = true;
                log.debug("push!!!");
            }
            else {
                log.debug("different heights? " + playerTc.y + " " + contactTc.y);
            }
        }
    }

    private void checkForCheckPoint(Entity contactEntity) {
        CheckpointComponent ck = ComponentRetriever.get(contactEntity, CheckpointComponent.class);
        if (ck != null) {
            PlayerComponent pc = ComponentRetriever.get(player, PlayerComponent.class);
            TransformComponent tc = ComponentRetriever.get(contactEntity, TransformComponent.class);
            pc.checkPoint.set(tc.x, tc.y);
        }
    }


    @Override
    public void endContact(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {

        if (contactFixture.getFilterData().categoryBits == Filters.BIT_OBSTACLE) {
                playerState.shouldPush = false;
        }

        checkTouchingPlatform(contactFixture, ownFixture, false);
        InteractableComponent infoComponent = ComponentRetriever.get(contactEntity, InteractableComponent.class);
        if (infoComponent != null) {
            if (current == infoComponent) {
                infoComponent.endCotact();
                current = null;
            } else {
                infoComponent.endCotact();
            }
        }
    }

    @Override
    public void init(Entity entity) {
        this.player = entity;
        Entity animationEntity = EntityActions.getAnimationFromEntity(entity);
        if (animationEntity == null) return;
        playerState = ComponentRetriever.get(animationEntity, StateComponent.class);
    }

    @Override
    public void act(float delta) {
        lastHit += delta;
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && current != null) {
            current.interact();
        }
    }

    @Override
    public void dispose() {

    }
    @Override
    public void preSolve(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {
        NpcComponent npcComp = ComponentRetriever.get(contactEntity, NpcComponent.class);
        EnemyComponent enemyComponent = ComponentRetriever.get(contactEntity, EnemyComponent.class);
        if (npcComp != null || enemyComponent != null) {
            contact.setEnabled(false);
        }
        if (contactFixture.getFilterData().categoryBits == Filters.BIT_WORLD || contactFixture.getFilterData().categoryBits == Filters.BIT_OBSTACLE) {
            TransformComponent playerTc = ComponentRetriever.get(player, TransformComponent.class);
            TransformComponent contactTc = ComponentRetriever.get(contactEntity, TransformComponent.class);

            DimensionsComponent playerDim = ComponentRetriever.get(player, DimensionsComponent.class);
            DimensionsComponent contactDim = ComponentRetriever.get(contactEntity, DimensionsComponent.class);

            if (playerTc.y + playerDim.height / 2 < contactTc.y ) {
                contact.setFriction(0);
            } else if (playerTc.y < contactTc.y + contactDim.height - 0.1f && playerTc.y + playerDim.height / 2 >= contactTc.y + contactDim.height) {
                contact.setFriction(0);
                PhysicsBodyComponent pc = ComponentRetriever.get(player, PhysicsBodyComponent.class);
                pc.body.applyLinearImpulse(0, pc.mass/2, pc.centerX, pc.centerY, true);
            }
        }
    }

    @Override
    public void postSolve(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {

    }

    private void checkTouchingPlatform(Fixture contactFixture, Fixture ownFixture, boolean in) {

        if (ownFixture.getUserData() != null && ownFixture.getUserData().equals(Filters.BIT_PLAYER)) {
            if (contactFixture.getFilterData().categoryBits == Filters.BIT_WORLD || contactFixture.getFilterData().categoryBits == Filters.BIT_OBSTACLE) {
                if (playerState == null) return;
                if (in)
                    playerState.incrementTouchingPlatforms();
                else playerState.decrementTouchingPlatforms();
            }
        }
    }

    private void checkForInteractable(Entity contactEntity) {
        InteractableComponent infoComponent = ComponentRetriever.get(contactEntity, InteractableComponent.class);
        if (infoComponent != null) {
            if (current != null) {
                current.endCotact();
            }
            infoComponent.beginContact();
            current = infoComponent;
        }
    }

    private void checkForDamage(Entity contactEntity, Fixture contactFixture, Contact contact) {
        DamageComponent damageComponent = ComponentRetriever.get(contactEntity, DamageComponent.class);
        Entity auxEntity;
        if (damageComponent == null && contactFixture.getUserData() != null && contactFixture.getUserData() instanceof Entity) {
            auxEntity = (Entity) contactFixture.getUserData();
            damageComponent = ComponentRetriever.get(auxEntity, DamageComponent.class);
        }
        if ((damageComponent != null ) && lastHit > GameConfig.PLAYER_INVULNERABILITY) {
            if (playerState.isCrouching() || playerState.isCrouchWalking()) {
               //
            }
            lastHit = 0;
            EntityActions.addActionToEntity(EntityActions.INTANGIBLE, player, engine);
            HealthComponent healthComponent = ComponentRetriever.get(player, HealthComponent.class);
            if (healthComponent != null) {
                healthComponent.damage(damageComponent.damage);
                GUIScene.INSTANCE.removeHealth();

                Entity entity1 = EntityActions.getAnimationFromEntity(player);
                if (entity1 != null) {
                    StateComponent stateComponent = ComponentRetriever.get(entity1, StateComponent.class);
                    if (stateComponent != null) {
                        stateComponent.setTakingDamage();
                    }
                }

            }
            PhysicsBodyComponent physicsBodyComponent = ComponentRetriever.get(player, PhysicsBodyComponent.class);
            if (physicsBodyComponent != null) {
                Vector2 velocity = physicsBodyComponent.body.getLinearVelocity();
                physicsBodyComponent.body.applyLinearImpulse(0, -velocity.y * 2, physicsBodyComponent.centerX, physicsBodyComponent.centerY, true);

            }
            PhysicsBodyComponent pc = ComponentRetriever.get(contactEntity, PhysicsBodyComponent.class);
            log.debug(pc.body.isActive() + "");
        }
    }
}
