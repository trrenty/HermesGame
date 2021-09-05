package com.hermes;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.hermes.config.GameConfig;
import com.hermes.screens.loading.LoadingScreen;

public class Hermes extends Game {
	private AssetManager assetManager;
	private SpriteBatch batch;
	private World world;
	private PooledEngine engine;
	private AtlasTmxMapLoader mapLoader;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		assetManager = new AssetManager();

		assetManager.getLogger().setLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		world = new World(new Vector2(0, -GameConfig.GRAVITY), true);
		engine = new PooledEngine();
		mapLoader = new AtlasTmxMapLoader();

		setScreen(new LoadingScreen(null));

	}

	@Override
	public void dispose() {
		assetManager.dispose();
		batch.dispose();
		world.dispose();

	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public World getWorld() {
		return world;
	}

	public PooledEngine getEngine() {
		return engine;
	}

	public AtlasTmxMapLoader getMapLoader() {
		return mapLoader;
	}
}
