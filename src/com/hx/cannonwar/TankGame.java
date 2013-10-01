package com.hx.cannonwar;

import android.view.KeyEvent;
import android.view.View;

import com.badlogic.androidgames.framework.Game;
import com.zhou.tank.GameScreen;
import com.zhou.tank.LoadingScreen;

public class TankGame extends Game {
	
	private LoadingScreen loadScreen;
	private GameScreen gameScreen;
	
	@Override
	public LoadingScreen getStartScreen() {
		if(loadScreen==null){
			loadScreen = new LoadingScreen(this);
		}
		return loadScreen;
	}
	public GameScreen getGameScreen() {
		if(gameScreen==null){
			gameScreen = new GameScreen(this);
		}
		
		return gameScreen;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(screen.type=="mainmenu"){
				System.exit(0);  
			}else if(screen.type == "game"){
				setScreen(getStartScreen());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//Assets.unload();
	}


	@Override
	public void onClick(View arg0) {
		
	}
	
}