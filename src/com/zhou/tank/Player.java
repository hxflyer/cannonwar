package com.zhou.tank;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.util.Log;

public class Player extends Tank {

	public boolean hasHat;
	//public boolean isOnbroad;
	public int starNum;
	public boolean hasSpade;
	public boolean eatBomb;
	//public float hasShipTime;
	public float hasHatTime;
	public int coolDownCount;
	
	public Player(MapListener mapListener) {
		super(mapListener);
		bullets = new ArrayList<Bullet>();
		
	}
	
	@Override
	public void initTank(int x, int y, int type, int direction) {
		super.initTank(x, y, type, direction);
		hasSpade = false;		
		starNum = 0;
		hasHat = true;		
		eatBomb = false;		
		hasHatTime = 0;
	}
	
	public void initPlayer(int x, int y , int direction){
		this.direction = direction;
		this.x = x;
		this.y = y;
		hasSpade = false;		
		hasHat = true;		
		eatBomb = false;		
		hasHatTime = 0;
	}


	@Override
	public void changeType(int type) {
		if (type < 0 || type > 4) {
			throw new RuntimeException(
					"the tank_type of player is now right in changeType()");
		}
		this.tank_type = Tank.player_type[type];
		this.tank_lifes = Tank.player_lifes[type];
		this.tank_speed = Tank.player_speed[type];
		//bullet.changeType(type);
		for(int i =0;i<bullets.size();i++){
			bullets.get(i).changeType(type);
		}
	}

	@Override
	
	public void update(float deltaTime) {
		for(int i =0;i<bullets.size();i++){
			PlayerBullet b = (PlayerBullet)bullets.get(i);
			b.update(deltaTime);
			if(!b.isLive){
				bullets.remove(i);
				i--;
			}
		}
		coolDownCount--;
	}

	@Override
	public void changeDirection(float deltaTime) {
	}

	@Override
	public void setGood() {
		this.isGood = true;
		for(int i =0;i<bullets.size();i++){
			bullets.get(i).initBullet(x + Setting.Tank_Size / 2 - Setting.Bullet_Size / 2,
					y + Setting.Tank_Size / 2 - Setting.Bullet_Size / 2, direction);
		}
	}

	@Override
	public void shoot(float deltaTime) {
		if(coolDownCount>0){
			return;
		}
		if(Setting.soundEnabled)
			Assets.shoot.play(1);
		PlayerBullet b = new PlayerBullet(mapListener);	
		b.isLive = true;
		b.changeType(this.tank_type);
		b.initBullet(x + Setting.Tank_Size / 2 - Setting.Bullet_Size
				/ 2, y + Setting.Tank_Size / 2 - Setting.Bullet_Size / 2,
				direction);
		bullets.add(b);
		coolDownCount = 10;
	}
	
	/**
	 * 玩家吃奖励
	 * @param world
	 * @param bonus
	 */
	public void eatBonus(World world, Bonus bonus){
		if(!bonus.isLive){
			return;
		}
		if(!isLive){
			return;
		}
		if(getRect().intersect(bonus.getRect())){
			if(Setting.soundEnabled){
				Assets.catch_bonus.play(1);
			}
			world.totalScore +=50;
			bonus.isLive = false;
			switch (bonus.bonus_type) {
			case 0:
				eatBomb = true;
				break;
			case 1:
				hasHat = true;
				break;
			case 2:
				hasSpade = true;
				world.homeshow = 0 ;
				world.homeIsFeTime = 0;
				break;
			case 3:
				starNum++;
				hasHat = true;
				hasHatTime = 8;
				bonus.tickTime = 20;
				if(starNum < 4 && tank_type != 3){
					changeType(tank_type + 1);
				}
				break;
			case 4:
				world.playerLife++;
				bonus.tickTime = 25;
				break;
			}
		}
	}
}