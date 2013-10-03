package com.zhou.tank;

import android.util.Log;

/**
 * 玩家字典
 * @author admin
 * @date 2013-10-3-上午12:11:32
 */
public class PlayerBullet extends Bullet {

	public PlayerBullet(MapListener mapListener) {
		super(mapListener);
	}

	@Override
	public void changeType(int type) {
		this.bullet_type = type;
		this.bullet_speed = Bullet.player_bullet_speed[type];
		this.bullet_strength = Bullet.player_bullet_strength[type];
		Log.d("tank", "bullet_speed: " + Float.toString(bullet_speed));
	}

	@Override
	public void setGood() {
		this.isGood = true;
	}
	
	public void shootTank(Enemy tank){
		if(tank.getRect().intersect(getRect())){
			isLive = false;
			tank.tank_lifes -= bullet_strength;
			
			if(tank.tank_lifes <= 0){
				
			}
		}
	}
}
