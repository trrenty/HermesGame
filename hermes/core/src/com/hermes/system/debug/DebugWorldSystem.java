package com.hermes.system.debug;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DebugWorldSystem extends EntitySystem {
    private static final Logger log = new Logger(DebugWorldSystem.class.getName(), Logger.DEBUG);
    private final Box2DDebugRenderer renderer;
    private final World world;
    private final Viewport viewport;

    public DebugWorldSystem(World world, Viewport viewport, Box2DDebugRenderer renderer) {
        this.world = world;
        this.viewport = viewport;
        this.renderer = renderer;
    }

    @Override
    public void update(float deltaTime) {
        viewport.apply();
        renderer.render(world, viewport.getCamera().combined);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        renderer.dispose();
        log.debug("renderer disposed!");
    }
}
