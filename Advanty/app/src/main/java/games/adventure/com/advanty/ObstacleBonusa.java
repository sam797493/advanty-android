package games.adventure.com.advanty;

import android.graphics.Bitmap;

import games.adventure.com.advanty.Util;


public class ObstacleBonusa extends Obstacle {
	public char ObstacleType; //s=slow, j=jumper //b=bonus
	public boolean didTrigger;
	private int radX;
	private int radY;
	public float centerX;
	public float centerY;
	private float angle;
	private float orbitSpeed;
	

	public Bitmap bonusScoreEffectImg;

	
	public ObstacleBonusa(float _x, float _y, float _z, float _width, float _height, char type){
		super(_x, _y, _z, _width, _height, type);
		
		x=_x;
		y=_y;
		z=_z;
		
		radX=radY=(int)(_width*2); //50
		angle=0;
		orbitSpeed=_width/1000; //radX/1000; //0.05f; 
		centerX=x;
		centerY=y;
		
		bonusScoreEffectImg = Util.loadBitmapFromAssets("game_bonusscore.png");
		float bonusScoreEffectImgWidthHeightFector = bonusScoreEffectImg.getWidth()/bonusScoreEffectImg.getHeight();
		float bonusScoreEffectWidth = Util.getPercentOfScreenWidth(20);
		float bonusScoreEffectHeight = bonusScoreEffectWidth/bonusScoreEffectImgWidthHeightFector;

		
		
	}
	

	public void updateObstacleCircleMovement(){
		x = centerX+(float)Math.cos(angle)*radX;
		y = centerY+(float)Math.sin(angle)*radY;
		angle += orbitSpeed;
	}	
}
