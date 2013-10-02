package com.zhou.tank;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;

public class LoadingScreen extends Screen {
	
	public LoadingScreen(Game game) {
		super(game);
		type = "loading";
	}

	@Override
	public void update(float deltaTime) {
		Assets.load(game);
	}

	@Override
	public void present(float deltaTime) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}
}