package com.hermes.thread;

import com.badlogic.gdx.utils.Logger;

import java.util.concurrent.locks.ReentrantLock;

public class RockSensorRunnable implements Runnable {

    private static final Logger log = new Logger(RockSensorRunnable.class.getName(), Logger.DEBUG);
    private final ReentrantLock sharedLock;
    private final ReentrantLock ownLock;

    public RockSensorRunnable(ReentrantLock ownLock, ReentrantLock sharedLock, String name) {
        this.ownLock = ownLock;
        this.sharedLock = sharedLock;
    }

    public RockSensorRunnable(ReentrantLock ownLock, ReentrantLock sharedLock) {
        this(ownLock, sharedLock, Thread.currentThread().getName());
    }


    @Override
    public void run() {
        if (ownLock.tryLock()) {
            if (sharedLock.isHeldByCurrentThread()) {
                sharedLock.unlock();
            }
            ownLock.unlock();
        } else {
            try {
                if (!sharedLock.isHeldByCurrentThread()) {
                    sharedLock.lockInterruptibly();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}
