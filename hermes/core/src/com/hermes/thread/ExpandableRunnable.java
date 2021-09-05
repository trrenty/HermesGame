package com.hermes.thread;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

import java.util.concurrent.Phaser;

public class ExpandableRunnable implements Runnable {

    private final Phaser phaser;
    private final Array<Runnable> runnables = new Array<>();
    private static final Logger log = new Logger(RockSensorRunnable.class.getName(), Logger.DEBUG);


    public ExpandableRunnable(Phaser phaser) {
        this.phaser = phaser;
    }

    public ExpandableRunnable(Phaser phaser, Runnable runnable) {
        this(phaser);
        runnables.add(runnable);
    }

    @Override
    public void run() {
        while (!phaser.isTerminated()) {
            phaser.awaitAdvance(phaser.getPhase());
            for (Runnable runnable : new Array.ArrayIterator<>(runnables)) {
                runnable.run();
            }
        }
    }

    public void addTask(Runnable runnable) {
        runnables.add(runnable);
    }
}
