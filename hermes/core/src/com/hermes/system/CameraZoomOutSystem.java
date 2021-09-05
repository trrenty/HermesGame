package com.hermes.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.CameraZoomOutComponent;
import com.hermes.component.PlayerComponent;
import com.hermes.config.GameConfig;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class CameraZoomOutSystem extends EntitySystem {

    
    private static final Logger log = new Logger(CameraZoomOutSystem.class.getName(), Logger.DEBUG);
    
    private final Rectangle areaRectangle = new Rectangle();
    private Entity player;

    private static final Family FAMILY = Family.all(
            CameraZoomOutComponent.class
    ).get();

    private static final Family PLAYER_FAMILY = Family.all(
            PlayerComponent.class
    ).get();
    private final OrthographicCamera camera;
    private ImmutableArray<Entity> entities;
    private boolean insideRect;
    private float timer;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(FAMILY);
    }

    public CameraZoomOutSystem(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {

        insideRect = false;
        for (int i = 0; i < entities.size(); ++i) {
            processEntity(entities.get(i), deltaTime);
            if (insideRect) {
                break;
            };
        }

        if (insideRect && camera.zoom < GameConfig.MAX_ZOOM_OUT) {
            camera.zoom += deltaTime;
            if (camera.zoom > GameConfig.MAX_ZOOM_OUT) {
                camera.zoom = GameConfig.MAX_ZOOM_OUT;
            }
            camera.update();

        } else if (!insideRect && camera.zoom > 1) {
            camera.zoom -= deltaTime;
            if (camera.zoom < 1) {
                camera.zoom = 1;
            }
            camera.update();
        }
    }

    protected void processEntity(Entity entity, float deltaTime) {
        if (player == null || ComponentRetriever.get(player, PhysicsBodyComponent.class) == null) {
            initPlayer();
        }

        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);

        areaRectangle.set(transformComponent.x, transformComponent.y, dimensionsComponent.width * transformComponent.scaleX, dimensionsComponent.height * transformComponent.scaleY);

        PhysicsBodyComponent bodyComponent = ComponentRetriever.get(player, PhysicsBodyComponent.class);

        if (bodyComponent == null || bodyComponent.body == null) {
            return;
        }
        boolean contains = areaRectangle.contains(bodyComponent.body.getPosition());

        if (contains) {
            insideRect = true;
        }
    }

    private void initPlayer() {
        ImmutableArray<Entity> players = getEngine().getEntitiesFor(PLAYER_FAMILY);
        if (players.size() <= 0) {
            return;
        }
        player = players.first();
    }
}
