package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	static Consts consts = new Consts();
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("2048");
		config.setWindowSizeLimits(
				consts.STATIC_WIDTH,
				consts.STATIC_HEIGHT,
				consts.STATIC_WIDTH * 10,
				consts.STATIC_HEIGHT * 10
		);
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}
