package com.zhou.tank;

public class EnemyBullet extends Bullet {

	public EnemyBullet(MapListener mapListener) {
		super(mapListener);
	}

	@Override
	public void changeType(int type) {
		// TODO Auto-generated method stub
		this.bullet_type = type;
		this.bullet_speed = Bullet.enemy_bullet_speed[type];
		this.bullet_strength = Bullet.enemy_bullet_strength[type];
	}

	@Override
	public void setGood() {
		// TODO Auto-generated method stub
		this.isGood = false;
	}
}