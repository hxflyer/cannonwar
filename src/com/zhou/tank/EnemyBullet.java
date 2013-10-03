package com.zhou.tank;

/**
 * 敌人子弹
 * @author admin
 * @date 2013-10-3-上午12:11:13
 */
public class EnemyBullet extends Bullet {

	public EnemyBullet(MapListener mapListener) {
		super(mapListener);
	}

	@Override
	public void changeType(int type) {
		this.bullet_type = type;
		this.bullet_speed = Bullet.enemy_bullet_speed[type];
		this.bullet_strength = Bullet.enemy_bullet_strength[type];
	}

	@Override
	public void setGood() {
		this.isGood = false;
	}
}