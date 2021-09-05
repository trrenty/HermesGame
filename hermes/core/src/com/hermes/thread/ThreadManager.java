package com.hermes.thread;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.hermes.states.EmptyState;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadManager {

    // INSTANCE
    private static final Logger log = new Logger(ThreadManager.class.getName(), Logger.DEBUG);
    // GENERAL STFF
    private final Phaser phaser;
    private final ExecutorService executor;

    // ROCK SENSOR SYNCHRONIZER
    private final ReentrantLock sharedLock;
    private final ReentrantLock playerLock;
    private final ReentrantLock rockLock;

    // GHOSTS COUNTDOWN LATCH
    private final CountDownLatch latch;
    public final BlockingQueue<Entity> queue;

    // PLATFORMS STUFF
    public final CyclicBarrier barrier;
    private volatile boolean movesToRight = true;

    public boolean movesToRight() {
        return movesToRight;
    }

    public ThreadManager() {
        this.phaser = new Phaser(1);
        this.executor = Executors.newFixedThreadPool(10);

        this.sharedLock = new ReentrantLock();
        this.playerLock = new ReentrantLock();
        this.rockLock = new ReentrantLock();

        this.latch = new CountDownLatch(4);
        this.queue = new ArrayBlockingQueue<>(4);

        barrier = new CyclicBarrier(3, this::changeDirection);

    }

    public synchronized void changeDirection() {
        movesToRight = !movesToRight;
    }

    public Future<?> submitRunnableForCountDown(Entity entity) {
        return executor.submit(new ExpandableRunnable(phaser, new CountDownRunnable(queue, latch, entity)));
    }

    public Future<?> submitRunnableForBarrier(Entity entity) {
        return executor.
                submit(
                        new ExpandableRunnable(phaser,
                                new SpikyPlatformRunnable(queue, barrier, entity)));
    }


    public void end() {
        phaser.arriveAndDeregister();
        executor.shutdownNow();
    }

    public Phaser getPhaser() {
        return phaser;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public ReentrantLock getSharedLock() {
        return sharedLock;
    }

    public ReentrantLock getPlayerLock() {
        return playerLock;
    }

    public ReentrantLock getRockLock() {
        return rockLock;
    }

    public void addToQueueAndAdvance(Entity entity) {
        queue.offer(entity);
        phaser.arriveAndAwaitAdvance();
    }

    public void clearQueue() {
        queue.clear();
    }

    public void countDown() {
        latch.countDown();
    }
}
