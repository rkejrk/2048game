package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	static Consts consts = new Consts();
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("2048");
		config.setWindowSizeLimits(
				consts.static_width,
				consts.static_height,
				consts.static_width * 2,
				consts.static_height *2
		);
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}
