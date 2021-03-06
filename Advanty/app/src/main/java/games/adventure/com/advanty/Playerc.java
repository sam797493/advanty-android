package games.adventure.com.advanty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import games.adventure.com.advanty.Settings;
import games.adventure.com.advanty.SoundManager;
import games.adventure.com.advanty.Sprite;
import games.adventure.com.advanty.Util;

public class Playerc {
	private static float MAX_JUMP_HEIGHT = Util.getPercentOfScreenHeight(15);  //50
	//private static float MIN_JUMP_HEIGHT = Util.getPercentOfScreenHeight(5);  //30
	private float lastPosY;
	static public float width;
	static public float height;
	public float x;
	public float y;
	private boolean jumping = false;
	private boolean jumpingsoundplayed = true;
	private boolean onGround = false;
	private boolean reachedPeak = false;
	private boolean slowSoundplayed = false;
	private float jumpStartY;

	private float velocity = 0;
	private float velocityMax = 0;
	private float velocityDownfallSpeed = 0;

	private Rect playerRect;
	private Rect ObstacleRect;
	private float speedoffsetX = 0;
	private float speedoffsetXStart;
	private float speedoffsetXMax;
	private float speedoffsetXStep;
	private Bitmap playerSpriteImg = null;
	public Sprite playerSprite;
	private boolean fingerOnScreen = false;
	private float bonusVelocity = 0;
	private float bonusVelocityDownfallSpeed = 0;

	public int bonusItems = 0;
	private int bonusScorePerItem = 200;


	public Playerc(Context context, OpenGLRenderer glrenderer, int ScreenHeight) {
		x = Util.getPercentOfScreenWidth(9); //70;
		y = Settings.FirstBlockHeight+Util.getPercentOfScreenHeight(4);

		width = Util.getPercentOfScreenWidth(9); //40; dicker //40; nyan cat //60; nyan cat pre minimalize //62; playersprite settings
		height = width*Util.mWidthHeightRatio; //40; dicker //30;  nyan cat //42; nyan cat pre minimalize //63; playersprite settings

		velocityMax = Util.getPercentOfScreenHeight(3); //9 Util.getPercentOfScreenHeight(1.875f)
		velocityDownfallSpeed = velocityMax/30.0f;
		bonusVelocityDownfallSpeed = velocityDownfallSpeed / 6.0f;

		speedoffsetXStart = x;
		speedoffsetXMax = Util.getPercentOfScreenWidth(7);
		speedoffsetXStep = Util.getPercentOfScreenWidth(0.002f);

		playerSpriteImg = Util.loadBitmapFromAssets("game_character_spritesheet3.png");
		playerSprite = new Sprite(x, y, 0.5f, width, height, 25, 8);
		playerSprite.loadBitmap(playerSpriteImg);
		glrenderer.addMesh(playerSprite);

		playerRect = new Rect();
		playerRect.left =(int)x;
		playerRect.top =(int)(y+height);
		playerRect.right =(int)(x+width);
		playerRect.bottom =(int)y;

		ObstacleRect = new Rect();
	}

	public void cleanup() {
		if (playerSpriteImg != null) playerSpriteImg.recycle();
	}

	public void setJump(boolean jump) {
		fingerOnScreen = jump;
		if(!jump)
		{
			reachedPeak = true;
			bonusVelocity = 0.0f;
		}

		if(reachedPeak || !onGround) return;

		jumpStartY = y;
		jumping = true;
		if(jump)
			jumpingsoundplayed = false;
	}

	public boolean update() {
		playerSprite.updatePosition(x, y);
		playerSprite.tryToSetNextFrame();

		if(jumpingsoundplayed==false){
			SoundManager.playSound(3, 1);
			jumpingsoundplayed = true;
		}

		if (jumping && !reachedPeak) {
			velocity += 1.5f * (MAX_JUMP_HEIGHT - (y - jumpStartY)) / 100.f;


			if(Settings.RHDEBUG){
				Log.d("debug", "y: " + (y));
				Log.d("debug", "y + height: " + (y + height));
				//Log.d("debug", "velocity: " + velocity);
				//Log.d("debug", "modifier: " + (MAX_JUMP_HEIGHT - (y - jumpStartY)) / 100.0f);
				//Log.d("debug", "MAX_JUMP_HEIGHT - (y - jumpStartY): " + (MAX_JUMP_HEIGHT - (y - jumpStartY)));
			}

			if(y - jumpStartY >= MAX_JUMP_HEIGHT)
			{
				reachedPeak = true;
			}
		}
		else
		{
			velocity -= velocityDownfallSpeed;
		}


		if (velocity < -velocityMax)
			velocity = -velocityMax;
		else if (velocity > velocityMax)
			velocity = velocityMax;

		y += velocity + bonusVelocity;

		bonusVelocity-= bonusVelocityDownfallSpeed;
		if (bonusVelocity < 0)
			bonusVelocity = 0;

		playerRect.left =(int)x;
		playerRect.top =(int)(y+height);
		playerRect.right =(int)(x+width);
		playerRect.bottom =(int)y;

		onGround = false;

		for (int i = 0; i < Levelc.maxBlocks; i++)
		{
			if( checkIntersect(playerRect, Levelc.blockData[i].BlockRect) )
			{
				if(lastPosY >= Levelc.blockData[i].mHeight && velocity <= 0)
				{
					y=Levelc.blockData[i].mHeight;
					velocity = 0;
					reachedPeak = false;
					jumping = false;
					onGround = true;
					bonusVelocity = 0.0f;
				}
				else{
					// false -> player stops at left -> block mode
					// true -> player goes through left side -> platform mode
					return false;
				}
			}
		}
		lastPosY = y;

		if(speedoffsetX<speedoffsetXMax ) //50
			speedoffsetX += speedoffsetXStep; //0.01

		x=speedoffsetXStart+speedoffsetX;

		if(y + height < 0){
			y = -height;
			return false;
		}

		return true;
	}

	public boolean collidedWithObstacle(float LevelcPosition) {

		for(int i = 0; i < Levelc.maxObstaclesJumper; i++)
		{
			ObstacleRect.left =  (int)Levelc.obstacleDataJumper[i].x;
			ObstacleRect.top = (int)Levelc.obstacleDataJumper[i].y+(int)Levelc.obstacleDataJumper[i].height;
			ObstacleRect.right = (int)Levelc.obstacleDataJumper[i].x+(int)Levelc.obstacleDataJumper[i].width;
			ObstacleRect.bottom = (int)Levelc.obstacleDataJumper[i].y;

			if( checkIntersect(playerRect, ObstacleRect) && !Levelc.obstacleDataJumper[i].didTrigger)
			{
				Levelc.obstacleDataJumper[i].didTrigger=true;

				SoundManager.playSound(6, 1);
				velocity = Util.getPercentOfScreenHeight(2.6f);//6; //katapultiert den player wie ein trampolin nach oben

				if (fingerOnScreen)
					bonusVelocity = Util.getPercentOfScreenHeight(1.5f);
			}
		}

		for(int i = 0; i < Levelc.maxObstaclesSlower; i++)
		{
			ObstacleRect.left =  (int)Levelc.obstacleDataSlower[i].x;
			ObstacleRect.top = (int)Levelc.obstacleDataSlower[i].y+(int)Levelc.obstacleDataSlower[i].height;
			ObstacleRect.right = (int)Levelc.obstacleDataSlower[i].x+(int)Levelc.obstacleDataSlower[i].width;
			ObstacleRect.bottom = (int)Levelc.obstacleDataSlower[i].y;

			if( checkIntersect(playerRect, ObstacleRect) && !Levelc.obstacleDataSlower[i].didTrigger)
			{
				Levelc.obstacleDataSlower[i].didTrigger=true;

				//TODO: prevent playing sound 2x or more
				if(!slowSoundplayed){
					SoundManager.playSound(5, 1);
					slowSoundplayed=true;
				}
				return true; //slow down player fast
			}
		}

		for(int i = 0; i < Levelc.maxObstaclesBonus; i++)
		{
			ObstacleRect.left =  (int)Levelc.obstacleDataBonus[i].x;
			ObstacleRect.top = (int)Levelc.obstacleDataBonus[i].y+(int)Levelc.obstacleDataBonus[i].height;
			ObstacleRect.right = (int)Levelc.obstacleDataBonus[i].x+(int)Levelc.obstacleDataBonus[i].width;
			ObstacleRect.bottom = (int)Levelc.obstacleDataBonus[i].y;

			if( checkIntersect(playerRect, ObstacleRect) && !Levelc.obstacleDataBonus[i].didTrigger)
			{
				SoundManager.playSound(8, 1);
				Levelc.obstacleDataBonus[i].didTrigger=true;

				bonusItems++;
				Levelc.obstacleDataBonus[i].z= -1;
			}
		}
		slowSoundplayed=false;
		return false;
	}

	public boolean checkIntersect(Rect playerRect, Rect blockRect) {
		if(playerRect.bottom >= blockRect.bottom && playerRect.bottom <= blockRect.top)
		{
			if(playerRect.right >= blockRect.left && playerRect.right <= blockRect.right )
				return true;
			else if(playerRect.left >= blockRect.left && playerRect.left <= blockRect.right )
				return true;
		}
		else if(playerRect.top >= blockRect.bottom && playerRect.top <= blockRect.top){
			if(playerRect.right >= blockRect.left && playerRect.right <= blockRect.right )
				return true;
			else if(playerRect.left >= blockRect.left && playerRect.left <= blockRect.right )
				return true;
		}
		//blockrect in playerrect
		if(blockRect.bottom >= playerRect.bottom && blockRect.bottom <= playerRect.top)
			if(blockRect.right >= playerRect.left && blockRect.right <= playerRect.right )
				return true;

		return false;
	}

	public void reset() {
		velocity = 0;
		x = 70; // x/y is bottom left corner of picture
		y = Settings.FirstBlockHeight+20;

		speedoffsetX = 0;
		bonusItems = 0;
	}

	public int getBonusScore()
	{
		return bonusItems * bonusScorePerItem;
	}



}
