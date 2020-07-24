package com.dune.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dune.game.DuneGame;
import com.dune.game.screens.ScreenManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = ScreenManager.WORLD_WIDTH;
		config.height = ScreenManager.WORLD_HEIGHT;
//		config.foregroundFPS = 1;
		new LwjglApplication(new DuneGame(), config);
	}
}
