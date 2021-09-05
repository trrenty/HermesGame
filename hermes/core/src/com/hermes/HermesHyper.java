package com.hermes;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.hermes.assets.AssetDescriptors;
import com.hermes.assets.AssetPaths;
import com.hermes.component.*;
import com.hermes.screens.game.FirstLevelScreenV2;
import com.hermes.screens.game.LevelScreen;
import com.hermes.screens.game.SecondLevelScreen;
import games.rednblack.editor.renderer.SceneLoader;
import games.rednblack.editor.renderer.factory.EntityFactory;
import games.rednblack.editor.renderer.resources.AsyncResourceManager;
import games.rednblack.editor.renderer.resources.ResourceManager;
import games.rednblack.editor.renderer.resources.ResourceManagerLoader;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.h2d.extension.talos.TalosItemType;
import games.rednblack.h2d.extention.spine.SpineItemType;

public class HermesHyper extends Game {
	private AssetManager assetManager;
	private Batch batch;
	private World world;
	private PooledEngine engine;
	private SceneLoader sceneLoader;
	private EntityFactory factory;

	@Override
	public void create() {
		Box2D.init();

		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		assetManager = new AssetManager();
		assetManager.getLogger().setLevel(Application.LOG_DEBUG);

		assetManager.setLoader(AsyncResourceManager.class, new ResourceManagerLoader(assetManager.getFileHandleResolver()));

//		assetManager.load(AssetPaths.RESOURCE_MANAGER, AsyncResourceManager.class, param);
		assetManager.load(AssetDescriptors.RESOURCE_MANAGER);
		assetManager.load(AssetDescriptors.I18N_BUNDLE);
		initSkin();
		assetManager.finishLoading();

		ResourceManager resourceManager = assetManager.get(AssetDescriptors.RESOURCE_MANAGER);


		sceneLoader = new SceneLoader(resourceManager);

		sceneLoader.injectExternalItemType(new SpineItemType());
		sceneLoader.injectExternalItemType(new TalosItemType());



		batch = sceneLoader.getBatch();
		world = sceneLoader.getWorld();
		engine = sceneLoader.getEngine();
		factory = sceneLoader.getEntityFactory();

		ComponentRetriever.addMapper(StateComponent.class);
		ComponentRetriever.addMapper(ParallaxComponent.class);
		ComponentRetriever.addMapper(HealthComponent.class);
		ComponentRetriever.addMapper(DamageComponent.class);
		ComponentRetriever.addMapper(InteractableComponent.class);
		ComponentRetriever.addMapper(DestinationComponent.class);
		ComponentRetriever.addMapper(LootComponent.class);
		ComponentRetriever.addMapper(PlayerComponent.class);
		ComponentRetriever.addMapper(CameraZoomOutComponent.class);
		ComponentRetriever.addMapper(CheckpointComponent.class);
		ComponentRetriever.addMapper(NpcComponent.class);
		ComponentRetriever.addMapper(LicuriciComponent.class);
		ComponentRetriever.addMapper(ThreadComponent.class);
		ComponentRetriever.addMapper(ActionOnRemoveComponent.class);
		ComponentRetriever.addMapper(TaskHolderComponent.class);
		ComponentRetriever.addMapper(EnemyComponent.class);


		setScreen(new FirstLevelScreenV2(this));


	}

	private void initSkin() {

		Colors.put("GOLD", Color.ORANGE);
		Colors.put("RED", Color.RED);
		Colors.put("DARK_RED", Color.FIREBRICK);
		Colors.put("GREEN", Color.valueOf("34f7d0"));
		Colors.put("KW", Color.valueOf("cc7832"));
		Colors.put("FN", Color.valueOf("ffc66d"));
		Colors.put("VAR", Color.valueOf("9876aa"));
		Colors.put("NR", Color.valueOf("a9b7c6"));
		Colors.put("WH", Color.WHITE);



		final ObjectMap<String, Object> resources = new ObjectMap<>();
		generateFonts("ui_font_",AssetPaths.UI_FONT_TTF, resources);
		generateFonts("code_font_", AssetPaths.CODE_FONT, resources);
		final SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter(AssetPaths.GUI_SKIN_ATLAS, resources);
		assetManager.load(AssetPaths.GUI_SKIN, Skin.class, skinParameter);


	}

	private void generateFonts(String name, String assetName, ObjectMap<String, Object> resources) {
		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(assetName));
		final FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.minFilter = Texture.TextureFilter.Linear;
		fontParameter.magFilter = Texture.TextureFilter.Linear;
		fontParameter.characters+= "ăîâșț";
		final int[] sizesToCreate = { 16, 20, 26, 32 , 60};
		for (int size : sizesToCreate) {
			fontParameter.size = size;
			final BitmapFont bitmapFont = fontGenerator.generateFont(fontParameter);
			bitmapFont.getData().markupEnabled = true;
			resources.put(name + size, bitmapFont);
		}
		fontGenerator.dispose();
	}

	@Override
	public void dispose() {
		sceneLoader.dispose();
		assetManager.dispose();
		LevelScreen.threadManager.end();

//		ThreadManager.INSTANCE.end();

	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public Batch getBatch() {
		return batch;
	}

	public World getWorld() {
		return world;
	}

	public PooledEngine getEngine() {
		return engine;
	}

	public SceneLoader getSceneLoader() {
		return sceneLoader;
	}

}
