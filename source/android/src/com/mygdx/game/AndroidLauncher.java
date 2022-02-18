package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class AndroidLauncher extends AndroidApplication {
	final static Consts consts = new Consts();
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useCompass = false;
		config.useAccelerometer = false;
		config.useGyroscope = false;
		config.useImmersiveMode = false;
		config.useWakelock = false;
		config.useRotationVectorSensor = false;
		initialize(new MyGdxGame(consts.PLATFORM_ANDROID), config);
	}
}
