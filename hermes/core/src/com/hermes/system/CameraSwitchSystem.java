package com.hermes.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Logger;
import com.hermes.system.debug.DebugCameraSystem;

public class CameraSwitchSystem extends EntitySystem {

    private static final Logger log = new Logger(CameraSwitchSystem.class.getName(), Logger.DEBUG);
    private final OrthographicCamera camera;
    private CameraFollowerSystemV2 followerCamera;
    private DebugCameraSystem debugCamera;

    public CameraSwitchSystem(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        followerCamera = engine.getSystem(CameraFollowerSystemV2.class);
        debugCamera = engine.getSystem(DebugCameraSystem.class);
        if (followerCamera == null || debugCamera == null) {
            log.error("followerCamera or debugCamera is null, add followerC and DebugC to engine before CameraSwitchSystem");
            log.info("no available CameraSwitchSystem");
            getEngine().removeSystem(this);
            return;
        }
        checkCameras();
    }

    @Override
    public void update(float deltaTime) {
//        log.debug("smth");
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            log.debug("C pressed");
            checkCameras();
        }
    }

    private void checkCameras()
    {
        if (followerCamera.checkProcessing()) {
            followerCamera.setProcessing(false);
            debugCamera.setProcessing(true);
        } else {
            log.debug("Camera follower not null");
            followerCamera.setProcessing(true);
            debugCamera.setProcessing(false);
//                 getEngine().addSystem(new DebugCameraSystem(camera, camera.position.x, camera.position.y));
        }
    }
}

