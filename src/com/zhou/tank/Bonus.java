package com.zhou.tank;

/**
 * 奖励
 * @author admin
 * @date 2013-10-3-上午12:27:28
 */
public class Bonus extends ObjectCubo {
	
	public int bonus_type;//0--6
	public float tickTime;
	public int show = 0;
	public Bonus(){
		this.width = Setting.Bouns_Size;
		this.height = Setting.Bouns_Size;
	}
}
