package com.hermes.thread;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class CountDownRunnable implements Runnable {

    private static final Logger log = new Logger(CountDownRunnable.class.getName(), Logger.DEBUG);
    private final BlockingQueue<Entity> queue;
    private final CountDownLatch latch;
    private final Entity entity;

    public CountDownRunnable(BlockingQueue<Entity> queue, CountDownLatch latch, Entity entity) {
        this.queue = queue;
        this.latch = latch;
        this.entity = entity;
    }

    @Override
    public void run() {
        if (queue.contains(entity)) {
            log.debug(entity + " counting down");
            latch.countDown();
            try {
                log.debug(entity + " awaiting");
                latch.await();
                log.debug(entity + " done awaiting");

            } catch (InterruptedException e) {
                latch.countDown();
                log.debug(entity + "intrerupted");
            }
        }
    }
}