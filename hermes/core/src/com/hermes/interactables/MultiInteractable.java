package com.hermes.interactables;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.GUIScene;
import com.hermes.screens.game.LevelScreen;
import com.hermes.system.CameraFollowerSystemV2;
import com.hermes.thread.ThreadManager;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public abstract class MultiInteractable extends BaseInteractable {
    protected final CameraFollowerSystemV2 cameraFollower;
    protected final ItemWrapper root;
    protected final PooledEngine engine;
    private static final Logger log = new Logger(MultiInteractable.class.getName(), Logger.DEBUG);
    protected final InputProcessor inputProcessor;
    public static final InputProcessor EMPTY_INPUT_PROCESSOR = new InputAdapter();
    protected boolean shouldContinue;
    protected final Array<Runnable> listOfInteractions = new Array<>();

    protected String entityName = null;


    // THREAD STUFF
    protected ThreadManager threadManager;

    public void setThreadManager(ThreadManager th) {
        threadManager = th;
    }

    // THREAD STUFF

    public MultiInteractable(LevelScreen screenV2, Entity entity) {
        super(entity);
        this.cameraFollower = screenV2.getCameraFollowerSystemV2();
        this.root = new ItemWrapper(screenV2.getSceneLoader().getRoot());
        this.engine = screenV2.getSceneLoader().getEngine();
        this.inputProcessor = Gdx.input.getInputProcessor();
        init();

    }

    protected abstract void init();

    @Override
    public void endContact() {
        super.endContact();
        enableInput();
    }

    @Override
    public void interact() {
        if (listOfInteractions.isEmpty()) {
            GUIScene.INSTANCE.setDialogueText("...");
        } else {
            Runnable interaction = listOfInteractions.first();
            interaction.run();
            if (shouldContinue) listOfInteractions.removeIndex(0);
            shouldContinue = false;
        }
    }

    public void addInteraction(Runnable runnable) {
        listOfInteractions.add(runnable);
    }

    public void addInteractions(Array<Runnable> runnables) {
        listOfInteractions.addAll(runnables);
    }

    public void addDialogueInteraction(String key) {
        listOfInteractions.add(() -> {setDialogueLabel(key); shouldContinue = true;});
    }

    protected void setDialogueLabel(String key) {
        if (entityName != null) {
            GUIScene.INSTANCE.setDialogueTextAndImage(key, entityName);
        } else {
            GUIScene.INSTANCE.setDialogueLabel(key);
        }
    }

protected void addAllDialoguesWithStartingAction(boolean shouldLoop, Runnable runnable) {
        Array<Runnable> copy = new Array<>();
        for (int j = 0; j < GUIScene.INSTANCE.getNrOfLabelsWithPrefix(dialogueKey); j++) {
            int finalJ = j;
            copy.add(() -> { runnable.run();GUIScene.INSTANCE.setDialogueLabel(dialogueKey+ finalJ, styleDialogue);shouldContinue = true;});
//            log.debug(j + "");
        }
        if (shouldLoop) {
            copy.add(() -> {addInteractions(copy); shouldContinue = true;});
        }
        addInteractions(copy);
    }

    protected void addAllDialogues(boolean shouldLoop) {
        addAllDialoguesWithStartingAction(shouldLoop, () -> {});
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    protected void disableInput() {
        Gdx.input.setInputProcessor(EMPTY_INPUT_PROCESSOR);
    }

    protected void enableInput() {
        Gdx.input.setInputProcessor(inputProcessor);
    }

    public void facePlayer() {
        Entity player = root.getChild(ChildrenNames.PLAYER_ID).getEntity();
        if (player != null) {
            TransformComponent playerTransform = ComponentRetriever.get(player, TransformComponent.class);
            TransformComponent entityTransform = ComponentRetriever.get(entity, TransformComponent.class);

            if (entityTransform.x < playerTransform.x && entityTransform.scaleX > 0) {
                entityTransform.scaleX = -entityTransform.scaleX;
            } else if (entityTransform.x > playerTransform.x && entityTransform.scaleX < 0) {
                entityTransform.scaleX = -entityTransform.scaleX;
            }
        }
    }

}
