package com.hermes.system.debug;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.hermes.util.debug.DebugCameraController;

public class DebugCameraSystem extends EntitySystem {

    private static final DebugCameraController DEBUG_CAMERA_CONTROLLER = new DebugCameraController();

    private final OrthographicCamera camera;

    public DebugCameraSystem(OrthographicCamera camera, float startX, float startY) {
        this.camera = camera;
        DEBUG_CAMERA_CONTROLLER.setStartPosition(startX, startY);


    }

    @Override
    public void update(float deltaTime) {
        DEBUG_CAMERA_CONTROLLER.handleDebugInput(deltaTime);
        DEBUG_CAMERA_CONTROLLER.applyTo(camera);
    }
}
