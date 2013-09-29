package com.zhou.tank;

import android.graphics.Rect;

public abstract class ObjectCubo {
	/**
	 * object has position x, y and width,height
	 */
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean isLive;
	
	public Rect getRect(){
		return new Rect(x, y, x+width, y+height);
	}
	
	public MapListener mapListener;
	
	public interface MapListener{
		public boolean checkTankP(Tank tank , int x ,int y);
		public boolean checkTank(Tank tank , int x ,int y);
		public boolean checkBullet(Bullet bullet,int x ,int y);
	}
}
