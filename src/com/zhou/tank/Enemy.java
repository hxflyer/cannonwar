package com.zhou.tank;


public class Enemy extends Tank {
		
	public Enemy(MapListener mapListener) {
		super(mapListener);
		bullet = new EnemyBullet(mapListener);
	}

	@Override
	public void changeType(int type) {
		
		if(type < 0 || type > 8){
			throw new RuntimeException("the tank_type of player is now right in changeType()");
		}
		this.tank_type = type;
		this.tank_lifes = Tank.enemy_lifes[type];
		this.tank_speed = Tank.enemy_speed[type];		
		bullet.changeType(type);
	}

	@Override
	public void changeDirection(float deltaTime) {
		tickTime += deltaTime;
		if(tickTime > random.nextFloat()+2){
			int direction = random.nextInt(4);
			this.direction = direction;
			tickTime = 0;
		}
	}

	@Override
	public void setGood() {
		
		this.isGood = false;
		bullet.initBullet(x + Setting.Tank_Size/2 - Setting.Bullet_Size/2, 
				  y + Setting.Tank_Size/2 - Setting.Bullet_Size/2, direction);
	}
}