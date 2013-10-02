package com.zhou.tank;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Graphics.PixmapFormat;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.impl.AndroidPixmap;

public class Assets {
	
	//background
	public static Pixmap menuBackground;
	public static Pixmap gameBackground;
	
	public static Pixmap login360;
	public static Pixmap logout360;
	
	//bonus
	public static Pixmap bonus_bomb;
	public static Pixmap bonus_helmet;
	public static Pixmap bonus_spade;
	public static Pixmap bonus_star;
	public static Pixmap bonus_tank;
	
	//effect
	public static Pixmap tank_safe1;
	public static Pixmap tank_safe2;
	public static Pixmap explode;
	
	//tank
	public static Pixmap enemy;
	public static Pixmap player;
	public static Pixmap bullets;
	//terrain
	public static Pixmap terrain;
	
	//leftSide icon
	public static Pixmap scoreStar;
	public static Pixmap level;
	
	//game btn
	public static Pixmap gamePlayBtn;
	public static Pixmap gamePauseBtn;
	
	//popups
	public static Pixmap successPopup;
	public static Pixmap failPopup;
	public static AndroidPixmap shareScoreImg;
	
	//sound
	public static Sound shoot;
	public static Sound show_bonus;
	public static Sound catch_bonus;
	public static Sound lose;
	public static Sound enter_game;
	public static Sound explodeSound;
	public static Sound winSound;
	
	public static void load(Game game){
		Graphics g = game.getGraphics();
		//bg
		menuBackground 		= g.newPixmap("menuBackground.jpg", PixmapFormat.ARGB4444);
		gameBackground		= g.newPixmap("gameBackground.png", PixmapFormat.ARGB4444);
		
		login360			= g.newPixmap("login360.png", PixmapFormat.ARGB4444);
		logout360			= g.newPixmap("logout360.png", PixmapFormat.ARGB4444);
		
		//bonus
		bonus_bomb 			= g.newPixmap("bonus_bomb.png", PixmapFormat.ARGB4444);
		bonus_helmet 		= g.newPixmap("bonus_helmet.png", PixmapFormat.ARGB4444);
		bonus_spade 		= g.newPixmap("bonus_spade.png", PixmapFormat.ARGB4444);
		bonus_star			= g.newPixmap("bonus_star.png", PixmapFormat.ARGB4444);
		bonus_tank 			= g.newPixmap("bonus_tank.png", PixmapFormat.ARGB4444);
		
		//effect
		tank_safe1 			= g.newPixmap("tank_safely1.png", PixmapFormat.ARGB4444);
		tank_safe2 			= g.newPixmap("tank_safely2.png", PixmapFormat.ARGB4444);
		explode 			= g.newPixmap("explode.png", PixmapFormat.ARGB4444);
		
		//tank
		enemy 				= g.newPixmap("enemy.png", PixmapFormat.ARGB4444);
		player 				= g.newPixmap("player.png", PixmapFormat.ARGB4444);
		bullets 			= g.newPixmap("bullet.png", PixmapFormat.ARGB4444);
		
		gamePlayBtn			= g.newPixmap("playBtn.png", PixmapFormat.ARGB4444);
		gamePauseBtn		= g.newPixmap("pauseBtn.png", PixmapFormat.ARGB4444);
		//terrain
		terrain 			= g.newPixmap("terrain.png", PixmapFormat.ARGB4444);
		
		//leftside banner
		scoreStar			= g.newPixmap("scoreStar.png", PixmapFormat.ARGB4444);
		level 				= g.newPixmap("level.png", PixmapFormat.ARGB4444);
		
		//popups
		failPopup			= g.newPixmap("failPopup.png", PixmapFormat.ARGB4444);
		successPopup		= g.newPixmap("successPopup.png", PixmapFormat.ARGB4444);
		
		//sound
		enter_game			= game.getAudio().newSound("enter_game.mp3");
		show_bonus			= game.getAudio().newSound("show_bonus.mp3");
		catch_bonus			= game.getAudio().newSound("catch_bonus.mp3");
		shoot 				= game.getAudio().newSound("shoot.mp3");
		lose 				= game.getAudio().newSound("lose.mp3");
		explodeSound		= game.getAudio().newSound("explode.mp3");
		winSound			= game.getAudio().newSound("win.mp3");
		
		game.setScreen(new MainMenuScreen(game));
	}
	
	public static void unload(){
		menuBackground.dispose();
		gameBackground.dispose();
		
		login360.dispose();
		logout360.dispose();
		//bonus
		bonus_bomb.dispose();
		bonus_helmet.dispose();
		bonus_spade.dispose();
		bonus_star.dispose();
		bonus_tank.dispose();
		
		//effect
		tank_safe1.dispose();
		tank_safe2.dispose();
		explode.dispose();
		
		//tank
		enemy.dispose();
		player.dispose();
		bullets.dispose();
		
		//terrain
		terrain.dispose();
		
		//leftside banner
		scoreStar.dispose();
		level.dispose();
		
		//popups
		failPopup.dispose();
		successPopup.dispose();
		
		shareScoreImg.dispose();
		
		//sound
		enter_game.dispose();
		show_bonus.dispose();
		catch_bonus.dispose();
		shoot.dispose();
		lose.dispose();
		explodeSound.dispose();
		winSound.dispose();
	}
	
}
