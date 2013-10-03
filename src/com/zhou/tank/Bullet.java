package com.zhou.tank;

/**
 * 子弹
 * @author admin
 * @date 2013-10-3-上午12:10:47
 */
public abstract class Bullet extends ObjectCubo {
	
	public static final int[] player_bullet_type = { 0 , 1 , 2 , 3};
	public static final int[] player_bullet_strength = { 1 , 1 , 2 , 4};
	public static final int[] player_bullet_speed = { 
		Setting.Bullet_Fast_Speed, 
		Setting.Bullet_VeryFast_Speed,
		Setting.Bullet_VeryFast_Speed, 
		Setting.Bullet_SuperFast_Speed
	};
	
	public static final int[] enemy_bullet_type = {0, 1, 2, 3};
	public static final int[] enemy_bullet_strength = {1, 1, 2, 2};//stack the life of tank;
	public static final int[] enemy_bullet_speed = {
		Setting.Bullet_Mode_Speed,
		Setting.Bullet_Mode_Speed,
		Setting.Bullet_Fast_Speed,
		Setting.Bullet_SuperFast_Speed 
	};
	
	public boolean isGood;
	public int direction;
	public int bullet_type;
	public int bullet_strength;
	public int bullet_speed;
	
	public Bullet(MapListener mapListener){
		this.mapListener = mapListener;
		this.width = Setting.Bullet_Size;
		this.height = Setting.Bullet_Size;
		isLive = false;
	}
			
	public void initBullet(int x ,int y ,int direction){
		this.x = x ;
		this.y = y;
		this.direction = direction;
		
		setGood();
	}
	
	public abstract void setGood();
	public abstract void changeType( int type );
	
	public void update(float deltaTime){
		move();
	}
	
	public void move(){		
		if(!isLive)
			return;
		
		int x1 = x;
		int y1 = y;
		switch(direction){
			case Setting.LEFT:
				x1 -= bullet_speed;
				break;
			case Setting.UP:
				y1 -= bullet_speed;
				break;
			case Setting.RIGHT:
				x1 += bullet_speed;
				break;
			case Setting.DOWN:
				y1 += bullet_speed;
				break;
		}
		bulletIsLive(x1 , y1);
		if(isLive){
			x = x1;
			y = y1;
		}
	};
	
	public void bulletIsLive( int x ,int y){
		isLive = ( x < - Setting.Bullet_Size/2 || x >= World.WORLD_WIDTH - Setting.Bullet_Size/2 || y < 0 || y >= World.WORLD_HEIGHT - Setting.Bullet_Size/2) ? false : true;
		if(isLive){
			isLive = mapListener.checkBullet(this ,x, y);
		}			
	}
	
	public void checkMiss(){
		
	}
}