package com.hermes.interactables.others;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.BundleKeys;
import com.hermes.assets.gui.GUIScene;
import com.hermes.interactables.MultiInteractable;
import com.hermes.screens.game.FirstLevelScreenV2;
import com.hermes.screens.game.LevelScreen;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class ResetEntityInteractable extends MultiInteractable {

    private final Entity entityToReset;
    private final Vector2 initialPosition;
    private final String entityToResetName = "theThing";

    public ResetEntityInteractable(LevelScreen screenV2, Entity entity, Entity entityToReset) {
        super(screenV2, entity);
        this.entityToReset = entityToReset;
        initialPosition = new Vector2();
        init();
    }

    @Override
    protected void init() {

        if (entityToReset == null || initialPosition == null) return;
        TransformComponent transformComponent = ComponentRetriever.get(entityToReset, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entityToReset, DimensionsComponent.class);

        initialPosition.set(transformComponent.x + dimensionsComponent.width / 2, transformComponent.y + dimensionsComponent.height/2);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GUIScene.INSTANCE.setDialogueLabel(BundleKeys.RESET,styleDialogue, entityToResetName);
                shouldContinue = true;
                addInteraction(
                        () -> {
                            PhysicsBodyComponent bodyComponent = ComponentRetriever.get(entityToReset, PhysicsBodyComponent.class);
                            bodyComponent.body.setTransform(initialPosition, 0);
                            shouldContinue = true;
                            bodyComponent.body.setAwake(true);
                            addInteraction(this);
                        }
                );
            }
        };
        addInteraction(runnable);
    }
}
