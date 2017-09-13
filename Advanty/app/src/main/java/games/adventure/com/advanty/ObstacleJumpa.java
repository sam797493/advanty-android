package games.adventure.com.advanty;

import android.graphics.Bitmap;

import games.adventure.com.advanty.Obstacle;
import games.adventure.com.advanty.Sprite;
import games.adventure.com.advanty.Util;


public class ObstacleJumpa extends Obstacle {
	public Bitmap jumpSpriteImg;

	public Sprite jumpSprite = null;
	
	public ObstacleJumpa(float _x, float _y, float _z, float _width, float _height, char type, int _FrameUpdateTime, int _numberOfFrames){
		super(_x, _y, _z, _width, _height, type);
		
		jumpSprite = new Sprite(_x, _y, _z, 200, 280, _FrameUpdateTime, _numberOfFrames);
		jumpSprite.loadBitmap(Util.loadBitmapFromAssets("game_obstacle_jump_animated.png"));



		Util.getAppRenderer().addMesh(jumpSprite);
		
	}	
}
