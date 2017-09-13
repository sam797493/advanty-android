package games.adventure.com.advanty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import games.adventure.com.advanty.R;
import games.adventure.com.advanty.Settings;


public class Menuc extends Activity {
	MediaPlayer menuLoop;
	private Toast loadMessage;
	private Runnable gameLauncher;
	private Intent gameIntent;
	private Handler mHandler;
	private android.widget.Button mPlayButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mine3);



		loadMessage = Toast.makeText(getApplicationContext(), "loading game...", Toast.LENGTH_SHORT );
		loadMessage.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);

		gameIntent = new Intent (this, mainc.class);
		mPlayButton = (android.widget.Button)findViewById(R.id.startButton3);
		mPlayButton.setClickable(true);
		mPlayButton.setEnabled(true);
		gameLauncher = new Runnable() {

			public void run() {
				mPlayButton.setClickable(false);
				mPlayButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
				startActivityForResult(gameIntent, 0);
			}
		};

		mHandler = new Handler();



        /*
        menuLoop = MediaPlayer.create(getApplicationContext(), R.raw.menu);
        menuLoop.setLooping(true);
        menuLoop.seekTo(0);
        menuLoop.setVolume(0.5f, 0.5f);
        menuLoop.start();
        */
	}

	public void playGame(View view) {

		// Loading Toast
		loadMessage.show();
		Settings.SHOW_FPS = false;
		mHandler.post(gameLauncher);
	}

	public void playGameWithFPS(View view) {

		// Loading Toast
		loadMessage.show();
		Settings.SHOW_FPS = true;
		mHandler.post(gameLauncher);
	}




	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			showDialog(1);
			mHandler.postDelayed(new Runnable() {

				public void run() {
					mPlayButton.setClickable(true);
					mPlayButton.getBackground().clearColorFilter();
				}
			}, 10000);
		} else {
			mPlayButton.setClickable(true);
			mPlayButton.getBackground().clearColorFilter();
		}

	}



	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setTitle("Error while changing view")
				.setMessage("System needs some time to free memory. Please try again in 10 seconds.")
				.setCancelable(true)
				.create();
	}
}
