package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.hermes.common.Task;
import com.hermes.component.TaskHolderComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class TaskSystem extends IteratingSystem {
    public static final Family FAMILY = Family.all(
            TaskHolderComponent.class
    ).get();
    public TaskSystem() {
        super(FAMILY);
    }

    private static final Logger log = new Logger(TaskSystem.class.getName(), Logger.DEBUG);

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
//        log.debug("task running");
        TaskHolderComponent task = ComponentRetriever.get(entity, TaskHolderComponent.class);
        if (!task.isDone()) {
            task.update(deltaTime);
        } else {
            task.cleanUp();
            getEngine().removeEntity(entity);
        }

    }

    public void addTaskToProcess(Task task) {
        Entity entity = getEngine().createEntity();
        TaskHolderComponent taskHolder = getEngine().createComponent(TaskHolderComponent.class);
        taskHolder.task = task;
        entity.add(taskHolder);
        getEngine().addEntity(entity);
    }
}
