package com.hermes.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.hermes.HermesHyper;
import com.hermes.config.GameConfig;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode((int)GameConfig.WIDTH, (int)GameConfig.HEIGHT);

		new Lwjgl3Application(new HermesHyper(), config);
	}
}