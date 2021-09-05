package com.hermes.thread;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class SpikyPlatformRunnable implements Runnable {
    private final BlockingQueue<Entity> queue;
    private final CyclicBarrier barrier;
    private final Entity entity;

    private static final Logger log = new Logger(SpikyPlatformRunnable.class.getName(), Logger.DEBUG);

    public SpikyPlatformRunnable(BlockingQueue<Entity> queue, CyclicBarrier barrier, Entity entity) {
        this.queue = queue;
        this.barrier = barrier;
        this.entity = entity;
    }

    @Override
    public void run() {
        if (queue.contains(entity)) {
            try {
                barrier.await();
                queue.remove(entity);

            } catch (InterruptedException | BrokenBarrierException e) {
                log.debug(e.getMessage());
            }
        }
    }
}
