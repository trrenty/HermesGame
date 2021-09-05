package com.hermes.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import games.rednblack.editor.renderer.resources.AsyncResourceManager;
import games.rednblack.editor.renderer.resources.ResourceManager;

public class AssetDescriptors {
//    public  final static AssetDescriptor<TextureAtlas> GAMEPLAY_ATLAS = new AssetDescriptor<TextureAtlas>();
    public static final AssetDescriptor<BitmapFont> UI_FONT = new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT, BitmapFont.class);
    public static final AssetDescriptor<AsyncResourceManager> RESOURCE_MANAGER = new AssetDescriptor<AsyncResourceManager>(AssetPaths.RESOURCE_MANAGER, AsyncResourceManager.class);
    public static final AssetDescriptor<I18NBundle> I18N_BUNDLE = new AssetDescriptor<I18NBundle>(AssetPaths.I18N_BUNDLE, I18NBundle.class);
    public static final AssetDescriptor<Skin> GUI_SKIN = new AssetDescriptor<Skin>(AssetPaths.GUI_SKIN, Skin.class);
    private AssetDescriptors () {}
}
