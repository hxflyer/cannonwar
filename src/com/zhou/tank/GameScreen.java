package com.zhou.tank;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.zhou.tank.World.WorldListener;

public class GameScreen extends Screen {

	public WorldListener worldListener;
	public World world;
	public int tickTime = 0;//player has hat time
	public int[] tickTimes = new int[6];//the explode 's time
	public int huTime = 0;//hu the flow's time
	public static Rect WorldRect = new Rect(200,0,690,480);
	public static Point WorldCenter = new Point(450,240);
	public GameScreen(Game game) {
		super(game);
		type = "game";
		final FileIO fileIo = game.getFileIO();
		worldListener = new WorldListener() {

			@Override
			public int[][] loadMap(int level) {
				return MapFactory.createMap(fileIo, level);
			}
		};
		world = new World(worldListener, game);
		reset();
	}
	
	public void reset(){
		if(world!=null){
			world.restart();
		}
		tickTime = 0;
		huTime = 0;
		world.isPaused = false;
		for(int i =0;i<tickTimes.length;i++){
			tickTimes[i] = 0;
		}
	}
	private int frozeTime = 0;
	@Override
	public void update(float deltaTime) {
		if(frozeTime>0){
			frozeTime--;
			return;
		}
		if (!world.isGameOver) {
			if (game.getInput().isTouchDown(0)) {
				int x = game.getInput().getTouchX(0);
				int y = game.getInput().getTouchY(0);
				if(x > WorldRect.right && y<100) {
					world.isPaused = !world.isPaused;
					frozeTime = 10;
				}
			}
			if (game.getInput().isTouchDown(1)) {
				int x = game.getInput().getTouchX(1);
				int y = game.getInput().getTouchY(1);
				if(x > WorldRect.right && y<100) {
					world.isPaused = !world.isPaused;
					frozeTime = 10;
				}
			}
		}
		
		if(world.isPaused){
			return;
		}
		world.update(deltaTime);
		
		
		// if gameover ,the tank cannot move;
		if(world.isShowSuccessLabel){
			if (world.tickTimes[6]>1.5 && game.getInput().isTouchDown(0)) {
				int x = game.getInput().getTouchX(0);
				int y = game.getInput().getTouchY(0);
				if(x>250 && x<550 && y>300 && y<400){
					world.nextLevel();
				}
			}
		}else if(world.isShowFailLabel){
			if (world.tickTimes[6]>1.5 && game.getInput().isTouchDown(0)) {
				int x = game.getInput().getTouchX(0);
				int y = game.getInput().getTouchY(0);
				if(x>250 && x<550){

					if(y>150){
						if(y<240){
							world.restart();
							Log.d("tank","btn replay");
						}else if(y<320){
							Log.d("tank","btn share");
							if(game.isLoggedIn){
								game.showShareScorePopup();
							}else{
								game.loginNextIntent = "shareScore";
								game.doSdkLogin(true, false, Game.APPKEY);
							}
							frozeTime = 30;
						}else if(y<380){
							Log.d("tank","btn invite");
							if(game.isLoggedIn){
								game.showInviteFriendPopup();
							}else{
								game.loginNextIntent = "inviteFriend";
								game.doSdkLogin(true, false, Game.APPKEY);
							}
							frozeTime = 30;
						}else if(y<440){
							Log.d("tank","btn check list");
							if(game.isLoggedIn){
								game.showFriendsRank();
							}else{
								game.loginNextIntent = "showList";
								game.doSdkLogin(true, false, Game.APPKEY);
							}
							frozeTime = 30;
						}
					}
				}
				//world.restart();
			}
		}else if (!world.isGameOver) {
			
			if (game.getInput().isTouchDown(0)) {
				int x = game.getInput().getTouchX(0);
				int y = game.getInput().getTouchY(0);
				if (x < 250 && y > 220) {
					world.player.direction = pressInwhichRect(105, 370, x, y);
					world.player.move(deltaTime);
				} else if( x < 320 && x > 160 && y < 80){
					//game.setScreen(new MainMenuScreen(game));
				}else if (x > WorldRect.right && y>300) {
					if(world.player.isLive){
						world.player.shoot(deltaTime);
					}
				}
			}
			if (game.getInput().isTouchDown(1)) {
				int x = game.getInput().getTouchX(1);
				int y = game.getInput().getTouchY(1);
				if (x < 250 && y > 220) {
					world.player.direction = pressInwhichRect(105, 370, x, y);
					world.player.move(deltaTime);
				} else if( x < 320 && x > 160 && y < 80){
					//game.setScreen(new MainMenuScreen(game));
				}else if (x > WorldRect.right && y>300) {
					if(world.player.isLive){
						world.player.shoot(deltaTime);
					}
				}
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		drawWorld(g, deltaTime);
		
	}

	public void drawWorld(Graphics g, float deltaTime) {
		// need to fresh the
		g.clear(0);
		g.drawPixmap(Assets.gameBackground, 0, 0);
		
		g.drawPixmap(Assets.scoreStar, 20, 25,0,0,30,30);
		String scoreString = ""+world.totalScore;
		g.drawText(scoreString, 60+scoreString.length()*5, 50, Color.RED, 22);
		
		g.drawPixmap(Assets.level, 20, 75);
		g.drawText("" + Setting.level, 65, 100, Color.RED, 22);
		
		
		
		g.drawPixmap(Assets.enemy, 20, 125,0,0,30,30);
		g.drawText("" + (world.totalEnemyNum() - world.createTankNum), 65, 150, Color.RED, 22);

		g.drawPixmap(Assets.player, 20, 175,0,0,30,30);
		if(world.playerLife>=0){
			g.drawText("" + world.playerLife, 65, 200, Color.RED, 22);
		}else{
			g.drawText("0", 65, 200, Color.RED, 22);
		}
		
		drawMap(g);
		drawEnemy(g);
		drawPlayer(g);
		drawExplodes(g);
		drawBonus(g, deltaTime);
		if (world.isShowFailLabel) {
			g.drawPixmap(Assets.failPopup,(800-Assets.failPopup.getWidth())/2,(480-Assets.failPopup.getHeight())/2);
			g.drawText(""+world.totalScore, 400, 118, Color.WHITE, 32);
		}else if(world.isShowSuccessLabel){
			g.drawPixmap(Assets.successPopup,(800-Assets.successPopup.getWidth())/2,(480-Assets.successPopup.getHeight())/2);
			g.drawText(""+world.levelScore, 410, 255, Color.RED, 30);
			g.drawText(""+world.totalScore, 410, 305, Color.RED, 30);
		}
		if(world.isPaused){
			g.drawPixmap(Assets.gamePlayBtn, 720, 25,0,0,40,40);
		}else{
			g.drawPixmap(Assets.gamePauseBtn,720, 25,0,0,40,40);
		}
	}

	private void drawBonus(Graphics g, float deltaTime) {
		for (int i = 0; i < world.bonus.length; i++) {
			if (world.bonus[i].isLive) {
				world.bonus[i].tickTime += deltaTime;
				if (world.bonus[i].tickTime < 15) {
					switch (world.bonus[i].bonus_type) {
					case 0:
						g.drawPixmap(Assets.bonus_bomb, WorldRect.left + world.bonus[i].x,
								world.bonus[i].y);
						break;
					case 1:
						g.drawPixmap(Assets.bonus_helmet, WorldRect.left + world.bonus[i].x,
								world.bonus[i].y);
						break;
					case 2:
						g.drawPixmap(Assets.bonus_spade, WorldRect.left + world.bonus[i].x,
								world.bonus[i].y);
						break;
					case 3:
						g.drawPixmap(Assets.bonus_star, WorldRect.left + world.bonus[i].x,
								world.bonus[i].y);
						break;
					case 4:
						g.drawPixmap(Assets.bonus_tank, WorldRect.left + world.bonus[i].x,
								world.bonus[i].y);
						break;
					}

				} else if (world.bonus[i].tickTime >= 15
						&& world.bonus[i].tickTime <= 20) {
					world.bonus[i].show++;
					if (world.bonus[i].show % 10 == 0) {

						switch (world.bonus[i].bonus_type) {
						case 0:
							g.drawPixmap(Assets.bonus_bomb, WorldRect.left + world.bonus[i].x,
									world.bonus[i].y);
							break;
						case 1:
							g.drawPixmap(Assets.bonus_helmet, WorldRect.left + world.bonus[i].x,
									world.bonus[i].y);
							break;
						case 2:
							g.drawPixmap(Assets.bonus_spade, WorldRect.left + world.bonus[i].x,
									world.bonus[i].y);
							break;
						case 3:
							g.drawPixmap(Assets.bonus_star, WorldRect.left + world.bonus[i].x,
									world.bonus[i].y);
							break;
						case 4:
							g.drawPixmap(Assets.bonus_tank, WorldRect.left + world.bonus[i].x,
									world.bonus[i].y);
							break;
						}
					}
				} else if (world.bonus[i].tickTime > 20) {
					world.bonus[i].isLive = false;
					world.bonus[i].tickTime = 0;
					world.bonus[i].show = 0;
				}
			}
		}
	}

	private void drawExplodes(Graphics g) {
		for (int i = 0; i < world.explodes.length; i++) {
			if (world.explodes[i].isLive) {
				tickTimes[i]++;
				if (tickTimes[i] < 8) {
					if (i < 5) {
						g.drawPixmap(Assets.explode,
								WorldRect.left + world.enemyTanks[i].x,
								world.enemyTanks[i].y, 0, 0,
								Setting.Explode_Size, Setting.Explode_Size);
					} else {
						g.drawPixmap(Assets.explode, WorldRect.left + world.player.x,
								world.player.y, 0, 0, Setting.Explode_Size,
								Setting.Explode_Size);
					}
				} else if (tickTimes[i] < 16) {
					if (i < 5) {
						g.drawPixmap(Assets.explode,
								WorldRect.left + world.enemyTanks[i].x,
								world.enemyTanks[i].y, Setting.Explode_Size, 0,
								Setting.Explode_Size, Setting.Explode_Size);
					} else {
						g.drawPixmap(Assets.explode, WorldRect.left + world.player.x,
								world.player.y, Setting.Explode_Size, 0,
								Setting.Explode_Size, Setting.Explode_Size);
					}
				} else if (tickTimes[i] < 24) {
					if (i < 5) {
						g.drawPixmap(Assets.explode,
								WorldRect.left + world.enemyTanks[i].x,
								world.enemyTanks[i].y,
								Setting.Explode_Size * 2, 0,
								Setting.Explode_Size, Setting.Explode_Size);
					} else {
						g.drawPixmap(Assets.explode, WorldRect.left + world.player.x,
								world.player.y, Setting.Explode_Size * 2, 0,
								Setting.Explode_Size, Setting.Explode_Size);
					}
				}else{
					tickTimes[i] = 0;
					world.explodes[i].isLive = false;
				}
			}
		}
	}

	private void drawPlayer(Graphics g) {
		if (world.player.isLive) {
			switch (world.player.tank_type) {
			case 0:
				g.drawPixmap(Assets.player,
						WorldRect.left + world.player.x-Setting.Tank_DrawDiff, world.player.y-Setting.Tank_DrawDiff,
						Setting.Tank_DrawSize*world.player.tank_type, Setting.Tank_DrawSize * world.player.direction,
						Setting.Tank_DrawSize, Setting.Tank_DrawSize);
				break;
			case 1:
				g.drawPixmap(Assets.player,
						WorldRect.left + world.player.x-Setting.Tank_DrawDiff, world.player.y-Setting.Tank_DrawDiff,
						Setting.Tank_DrawSize*world.player.tank_type, Setting.Tank_DrawSize * world.player.direction,
						Setting.Tank_DrawSize, Setting.Tank_DrawSize);
				break;
			case 2:
				g.drawPixmap(Assets.player,
						WorldRect.left + world.player.x-Setting.Tank_DrawDiff, world.player.y-Setting.Tank_DrawDiff,
						Setting.Tank_DrawSize*world.player.tank_type, Setting.Tank_DrawSize * world.player.direction,
						Setting.Tank_DrawSize, Setting.Tank_DrawSize);
				break;
			case 3:
				g.drawPixmap(Assets.player,
						WorldRect.left + world.player.x-Setting.Tank_DrawDiff, world.player.y-Setting.Tank_DrawDiff,
						Setting.Tank_DrawSize*world.player.tank_type, Setting.Tank_DrawSize * world.player.direction,
						Setting.Tank_DrawSize, Setting.Tank_DrawSize);
				break;
			}
			if (world.player.hasHat) {
				tickTime++;
				tickTime = tickTime % 10;
				if (tickTime == 0) {
					g.drawPixmap(Assets.tank_safe1, WorldRect.left + world.player.x - Setting.Tank_DrawDiff,
							world.player.y -Setting.Tank_DrawDiff);
				} else if (tickTime == 5) {
					g.drawPixmap(Assets.tank_safe2, WorldRect.left + world.player.x - Setting.Tank_DrawDiff,
							world.player.y -Setting.Tank_DrawDiff);
				}
			}
			
		}
		if (world.player.bullets.size()>0) {
			for(int i=0;i<world.player.bullets.size();i++){
				PlayerBullet b = (PlayerBullet)world.player.bullets.get(i);
				g.drawPixmap(Assets.bullets, WorldRect.left + b.x, b.y,
					Setting.Bullet_Size, 0, Setting.Bullet_Size, Setting.Bullet_Size);
			}
		}
		//the caodi is on the tank
		for(int i = 0; i< world.map.length ;i++){
			for(int j = 0; j< world.map[0].length ;j++){
				if(world.map[i][j] == 3){
					g.drawPixmap(Assets.terrain, WorldRect.left + Setting.Item_Size * j, Setting.Item_Size * i,
							Setting.Item_Size * 2, 0, Setting.Item_Size, Setting.Item_Size);
				}
			}
		}
	}

	private void drawEnemy(Graphics g) {
		for (int i = 0; i < world.enemyTanks.length; i++) {
			if (world.enemyTanks[i].isLive) {
				switch (world.enemyTanks[i].tank_type) {
				case 0:
					g.drawPixmap(Assets.enemy,
							WorldRect.left + world.enemyTanks[i].x-Setting.Tank_DrawDiff,
							world.enemyTanks[i].y-Setting.Tank_DrawDiff,
							0,Setting.Tank_DrawSize * world.enemyTanks[i].direction,
							Setting.Tank_DrawSize, Setting.Tank_DrawSize);
					break;
				case 1:
					g.drawPixmap(Assets.enemy,
							WorldRect.left + world.enemyTanks[i].x-Setting.Tank_DrawDiff,
							world.enemyTanks[i].y-Setting.Tank_DrawDiff,
							Setting.Tank_DrawSize, Setting.Tank_DrawSize * world.enemyTanks[i].direction,
							Setting.Tank_DrawSize, Setting.Tank_DrawSize);
					break;
				case 2:
					g.drawPixmap(Assets.enemy,
							WorldRect.left + world.enemyTanks[i].x-Setting.Tank_DrawDiff,
							world.enemyTanks[i].y-Setting.Tank_DrawDiff,
							Setting.Tank_DrawSize*2,Setting.Tank_DrawSize * world.enemyTanks[i].direction,
							Setting.Tank_DrawSize, Setting.Tank_DrawSize);
					break;
				case 3:
					g.drawPixmap(Assets.enemy,
							WorldRect.left + world.enemyTanks[i].x-Setting.Tank_DrawDiff,
							world.enemyTanks[i].y-Setting.Tank_DrawDiff,
							Setting.Tank_DrawSize*3,Setting.Tank_DrawSize * world.enemyTanks[i].direction,
							Setting.Tank_DrawSize, Setting.Tank_DrawSize);
					break;
				}
			}

			if (world.enemyTanks[i].bullet.isLive) {
				g.drawPixmap(Assets.bullets,
						WorldRect.left + world.enemyTanks[i].bullet.x, world.enemyTanks[i].bullet.y,
						0, 0,Setting.Bullet_Size, Setting.Bullet_Size);
			}
		}
	}

	private void drawMap(Graphics g) {
		for (int i = 0; i < World.World_Height; i++) {
			for (int j = 0; j < World.World_Width; j++) {
				int title = world.map[i][j];
				switch (title) {
				case 1:
					g.drawPixmap(Assets.terrain, WorldRect.left + Setting.Item_Size * j, Setting.Item_Size * i,
							0, 0, Setting.Item_Size, Setting.Item_Size);
					break;
				case 2:
					g.drawPixmap(Assets.terrain, WorldRect.left + Setting.Item_Size * j, Setting.Item_Size * i,
							Setting.Item_Size, 0, Setting.Item_Size, Setting.Item_Size);
					break;
				case 4:
					huTime ++;
					huTime = huTime%200;
					if(huTime < 100){
						g.drawPixmap(Assets.terrain, WorldRect.left + Setting.Item_Size * j, Setting.Item_Size * i,
								Setting.Item_Size * 3, 0, Setting.Item_Size, Setting.Item_Size);
					}else{
						g.drawPixmap(Assets.terrain, WorldRect.left + Setting.Item_Size * j, Setting.Item_Size * i,
								Setting.Item_Size * 4, 0, Setting.Item_Size, Setting.Item_Size);
					}
					break;				
				case 6:
					g.drawPixmap(Assets.terrain, WorldRect.left + Setting.Item_Size * j, Setting.Item_Size * i,
							Setting.Item_Size * 5, 0, Setting.Item_Size, Setting.Item_Size);
					break;
				case 7:
					g.drawPixmap(Assets.terrain, WorldRect.left + Setting.Item_Size * j, Setting.Item_Size * i,
							Setting.Item_Size * 6, 0, Setting.Item_Size, Setting.Item_Size);
					break;
				}
			}
		}
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

	// (160 ,320)
	public int pressInwhichRect(int x1, int y1, int x2, int y2) {
		if (Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
			if (x2 - x1 > 0) {
				return 1;
			} else {
				return 3;
			}
		} else {
			if (y2 - y1 > 0) {
				return 2;
			} else {
				return 0;
			}
		}
	}
}