package games.adventure.com.advanty;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;

import games.adventure.com.advanty.ParalaxBackground;
import games.adventure.com.advanty.Playera;
import games.adventure.com.advanty.R;
import games.adventure.com.advanty.Settings;
import games.adventure.com.advanty.SoundManager;


public class maina extends Activity {
	PowerManager.WakeLock wakeLock ;

	private static long lastCreationTime = 0;
	private static final int MIN_CREATION_TIMEOUT = 10000;



	//MediaPlayer musicPlayerIntro;
	MediaPlayer musicPlayerLoop;
	boolean MusicLoopStartedForFirstTime = false;

	boolean isRunning = false;
	RunnersHighView mGameView = null;

	private static final int SLEEP_TIME = 300;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		//setContentView(R.layout.main);	 

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "tag");
		wakeLock.acquire();

		SoundManager.getInstance();
		SoundManager.initSounds(this);
		SoundManager.loadSounds();

		musicPlayerLoop = MediaPlayer.create(getApplicationContext(), R.raw.gamebackground);

		musicPlayerLoop.setLooping(true);
		musicPlayerLoop.seekTo(0);
		musicPlayerLoop.setVolume(0.5f, 0.5f);


		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		isRunning = true;
		mGameView = new RunnersHighView(getApplicationContext());
		setContentView(mGameView);
	}

	@Override
	protected void onDestroy() {
		if(Settings.RHDEBUG)
			Log.d("debug", "onDestroy main");
		isRunning = false;

		wakeLock.release();
		musicPlayerLoop.release();
		SoundManager.cleanup();
		if (mGameView != null) mGameView.cleanup();
		System.gc();
		super.onDestroy();
	}
	@Override
	public void onResume() {
		if(Settings.RHDEBUG)
			Log.d("debug", "onResume");
		wakeLock.acquire();
		if(MusicLoopStartedForFirstTime)
			musicPlayerLoop.start();
		super.onResume();

	}
	@Override
	public void onStop() {
		if(Settings.RHDEBUG)
			Log.d("debug", "onStop");


		super.onStop();

	}
	@Override
	public void onRestart() {
		if(Settings.RHDEBUG)
			Log.d("debug", "onRestart");

		super.onRestart();

	}
	@Override
	public void onPause() {
		if(Settings.RHDEBUG)
			Log.d("debug", "onPause");


		wakeLock.release();
		musicPlayerLoop.pause();
		super.onPause();


	}



	public void sleep() {
		sleep(SLEEP_TIME);
	}

	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class RunnersHighView extends GLSurfaceView implements Runnable {
		private Playera player = null;
		private Levela level;
		private ParalaxBackground background;
		private int width;
		private int height;

		private games.adventure.com.advanty.Button resetButton = null;
		private Bitmap resetButtonImg = null;
		private games.adventure.com.advanty.Button saveButton = null;
		private Bitmap saveButtonImg = null;
		private games.adventure.com.advanty.RHDrawable blackRHD = null;
		private Bitmap blackImg = null;
		private games.adventure.com.advanty.RHDrawable gameLoadingRHD = null;
		private Bitmap gameLoadingImg = null;
		private float blackImgAlpha;
		private boolean scoreWasSaved = false;
		private boolean deathSoundPlayed = false;
		private games.adventure.com.advanty.OpenGLRenderer mRenderer = null;
		private games.adventure.com.advanty.CounterGroup mCounterGroup;
		private games.adventure.com.advanty.CounterDigit mCounterDigit1;
		private games.adventure.com.advanty.CounterDigit mCounterDigit2;
		private games.adventure.com.advanty.CounterDigit mCounterDigit3;
		private games.adventure.com.advanty.CounterDigit mCounterDigit4;
		private Bitmap CounterFont = null;;
		private Bitmap CounterYourScoreImg = null;;
		private games.adventure.com.advanty.RHDrawable CounterYourScoreDrawable = null;;;
		public  boolean doUpdateCounter = true;
		private long timeAtLastSecond;
		private int runCycleCounter;




		private Bitmap mHighscoreMarkBitmap = null;
		private games.adventure.com.advanty.RHDrawable mNewHighscore = null;

		private int totalScore = 0;
		private boolean nineKwasplayed = false;
		private boolean gameIsLoading = true;


		public RunnersHighView(Context context) {
			super(context);

			mRenderer = new games.adventure.com.advanty.OpenGLRenderer();
			this.setRenderer(mRenderer);

			games.adventure.com.advanty.Util.getInstance().setAppContext(context);
			games.adventure.com.advanty.Util.getInstance().setAppRenderer(mRenderer);

			Thread rHThread = new Thread(this);
			rHThread.start();

//			
//			initialize();
		}

		public void cleanup() {
			if (saveButtonImg != null) saveButtonImg.recycle();
			if (blackImg != null) blackImg.recycle();
			if (resetButtonImg!= null) resetButtonImg.recycle();
			if (background != null) background.cleanup();
			if (mHighscoreMarkBitmap != null) mHighscoreMarkBitmap.recycle();
			if (level != null) level.cleanup();
			if (player != null) player.cleanup();
		}

		private void initialize() {
			if(Settings.RHDEBUG)
				Log.d("debug", "initialize begin");

			long timeOfInitializationStart = System.currentTimeMillis();
			games.adventure.com.advanty.Util.roundStartTime = System.currentTimeMillis();

			Context context = games.adventure.com.advanty.Util.getAppContext();

			Rect rectgle= new Rect();
			Window window= getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(rectgle);

			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);

			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			width= display.getWidth();
			height= Math.abs(rectgle.top - rectgle.bottom);

			if(Settings.RHDEBUG)
				Log.d("debug", "displaywidth: " + width + ", displayheight: " + height);

			games.adventure.com.advanty.Util.mScreenHeight=height;
			games.adventure.com.advanty.Util.mScreenWidth=width;
			games.adventure.com.advanty.Util.mWidthHeightRatio=width/height;


			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[16*1024];

			gameLoadingImg = games.adventure.com.advanty.Util.loadBitmapFromAssets("game_loading.jpg");
			gameLoadingRHD = new games.adventure.com.advanty.RHDrawable(0, 0, 1, width, height);
			gameLoadingRHD.loadBitmap(gameLoadingImg);
			mRenderer.addMesh(gameLoadingRHD);

			long currentTime = System.currentTimeMillis();

			if (currentTime < lastCreationTime + MIN_CREATION_TIMEOUT) {
				long sleeptime = MIN_CREATION_TIMEOUT - (currentTime - lastCreationTime);
				lastCreationTime = currentTime;
				try {
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {
					e.printStackTrace();
					finish();
				}
			}
			lastCreationTime = System.currentTimeMillis();

			background = new ParalaxBackground(width, height);

			mRenderer.addMesh(background);
			sleep();



			try {

				background.loadLayerFar(games.adventure.com.advanty.Util.loadBitmapFromAssets("game_background_layer_1.png"));
				sleep();
			} catch (OutOfMemoryError oome) {
				System.gc();
				try {
					Thread.sleep(MIN_CREATION_TIMEOUT);

					background.loadLayerFar(
							games.adventure.com.advanty.Util.loadBitmapFromAssets("game_background_layer_1.png"));
					sleep();

				}catch (OutOfMemoryError e) {
					e.printStackTrace();
					setResult(1);
					finish();
				} catch (InterruptedException e) {
					setResult(0);
					finish();
				}
			}

			try {
				background.loadLayerMiddle(games.adventure.com.advanty.Util.loadBitmapFromAssets("bg2.png"));
				sleep();
			} catch (OutOfMemoryError oome) {
				System.gc();
				try {
					Thread.sleep(MIN_CREATION_TIMEOUT);
					background.loadLayerMiddle(games.adventure.com.advanty.Util.loadBitmapFromAssets("bg2.png"));
					sleep();

				}catch (OutOfMemoryError e) {
					e.printStackTrace();
					setResult(1);
					finish();
				} catch (InterruptedException e) {
					setResult(0);
					finish();
				}
			}
			try {
				background.loadLayerNear(games.adventure.com.advanty.Util.loadBitmapFromAssets("bg2.png"));
				sleep();

			} catch (OutOfMemoryError oome) {
				System.gc();
				try {
					Thread.sleep(MIN_CREATION_TIMEOUT);
					background.loadLayerNear(games.adventure.com.advanty.Util.loadBitmapFromAssets("bg2.png"));
					sleep();

				}catch (OutOfMemoryError e) {
					e.printStackTrace();
					setResult(1);
					finish();
				} catch (InterruptedException e) {
					setResult(0);
					finish();
				}
			}


			if(Settings.RHDEBUG)
				Log.d("debug", "before addMesh");


			resetButtonImg = games.adventure.com.advanty.Util.loadBitmapFromAssets("game_button_play_again.png");
			resetButton = new games.adventure.com.advanty.Button(
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(72),
					height- games.adventure.com.advanty.Util.getPercentOfScreenHeight(18),
					-2,
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(27),
					games.adventure.com.advanty.Util.getPercentOfScreenHeight(17));

			resetButton.loadBitmap(resetButtonImg);
			mRenderer.addMesh(resetButton);

			saveButtonImg = games.adventure.com.advanty.Util.loadBitmapFromAssets("game_button_save.png");
			saveButton = new games.adventure.com.advanty.Button(
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(42),
					height- games.adventure.com.advanty.Util.getPercentOfScreenHeight(18),
					-2,
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(27),
					games.adventure.com.advanty.Util.getPercentOfScreenHeight(17));
			saveButton.loadBitmap(saveButtonImg);
			mRenderer.addMesh(saveButton);


			player = new Playera(getApplicationContext(), mRenderer, height);
			sleep();

			level = new Levela(context, mRenderer, width, height);
			sleep();



			if(Settings.RHDEBUG)
				Log.d("debug", "after player creation");
//			loadingDialog = new ProgressDialog( context );
//		    loadingDialog.setProgressStyle(0);
//		    loadingDialog.setMessage("Loading Highscore ...");




			if(Settings.RHDEBUG)
				Log.d("debug", "after loading messages");



			if(Settings.RHDEBUG)
				Log.d("debug", "after HighscoreAdapter");

			//new counter
			CounterYourScoreImg = games.adventure.com.advanty.Util.loadBitmapFromAssets("game_background_score.png");
			CounterYourScoreDrawable = new games.adventure.com.advanty.RHDrawable(
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(5),
					height- games.adventure.com.advanty.Util.getPercentOfScreenHeight(19),
					0.9f,
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(28),
					games.adventure.com.advanty.Util.getPercentOfScreenHeight(19));

			CounterYourScoreDrawable.loadBitmap(CounterYourScoreImg);
			mRenderer.addMesh(CounterYourScoreDrawable);

			if(Settings.RHDEBUG)
				Log.d("debug", "after CounterYourScoreDrawable addMesh");

			CounterFont = games.adventure.com.advanty.Util.loadBitmapFromAssets("game_numberfont.png");
			mCounterGroup = new games.adventure.com.advanty.CounterGroup(
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(14),
					height- games.adventure.com.advanty.Util.getPercentOfScreenHeight(13.5f),
					0.9f, games.adventure.com.advanty.Util.getPercentOfScreenWidth(16),
					games.adventure.com.advanty.Util.getPercentOfScreenHeight(5),
					25);


			if(Settings.RHDEBUG)
				Log.d("debug", "after mCounterGroup");



			mCounterDigit1 = new games.adventure.com.advanty.CounterDigit(
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(19),
					height- games.adventure.com.advanty.Util.getPercentOfScreenHeight(13.5f),
					0.9f,
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(3),
					games.adventure.com.advanty.Util.getPercentOfScreenHeight(6));
			mCounterDigit1.loadBitmap(CounterFont);
			mCounterGroup.add(mCounterDigit1);

			mCounterDigit2 = new games.adventure.com.advanty.CounterDigit(
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(22),
					height- games.adventure.com.advanty.Util.getPercentOfScreenHeight(13.5f),
					0.9f,
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(3),
					games.adventure.com.advanty.Util.getPercentOfScreenHeight(6));
			mCounterDigit2.loadBitmap(CounterFont);
			mCounterGroup.add(mCounterDigit2);

			mCounterDigit3 = new games.adventure.com.advanty.CounterDigit(
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(25),
					height- games.adventure.com.advanty.Util.getPercentOfScreenHeight(13.5f),
					0.9f,
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(3),
					games.adventure.com.advanty.Util.getPercentOfScreenHeight(6));
			mCounterDigit3.loadBitmap(CounterFont);
			mCounterGroup.add(mCounterDigit3);

			mCounterDigit4 = new games.adventure.com.advanty.CounterDigit(
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(28),
					height- games.adventure.com.advanty.Util.getPercentOfScreenHeight(13.5f),
					0.9f,
					games.adventure.com.advanty.Util.getPercentOfScreenWidth(3),
					games.adventure.com.advanty.Util.getPercentOfScreenHeight(6));

			mCounterDigit4.loadBitmap(CounterFont);
			mCounterGroup.add(mCounterDigit4);

			mRenderer.addMesh(mCounterGroup);
			sleep();

			if(Settings.RHDEBUG)
				Log.d("debug", "after counter");


			blackImg = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);
			blackRHD = new games.adventure.com.advanty.RHDrawable(0, 0, 1, width, height);
			blackImgAlpha=1;
			blackRHD.setColor(0, 0, 0, blackImgAlpha);
			blackRHD.loadBitmap(blackImg);
			mRenderer.addMesh(blackRHD);

			gameLoadingRHD.z = -1.0f;


			mHighscoreMarkBitmap = games.adventure.com.advanty.Util.loadBitmapFromAssets("game_highscoremark.png");

			mNewHighscore = new games.adventure.com.advanty.RHDrawable(width/2 - 128, height/2 - 64, -2, 256, 128);
			mNewHighscore.loadBitmap(games.adventure.com.advanty.Util.loadBitmapFromAssets("game_new_highscore.png"));
			mRenderer.addMesh(mNewHighscore);




			//give the player time to read loading screen and controls
			while(System.currentTimeMillis() < timeOfInitializationStart+Settings.TimeForLoadingScreenToBeVisible)
				sleep(10);

			timeAtLastSecond = System.currentTimeMillis();
			runCycleCounter=0;




			if(Settings.RHDEBUG)
				Log.d("debug", "RunnersHighView initiation ended");
		}



		@SuppressWarnings("unused")
		public void run() {

			if(Settings.RHDEBUG)
				Log.d("debug", "run method started");

			// wait until the intro is over
			// this gives the app enough time to load
			try{
				//loadingDialog.show();
				if(Settings.RHDEBUG)
					Log.d("debug", "run method in try");
				if(Settings.RHDEBUG)
					Log.d("debug", "mRenderer.firstFrameDone: " + mRenderer.firstFrameDone);

				while(!mRenderer.firstFrameDone)
					Thread.sleep(10);

				initialize();


				long timeAtStart = System.currentTimeMillis();
				while (System.currentTimeMillis() < timeAtStart + 2000 && isRunning)
				{
					blackImgAlpha-=0.005;
					blackRHD.setColor(0, 0, 0, blackImgAlpha);
					Thread.sleep(10);
				}


				blackImg.recycle();
				gameLoadingImg.recycle();

				blackRHD.shouldBeDrawn = false;
				gameLoadingRHD.shouldBeDrawn = false;

				mRenderer.removeMesh(blackRHD);
				mRenderer.removeMesh(gameLoadingRHD);

				if(Settings.RHDEBUG)
					Log.d("debug", "after fade in");

				try {
					if(isRunning && !musicPlayerLoop.isPlaying())
						musicPlayerLoop.start();

				} catch (IllegalStateException e) {
					e.printStackTrace();
					Log.w(Settings.LOG_TAG, "seems like you startet the game more" +
							" than once in a few seconds or canceld the game start");
					Log.w(Settings.LOG_TAG, "PLEASE DO NOT DO THIS UNLESS IT IS A STRESS TEST");
					return;
				}

				MusicLoopStartedForFirstTime=true;

			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return;
			}

			if(Settings.RHDEBUG)
				Log.d("debug", "run method after try catch");

			blackRHD.z=-1.0f;
			blackRHD.setColor(0, 0, 0, 0);
			//mRenderer.removeMesh(blackRHD); //TODO: find a way to remove mesh without runtime errors

			long timeForOneCycle=0;
			long currentTimeTaken=0;
			long starttime = 0;

			gameIsLoading = false;

			if(Settings.RHDEBUG)
				Log.d("debug", "run method befor while");
			//			long debugTime = System.currentTimeMillis(); // FIXME DEBUG TIME FOR VIDEO CAPTURE
			games.adventure.com.advanty.Util.roundStartTime = System.currentTimeMillis();

			while(isRunning){

				starttime = System.currentTimeMillis();

//				if (debugTime + 15000 < starttime) sleep(100); // FIXME DEBUG TIME FOR VIDEO CAPTURE

//				level.update(); // FIXME remove this line

				player.playerSprite.setFrameUpdateTime(
						(level.baseSpeedMax+level.extraSpeedMax)*10 -
								((level.baseSpeed+level.extraSpeed)*10) +
								60 );
				if (player.update()) {
					if(Settings.RHDEBUG){
						currentTimeTaken = System.currentTimeMillis()- starttime;
						Log.d("runtime", "time after player update: " + Integer.toString((int)currentTimeTaken));
					}
					level.update();
					if(Settings.RHDEBUG){
						currentTimeTaken = System.currentTimeMillis()- starttime;
						Log.d("runtime", "time after level update: " + Integer.toString((int)currentTimeTaken));
					}
					background.update();

					if(Settings.RHDEBUG){
						currentTimeTaken = System.currentTimeMillis()- starttime;
						Log.d("runtime", "time after background update: " + Integer.toString((int)currentTimeTaken));
					}



				} else {
					if(player.y < 0){
						doUpdateCounter=false;
						resetButton.setShowButton(true);
						resetButton.z = 1.0f;
						saveButton.setShowButton(true);
						saveButton.z = 1.0f;
						if(!deathSoundPlayed){
							SoundManager.playSound(7, 1, 0.5f, 0.5f, 0);
							deathSoundPlayed=true;

							System.gc(); //do garbage collection
						}

					}
				}

				if(player.collidedWithObstacle(level.getLevelPosition()) ){
					level.lowerSpeed();
				}

				if(doUpdateCounter)
				{
					totalScore = level.getDistanceScore() + player.getBonusScore();
					if (Settings.SHOW_FPS) mCounterGroup.tryToSetCounterTo(mRenderer.fps);
					else mCounterGroup.tryToSetCounterTo(totalScore);

					if(totalScore>=9000 && nineKwasplayed==false)
					{
						nineKwasplayed=true;
						SoundManager.playSound(9, 1, 1000, 1000, 0);
					}
				}


				if(Settings.RHDEBUG){
					timeForOneCycle= System.currentTimeMillis()- starttime;
					Log.d("runtime", "time after counter update: " + Integer.toString((int)timeForOneCycle));
				}
				timeForOneCycle= System.currentTimeMillis()- starttime;

				if(timeForOneCycle < 10) {
					try{ Thread.sleep(10-timeForOneCycle); }
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				runCycleCounter++;

				if(Settings.RHDEBUG) {
					currentTimeTaken = System.currentTimeMillis()- starttime;
					Log.d("runtime", "time after thread sleep : " + Integer.toString((int)currentTimeTaken));
				}

				timeForOneCycle= System.currentTimeMillis()- starttime;
				if((System.currentTimeMillis() - timeAtLastSecond) > 1000 && Settings.RHDEBUG)
				{
					timeAtLastSecond = System.currentTimeMillis();
					Log.d("runtime", "run cycles per second: " + Integer.toString(runCycleCounter));
					runCycleCounter=0;
				}
				if(Settings.RHDEBUG){
					timeForOneCycle= System.currentTimeMillis()- starttime;
					Log.d("runtime", "overall time for this run: " + Integer.toString((int)timeForOneCycle));
				}
			}

			if(Settings.RHDEBUG)
				Log.d("debug", "run method ended");

		}








		public boolean onTouchEvent(MotionEvent event) {
			if(!gameIsLoading){
				if(event.getAction() == MotionEvent.ACTION_UP)
					player.setJump(false);

				else if(event.getAction() == MotionEvent.ACTION_DOWN){
					if (resetButton.getShowButton() || saveButton.getShowButton()) {
						if(resetButton.isClicked( event.getX(), games.adventure.com.advanty.Util.getInstance().toScreenY((int)event.getY()) ) ){
							System.gc(); //do garbage collection
							player.reset();
							level.reset();
							resetButton.setShowButton(false);
							resetButton.z = -2.0f;
							saveButton.setShowButton(false);
							saveButton.z = -2.0f;
							saveButton.x = saveButton.lastX;
							mCounterGroup.resetCounter();
							scoreWasSaved=false;
							deathSoundPlayed=false;
							SoundManager.playSound(1, 1);
							doUpdateCounter=true;



							nineKwasplayed = false;
							totalScore = 0;
							games.adventure.com.advanty.Util.roundStartTime = System.currentTimeMillis();
						}
						else if(saveButton.isClicked( event.getX(), games.adventure.com.advanty.Util.getInstance().toScreenY((int)event.getY())  ) && !scoreWasSaved){
							//save score
							saveButton.setShowButton(false);
							saveButton.z = -2.0f;
							saveButton.lastX = saveButton.x;
							saveButton.x = -5000;



							//play save sound
							SoundManager.playSound(4, 1);
							scoreWasSaved=true;
						}
					}
					else {
						player.setJump(true);
					}
				}
			}

			return true;
		}
	}
}
