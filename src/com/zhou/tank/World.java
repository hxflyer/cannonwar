package com.zhou.tank;

import java.util.Random;

import android.provider.UserDictionary.Words;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.zhou.tank.ObjectCubo.MapListener;

public class World implements MapListener {

	public interface WorldListener {
		public int[][] loadMap(int level);
	}

	public static final int World_Width = 16;
	public static final int World_Height= 16;
	public static int WORLD_WIDTH		= 480;
	public static int WORLD_HEIGHT		= 480;
	public static int WORLD_HALFWIDTH	= 240;
	public static int WORLD_HALFHEIGHT	= 240;
	// 6 ,nextlevel ,5 player ,0--4 enemys
	public float[] tickTimes = new float[7];
	public int totalScore = 0;
	public int levelScore = 0;
	public float homeIsFeTime;
	public int homeshow;

	public Player player;
	public Enemy[] enemyTanks;
	public Explode[] explodes;
	public Bonus[] bonus;

	public int createTankNum;
	public int movingTankNum;
	// public int enemyNum;// how many enemytanks on the screen
	public int playerLife;// the lifes of player who has on the screen
	public int[][] map;
	public boolean isGameOver;
	public WorldListener worldListener;
	public boolean isShowSuccessLabel;
	public boolean isShowFailLabel;
	
	public Game game;
	public World(WorldListener worldListener,Game parentGame) {
		player = new Player(this);
		game = parentGame;
		enemyTanks = new Enemy[3];
		for (int i = 0; i < 3; i++) {
			enemyTanks[i] = new Enemy(this);
		}
		explodes = new Explode[6];
		for(int i = 0;i<explodes.length ;i++){
			explodes[i] = new Explode();
		}
		bonus = new Bonus[5];
		for (int i = 0; i < bonus.length; i++) {
			bonus[i] = new Bonus();
		}
		this.worldListener = worldListener;
		playerLife = 1;
	}

	public void initWorld(int level) {
		if(Setting.soundEnabled){
			Assets.enter_game.play(1);
		}
		
		player.initPlayer(Setting.Item_Size * 4, WORLD_HEIGHT - Setting.Tank_Size - 1, Setting.UP);
		Random random = new Random();
		enemyTanks[0].initTank(0, 0, random.nextInt(4), Setting.DOWN);
		enemyTanks[1].initTank(WORLD_HALFWIDTH - Setting.Tank_Size / 2, 0,
				random.nextInt(4), Setting.DOWN);
		enemyTanks[2].initTank(WORLD_WIDTH - Setting.Tank_Size - 1, 0,
				random.nextInt(4), Setting.DOWN);
		
		Setting.level = level;
		levelScore			= 0;
		isGameOver 			= false;
		isShowSuccessLabel  = false;
		isShowFailLabel		= false;
		createTankNum = 3;
		movingTankNum = 3;
		for (int i = 0; i < bonus.length; i++) {
			bonus[i].isLive = false;
			bonus[i].tickTime = 0;
			bonus[i].show = 0;
		}
		map = worldListener.loadMap(Setting.level);
	}
	
	public boolean isPaused = false;
	public void update(float deltaTime) {
		
		for (int i = 0; i < enemyTanks.length; i++) {
			enemyTanks[i].update(deltaTime);
		}
		player.update(deltaTime);
		for (int i = 0; i < bonus.length; i++) {
			player.eatBonus(this, bonus[i]);
		}
		updateEnemy(deltaTime);
		updatePlayer(deltaTime);
		updatePlayerHasBonus(deltaTime);
		if (movingTankNum > 0 && !isGameOver) {
			checkBulletShotTank();
		} else {
			if (createTankNum >= totalEnemyNum()) {
				doSuccess(deltaTime);
			}
		}
		if (isGameOver) {
			player.changeType(0);
			doFail(deltaTime);
		}
	}

	public void doFail(float deltaTime) {
		tickTimes[6] += deltaTime;
		if (tickTimes[6] > 2) {
			if(isShowFailLabel==false){
				game.saveHighestScore(totalScore);
			}
			isShowFailLabel = true;
		}
		
	}
	
	public void restart(){
		totalScore		= 0;
		homeshow		= 0;
		homeIsFeTime	= 0;
		initWorld(1);
		playerLife		= 1;
		tickTimes[6]	= 0;
	}
	public void doSuccess(float deltaTime) {
		tickTimes[6] += deltaTime;
		if (tickTimes[6] > 1 && !isGameOver) {
			if(isShowSuccessLabel==false){
				game.saveHighestScore(totalScore);
				if(Setting.soundEnabled){
					Assets.winSound.play(1);
				}
			}
			isShowSuccessLabel = true;
		}
	}
	public void nextLevel(){
		if (Setting.level++ > Setting.totalLevel) {
			Setting.level = 1;
		}
		
		initWorld(Setting.level);
		tickTimes[6] = 0;
		//resetHome();
		homeshow = 0;
		homeIsFeTime = 0;
	}
	public void updateEnemy(float deltaTime) {
		if (createTankNum < totalEnemyNum()) {
			for (int i = 0; i < enemyTanks.length; i++) {
				if (enemyTanks[i].isLive == false) {
					tickTimes[i] += deltaTime;
					if (tickTimes[i] > 1.5) {
						tickTimes[i] = 0;
						enemyTanks[i].initTank(new Random().nextInt(3)
								* (WORLD_HALFWIDTH - Setting.Tank_Size / 2 - 1), 0,
								new Random().nextInt(4), Setting.DOWN);
						movingTankNum++;
						createTankNum++;
						if (createTankNum >= totalEnemyNum())
							break;
					}
				}
			}
		}
	}
	public int totalEnemyNum(){
		return(3+Setting.level);
	}
	public void updatePlayer(float deltaTime) {
		if (playerLife >= 0) {
			if (player.isLive == false) {
				tickTimes[5] += deltaTime;
				if (tickTimes[5] > 1) {
					tickTimes[5] = 0;
					player.initTank(Setting.Item_Size*4, WORLD_HEIGHT - Setting.Tank_Size - 1, 0,
							Setting.UP);
				}
			}
		}
	}

	// set home
	public void setHomeToFe() {
		map[14][6]  = 2;
		map[14][7]  = 2;
		map[14][8]  = 2;
		map[15][6]  = 2;
		map[15][8]  = 2;
	}

	public void resetHome() {
		map[14][6]  = 1;
		map[14][7]  = 1;
		map[14][8]  = 1;
		map[15][6]  = 1;
		map[15][8]  = 1;
	}

	public void updatePlayerHasBonus(float deltaTime) {
		if (player.eatBomb) {
			for(int i = 0 ;i < enemyTanks.length ;i++ ){
				if(enemyTanks[i].isLive){
					explodes[i].isLive = true;
					enemyTanks[i].isLive = false;		
					totalScore += (Setting.level-1)*20+100;
					levelScore += (Setting.level-1)*20+100;
				}
			}
			movingTankNum = 0;
			player.eatBomb = false;
		}
		if (player.hasSpade) {
			homeIsFeTime += deltaTime;
			if (homeIsFeTime < 15) {
				setHomeToFe();
			} else if (homeIsFeTime < 25) {
				homeshow++;
				if (homeshow % 10 == 0) {
					resetHome();
				} else if (homeshow % 10 == 5) {
					setHomeToFe();
				}
			} else {
				resetHome();
				homeshow = 0;
				homeIsFeTime = 0;
				player.hasSpade = false;
			}
		}
		
		if (player.hasHat) {
			player.hasHatTime += deltaTime;
			if (player.hasHatTime > 10) {
				player.hasHatTime = 0;
				player.hasHat = false;
			}
		}
	}

	public void checkBulletShotTank() {
		for (int i = 0; i < enemyTanks.length; i++) {
			Enemy enemy = enemyTanks[i];
			for(int j=0;j<player.bullets.size();j++){
				PlayerBullet b = (PlayerBullet)player.bullets.get(j);
				if (b.isLive && enemy.isLive) {
	
					if (enemy.getRect().intersect(b.getRect())) {
						player.bullets.remove(j);
						j--;
						if(Setting.soundEnabled){
							Assets.explodeSound.play(1);
						}
						enemy.tank_lifes -= b.bullet_strength;
						if(new Random().nextInt(10)<2){
							createBonus();
						}
						if (enemy.tank_lifes <= 0) {
							enemy.isLive = false;
							explodes[i].isLive = true;
							movingTankNum--;
						} else {
							enemy.changeType(enemy.tank_type - b.bullet_strength);
						}
						totalScore += (Setting.level-1)*20+100;
						levelScore += (Setting.level-1)*20+100;
					}
				}
				if (enemy.bullet.isLive) {
					// bullet and bullet
					if (b.isLive) {
						if (enemy.bullet.getRect().intersect(b.getRect())) {
							b.isLive = false;
							enemy.bullet.isLive = false;
						}
					}
				}
			}
			if (enemy.bullet.isLive) {
				// bullet and player
				if (player.isLive && player.getRect().intersect(enemy.bullet.getRect())) {
					enemy.bullet.isLive = false;
					if (!player.hasHat) {
						player.tank_lifes -= enemy.bullet.bullet_strength;
						if (player.tank_lifes <= 0) {
							explodes[5].isLive = true;
							playerLife--;							
							player.isLive = false;
							if(Setting.soundEnabled){
								Assets.explodeSound.play(1);
							}
							if (playerLife < 0) {
								isGameOver = true;
								if(Setting.soundEnabled){
									Assets.lose.play(1);
								}
								return;
							}
						} else {
							player.changeType(player.tank_type
									- enemy.bullet.bullet_strength);
						}
					}
				}
			}
			
		}
	}

	public void createBonus(){
		
		
			if(Setting.soundEnabled){
				Assets.show_bonus.play(1);
			}
			int firstDeatBonusId = bonus.length;
			for(int i=0;i<bonus.length;i++){
				if (bonus[i].isLive==false){
					firstDeatBonusId = i;
					break;
				}
			}
			if(firstDeatBonusId<bonus.length){
			bonus[firstDeatBonusId].isLive = true;
			bonus[firstDeatBonusId].tickTime = 0;
			bonus[firstDeatBonusId].show = 0;
			int bonusType = new Random().nextInt(5);
			bonus[firstDeatBonusId].bonus_type = bonusType;
			bonus[firstDeatBonusId].x = new Random()
					.nextInt(WORLD_WIDTH - Setting.Bouns_Size - 1);
			bonus[firstDeatBonusId].y = new Random()
					.nextInt(WORLD_HEIGHT - Setting.Bouns_Size * 2);
			}
		
	}
	
	@Override
	public boolean checkTank(Tank tank, int x, int y) {
		int item1 = map[y / Setting.Item_Size][x / Setting.Item_Size];
		if (item1 == 0 || item1 == 3)
			return true;
		
		return false;
	}		

	@Override
	public boolean checkBullet(Bullet bullet, int x, int y) {
		int y1 = (y + Setting.Bullet_Size / 2) / Setting.Item_Size;
		int x1 = (x + Setting.Bullet_Size / 2) / Setting.Item_Size;
		int item1 = map[y1][x1];
		switch (item1) {
		case 0:
			return true;
		case 1:
			map[y1][x1] = 0;
			return false;
		case 2:
			if (bullet.bullet_strength == 4) {
				map[y1][x1] = 0;
			}
			return false;
		case 3:
			if (bullet.bullet_strength == 4) {
				map[y1][x1] = 0;
			}
		case 4:
		case 5:
		case 7:
			return true;
		case 6:
			isGameOver = true;
			map[y1][x1] = 7;
			if(Setting.soundEnabled){
				Assets.lose.play(1);
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean checkTankP(Tank tank , int x ,int y) {
		int x1 = tank.x;
		int y1 = tank.y;
		tank.x = x;
		tank.y = y;	
		for(int i = 0;i< enemyTanks.length ;i++){
			if(enemyTanks[i].isLive){
				if(tank.hashCode() != enemyTanks[i].hashCode()){													
					if(tank.getRect().intersect(enemyTanks[i].getRect())){
						tank.x = x1;
						tank.y = y1;
						return false;
					}
				}
			}			
		}
		if(tank.hashCode()!= player.hashCode()){
			if(tank.getRect().intersect(player.getRect())){
				tank.x = x1;
				tank.y = y1;
				return false;
			}				
		}
		tank.x = x1;
		tank.y = y1;
		return true;
	}

	public void dispose() {
		worldListener = null;
		
	}

	
}