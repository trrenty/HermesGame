package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.PlayerComponent;
import com.hermes.config.GameConfig;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.ViewPortComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

import java.util.ArrayList;

public class CameraFollowerSystemV2 extends IteratingSystem {

    private static final Logger log = new Logger(CameraFollowerSystemV2.class.getName(), Logger.DEBUG);

    private float maxCameraViewX = Integer.MAX_VALUE;
    private float maxCameraViewY = Integer.MAX_VALUE;

    private float minCameraViewX = 0;
    private float minCameraViewY = 0;

    private float viewportCenterX;
    private float viewportCenterY;


    private Entity focus;
    private ArrayList<Float> verticalGuides;
    private ArrayList<Float> horizontalGuides;

    public CameraFollowerSystemV2() {
        super(Family.all(ViewPortComponent.class).get());

    }

    public CameraFollowerSystemV2(float maxCameraViewX, float maxCameraViewY) {
        super(Family.all(ViewPortComponent.class).get());
        this.maxCameraViewX = maxCameraViewX;
        this.maxCameraViewY = maxCameraViewY;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ViewPortComponent viewPortComponent = ComponentRetriever.get(entity, ViewPortComponent.class);
        OrthographicCamera camera = (OrthographicCamera) viewPortComponent.viewPort.getCamera();


        if (focus != null && ComponentRetriever.get(focus, PhysicsBodyComponent.class) != null ) {

            TransformComponent transform = ComponentRetriever.get(focus, TransformComponent.class);
            DimensionsComponent size = ComponentRetriever.get(focus, DimensionsComponent.class);

            if (transform == null || size == null) return;

            float playerCenterX = transform.x + size.width / 2;
            float playerCenterY = transform.y + size.height / 2 + GameConfig.CAMERA_FOLLOW_OFFSET_HEIGHT;

            if (
                    playerCenterX < minCameraViewX ||
                    playerCenterX > maxCameraViewX ||
                    playerCenterY - GameConfig.CAMERA_FOLLOW_OFFSET_HEIGHT < minCameraViewY ||
                    playerCenterY - GameConfig.CAMERA_FOLLOW_OFFSET_HEIGHT > maxCameraViewY
            ) {
                setDimensions(playerCenterX, playerCenterY);
            }

            float cameraX = camera.position.x;
            float cameraY = camera.position.y ;

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                playerCenterY -= 4 * GameConfig.CAMERA_FOLLOW_OFFSET_HEIGHT;
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                playerCenterY += 2 *GameConfig.CAMERA_FOLLOW_OFFSET_HEIGHT;
            }

            float toX = MathUtils.lerp(cameraX, playerCenterX, 0.1f * deltaTime * 60);
            PhysicsBodyComponent pc = ComponentRetriever.get(focus, PhysicsBodyComponent.class);

            float toY = MathUtils.lerp(cameraY, playerCenterY, 0.05f * deltaTime * 60);
            if (pc!= null && pc.body.getLinearVelocity().y < 0) {
                toY = MathUtils.lerp(cameraY, playerCenterY, 0.15f * deltaTime * 60);
            }



            toX = MathUtils.clamp(
                    toX,
                    minCameraViewX + viewportCenterX * camera.zoom,
                    maxCameraViewX - viewportCenterX * camera.zoom);
            toY = MathUtils.clamp(
                    toY,
                    minCameraViewY + viewportCenterY * camera.zoom,
                    maxCameraViewY - viewportCenterY * camera.zoom);


            camera.position.set(toX, toY, 0);

            camera.update();

        } else {
            log.debug("focus null");
            ImmutableArray<Entity> array = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
            if (array.size() <= 0) {
                return;
            }
            focus = array.peek();
        }
    }

    public void setFocus(Entity entity) {
        focus = entity;
        log.debug("setting focus");
    }

    public void setDimensions(float playerX, float playerY) {
        float min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
        for (Float verticalGuide : verticalGuides) {
            if (verticalGuide > playerX && verticalGuide < max) {
                max = verticalGuide;
            } else if (verticalGuide < playerX && verticalGuide > min) {
                min = verticalGuide;
            }
        }
        maxCameraViewX = max;
        minCameraViewX = min;
        min = Integer.MIN_VALUE; max = Integer.MAX_VALUE;
        for (Float horizontalGuide : horizontalGuides) {
            if (horizontalGuide > playerY && horizontalGuide < max) {
                max = horizontalGuide;
            } else if (horizontalGuide < playerY && horizontalGuide > min) {
                min = horizontalGuide;
            }
        }
        maxCameraViewY = max;
        minCameraViewY = min;

        log.debug(minCameraViewX + "-" + maxCameraViewX + " | " + minCameraViewY + "-" + maxCameraViewY);
    }

    public void setViewportCenter(float viewportCenterX, float viewportCenterY) {
        this.viewportCenterX = viewportCenterX;
        this.viewportCenterY = viewportCenterY;
    }


    public Entity getFocus() {
        return focus;
    }

    public void setGuides(ArrayList<Float> horizontalGuides, ArrayList<Float> verticalGuides) {
        this.horizontalGuides = horizontalGuides;
        this.verticalGuides = verticalGuides;
        setDimensions(0, 0);
    }
}