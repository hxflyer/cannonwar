package com.hx.cannonwar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;
import com.zhou.tank.Assets;
import com.zhou.tank.GameScreen;
import com.zhou.tank.LoadingScreen;

public class TankGame extends Game{
	
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
		super.onStop();
		Assets.unload();
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}