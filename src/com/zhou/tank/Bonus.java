package com.zhou.tank;

public class Bonus extends ObjectCubo {
	
	public int bonus_type;//0--6
	public float tickTime;
	public int show = 0;
	public Bonus(){
		this.width = Setting.Bouns_Size;
		this.height = Setting.Bouns_Size;
	}
}
