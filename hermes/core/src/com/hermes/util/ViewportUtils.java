package com.hermes.util;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hermes.config.GameConfig;

public class ViewportUtils {
    private ViewportUtils() {
    }

    private static final Logger log = new Logger(ViewportUtils.class.getName(), Logger.DEBUG);

    public static final int DEFAULT_CELL_SIZE = 1;

    public static void drawGrid(Viewport viewport, ShapeRenderer renderer) {
        drawGrid(viewport, renderer, DEFAULT_CELL_SIZE);
    }

    public static void drawGrid(Viewport viewport, ShapeRenderer renderer, int cellSize) {


        //VALIDATE
        if (viewport == null) {
            throw new IllegalArgumentException("Viewport paras in required!");
        }
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer param is required!");
        }
        if (cellSize < DEFAULT_CELL_SIZE) {
            cellSize = DEFAULT_CELL_SIZE;
        }

        // copy old color from renderer

        Color oldColor = new Color(renderer.getColor());

        int worldWidth = (int) GameConfig.WORLD_WIDTH;
        int worldHeight = (int) GameConfig.WORLD_HEIGHT;

        int doubleWorldWidth = worldWidth * 2;
        int doubleWorldHeight = worldHeight * 2;

        renderer.setProjectionMatrix(viewport.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE);



        //draw vertical lines
        for (int x = -doubleWorldWidth; x <doubleWorldWidth; x+=cellSize) {
            renderer.line(x, -doubleWorldHeight, x, doubleWorldHeight);
        }
        for (int x = -doubleWorldHeight; x<doubleWorldHeight; x+=cellSize) {
            renderer.line(-doubleWorldWidth, x, doubleWorldWidth, x);
        }

        //draw axis x and y
        renderer.setColor(Color.RED);
        renderer.line(-doubleWorldWidth, 0, doubleWorldWidth, 0);
        renderer.line(0, -doubleWorldHeight, 0, doubleWorldHeight);


        // draw camera grid

        renderer.rect(
                viewport.getCamera().position.x - GameConfig.VIEWPORT_CENTER_X,
                viewport.getCamera().position.y - GameConfig.VIEWPORT_CENTER_Y,
                GameConfig.VIEWPORT_WIDTH,
                GameConfig.VIEWPORT_HEIGHT
        );

        // draw world bounds
        renderer.setColor(Color.GREEN);
        renderer.line(0, worldHeight, worldWidth, worldHeight);
        renderer.line(worldWidth, 0, worldWidth, worldHeight);

        // draw camera offset

        renderer.setColor(Color.BLUE);
        renderer.rect(
                viewport.getCamera().position.x - GameConfig.CAMERA_FOLLOW_OFFSET_WIDTH,
                viewport.getCamera().position.y - GameConfig.CAMERA_FOLLOW_OFFSET_HEIGHT,
                GameConfig.CAMERA_FOLLOW_OFFSET_WIDTH * 2,
                GameConfig.CAMERA_FOLLOW_OFFSET_HEIGHT * 2
        );

        renderer.end();
        renderer.setColor(oldColor);
    }

    public static void debugPixelPerUnit(Viewport viewport) {
        if (viewport == null) {
            throw new IllegalArgumentException("viewport param is required!");
        }

        float screenWidth = viewport.getScreenWidth();
        float screenHeight = viewport.getScreenHeight();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // PPU pixel per unit

        float xPPU = screenWidth/worldWidth;
        float yPPU = screenHeight/worldHeight;

        log.debug("x PPU= " + xPPU + " y PPU= " + yPPU);
    }
}
