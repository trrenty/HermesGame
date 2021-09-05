package com.hermes.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Logger;
import com.hermes.component.ParallaxComponent;
import com.hermes.config.GameConfig;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class ParallaxSystem extends IteratingSystem {
    
    private static final Logger log = new Logger(ParallaxSystem.class.getName(), Logger.DEBUG);

    private final OrthographicCamera camera;

    private static final Family FAMILY = Family.all(
            ParallaxComponent.class,
            TransformComponent.class,
            DimensionsComponent.class
    ).get();

    public ParallaxSystem(OrthographicCamera camera) {
        super(FAMILY);
        this.camera = camera;
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dimensionComponent = ComponentRetriever.get(entity, DimensionsComponent.class);

        ParallaxComponent parallaxComponent = ComponentRetriever.get(entity, ParallaxComponent.class);

//        log.debug(transformComponent.x + " " + transformComponent.y);

        if (parallaxComponent.shouldScale) {
            transformComponent.scaleX = transformComponent.scaleY = camera.zoom;
            transformComponent.x = parallaxComponent.initialPosition.x + (camera.position.x - GameConfig.VIEWPORT_CENTER_X * camera.zoom) * parallaxComponent.parallaxCoefficientX;
            transformComponent.y = parallaxComponent.initialPosition.y + (camera.position.y - GameConfig.VIEWPORT_CENTER_Y * camera.zoom ) * parallaxComponent.parallaxCoefficientY;
        }
        else {
            transformComponent.x = parallaxComponent.initialPosition.x + (camera.position.x - GameConfig.VIEWPORT_CENTER_X ) * parallaxComponent.parallaxCoefficientX;
            transformComponent.y = parallaxComponent.initialPosition.y + (camera.position.y - GameConfig.VIEWPORT_CENTER_Y ) * parallaxComponent.parallaxCoefficientY;
        }

        if (parallaxComponent.shouldRepeat) {
            if (transformComponent.x < camera.position.x - dimensionComponent.width * transformComponent.scaleX / 2 - GameConfig.VIEWPORT_CENTER_X) {
                parallaxComponent.initialPosition.x += dimensionComponent.width * transformComponent.scaleX / 2;
            } else if (transformComponent.x > camera.position.x - GameConfig.VIEWPORT_CENTER_X) {
                parallaxComponent.initialPosition.x -= dimensionComponent.width * transformComponent.scaleX / 2;
            }
        }
    }
}
