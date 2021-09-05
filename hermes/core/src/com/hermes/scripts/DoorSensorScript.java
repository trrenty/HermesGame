package com.hermes.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Logger;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.Filters;
import com.hermes.component.ThreadComponent;
import com.hermes.thread.ExpandableRunnable;
import com.hermes.thread.RockSensorRunnable;
import com.hermes.thread.ThreadManager;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.physics.PhysicsContact;
import games.rednblack.editor.renderer.scripts.IScript;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class DoorSensorScript implements IScript, PhysicsContact {

    private static final Logger log = new Logger(DoorSensorScriptBackUp.class.getName(), Logger.DEBUG);
    private final Entity door;

    private final Vector2 initialPositionSensor = new Vector2();
    private final Vector2 openPositionSensor = new Vector2();

    private float doorAccumulator;
    private final float doorSpeed = 2;
    private final float openDoorSeconds;

    private float sensorAccumulator;
    private final float sensorSpeed = 1;
    private float openSensorSeconds;

    private boolean isOpen = false;

    // THREAD STUFF
    private final Phaser phaser;
    private final ReentrantLock sharedLock;
    private final ReentrantLock playerLock;
    private final ReentrantLock rockLock;

    float timer = 0;
    private boolean scheduledCheck;
    private Entity sensor;

    private final ThreadManager tm;
    private Thread playerThread;
    private Thread rockThread;




    public DoorSensorScript(Entity door, ThreadManager tm) {
        this.door = door;
        TransformComponent transformComponent = ComponentRetriever.get(door, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(door, DimensionsComponent.class);

        openDoorSeconds = dimensionsComponent.width * transformComponent.scaleX / doorSpeed;

        // THREAD STUFF

        this.tm = tm;
        phaser = tm.getPhaser();
        sharedLock = tm.getSharedLock();
        playerLock = tm.getPlayerLock();
        rockLock = tm.getRockLock();

    }

    @Override
    public void init(Entity entity) {
        this.sensor = entity;
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        initialPositionSensor.set(transformComponent.x, transformComponent.y);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        openPositionSensor.set(transformComponent.x, transformComponent.y - dimensionsComponent.height / 15f);

        openSensorSeconds = dimensionsComponent.height / 10f * transformComponent.scaleY / sensorSpeed;


        // threadStuff
        playerThread = new Thread(new ExpandableRunnable(phaser, new RockSensorRunnable(playerLock, sharedLock)));
        rockThread = new Thread(new ExpandableRunnable(phaser, new RockSensorRunnable(rockLock, sharedLock)));
        playerThread.setDaemon(true);
        rockThread.setDaemon(true);
        playerThread.start();
        rockThread.start();

    }

    @Override
    public void act(float delta) {
        if (isOpen && doorAccumulator < openDoorSeconds) {
            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(door, PhysicsBodyComponent.class);
            doorAccumulator += delta;
            bodyComponent.body.setLinearVelocity(doorSpeed, 0f);

        } else if (!isOpen && doorAccumulator > 0) {
            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(door, PhysicsBodyComponent.class);
            doorAccumulator -= 4*delta;
            bodyComponent.body.setLinearVelocity(-4 * doorSpeed, 0f);
        }
        else {

            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(door, PhysicsBodyComponent.class);
            bodyComponent.body.setLinearVelocity(0f, 0f);
        }

        if (isOpen && sensorAccumulator < openSensorSeconds) {
            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(sensor, PhysicsBodyComponent.class);
            sensorAccumulator += delta;
            bodyComponent.body.setLinearVelocity(0f, -1f);

        } else if (!isOpen && sensorAccumulator > 0) {
            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(sensor, PhysicsBodyComponent.class);
            sensorAccumulator -= delta;
            bodyComponent.body.setLinearVelocity(0f, 1f);

        }
        else {

            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(sensor, PhysicsBodyComponent.class);
            bodyComponent.body.setLinearVelocity(0f, 0f);
        }

        // THREAD STUFF

        timer += delta;
        if (timer >0.1f && scheduledCheck) {
            if (sharedLock.isLocked()) {
                log.debug("opening door");
                isOpen = true;
            } else {
                log.debug("closing door");
                isOpen = false;
            }
            scheduledCheck = false;
        }

    }

    @Override
    public void dispose() {

    }

    @Override
    public void beginContact(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {
        if (ComponentRetriever.get(contactEntity, ThreadComponent.class) != null) {
            if (contactFixture.getUserData().equals(Filters.BIT_PLAYER)) {
                playerLock.lock();
                phaser.arriveAndAwaitAdvance();

                if (sharedLock.isLocked()) {
                    GUIScene.INSTANCE.notifyPlayer(BundleKeys.LOCK_TRIED, "Hermes");
                } else {
                    GUIScene.INSTANCE.notifyPlayer(BundleKeys.LOCK_ACQUIRED, "Hermes");
                }

            } else if (contactFixture.getFilterData().categoryBits == Filters.BIT_OBSTACLE){
                rockLock.lock();
                phaser.arriveAndAwaitAdvance();

                if (sharedLock.isLocked()) {
                    GUIScene.INSTANCE.notifyPlayer(BundleKeys.LOCK_TRIED, "The Rock");
                } else {
                    GUIScene.INSTANCE.notifyPlayer(BundleKeys.LOCK_ACQUIRED, "The Rock");
                }

            }
            timer = 0;
            scheduledCheck = true;
        }
    }

    @Override
    public void endContact(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {
        if (ComponentRetriever.get(contactEntity, ThreadComponent.class) != null) {
            if (contactFixture.getUserData().equals(Filters.BIT_PLAYER)) {

                playerLock.unlock();
                if (sharedLock.hasQueuedThreads()) {
                    playerThread.interrupt();
                }
                phaser.arriveAndAwaitAdvance();

            } else if (contactFixture.getFilterData().categoryBits == Filters.BIT_OBSTACLE){

                rockLock.unlock();
                if (sharedLock.hasQueuedThread(rockThread)) {
                    rockThread.interrupt();
                }
                phaser.arriveAndAwaitAdvance();

            }
            timer = 0;
            scheduledCheck = true;
        }
    }

    @Override
    public void preSolve(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {

    }

    @Override
    public void postSolve(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {

    }
}



