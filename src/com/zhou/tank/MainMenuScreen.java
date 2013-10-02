package com.zhou.tank;

import android.graphics.Color;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.hx.cannonwar.TankGame;

public class MainMenuScreen extends Screen {

	private int frozeTime = 0;

	public MainMenuScreen(Game game) {
		super(game);
		type = "mainmenu";
	}
	
	@Override
	public void update(float deltaTime) {
		if(frozeTime>0){
			frozeTime--;
			return;
		}
		if(game.getInput().isTouchDown(0)){
				int x = game.getInput().getTouchX(0);
				int y = game.getInput().getTouchY(0);
				// 登录按钮
				if(x>650 && y<80){
					Log.d("tank", "login click");
					if(!game.isLoggedIn){
						game.doSdkLogin(true, false, Game.APPKEY);
					}else{
						game.doSdkLogout(game.mQihooUserInfo);
					}
				}else if(x>700 && y>400){
					// 好友列表--排行榜
					if(!game.isLoggedIn){
						game.loginNextIntent = "showList";
						game.doSdkLogin(true, false, Game.APPKEY);
					}else{
						game.showFriendsRank();
					}
				}else if(x>180 && x<620 && y>300 && y<450){
					// 开始游戏
					if(!game.isLoggedIn){
						game.loginNextIntent = "startGame";
						game.doSdkLogin(true, false, Game.APPKEY);
					}else{
						Screen gameScreen = ((TankGame)game).getGameScreen();
						((GameScreen)gameScreen).reset();
						game.setScreen(gameScreen);
					}
				}
				frozeTime = 30;
				/*if(event.x > 0 && event.x < 256 && event.y > 416 && event.y < 480){
					Setting.soundEnabled = !Setting.soundEnabled;
					return;
				}*/
		}
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.clear(0);
		g.drawPixmap(Assets.menuBackground,0,0);
		g.drawText("" + game.getHighestScore(), 440, 440, Color.RED, 32);
		if(game.isLoggedIn){
			g.drawPixmap(Assets.logout360, 650, 25);
		}else{
			g.drawPixmap(Assets.login360, 650, 25);
		}
		//Log.d("tank", "score:"+game.getHighestScore());
		//draw sound on/off
		/*if(Setting.soundEnabled){
			g.drawPixmap(Assets.items,  0 , 13 * 32, 32 * 8, 0 , 32 * 8, 32 *2);
		}else{
			g.drawPixmap(Assets.items,  0 , 13 * 32, 32 * 16, 0 , 32 * 8, 32 *2);
		}*/
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