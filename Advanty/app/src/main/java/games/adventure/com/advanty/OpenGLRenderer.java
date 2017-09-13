package games.adventure.com.advanty;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import games.adventure.com.advanty.Settings;

public class OpenGLRenderer implements Renderer {
	private final Group root;
	private long timeAtLastSecond = 0;
	private long currentTimeTaken=0;
	private long starttime = 0;
	private int fpsCounter;
	public int fps = 0;

	public boolean firstFrameDone = false;

	public OpenGLRenderer() {
		// Initialize our root.
		Group group = new Group();
		root = group;
	}


	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);


		gl.glDisable(GL10.GL_DITHER);



		gl.glShadeModel(GL10.GL_SMOOTH);

		gl.glClearDepthf(1.0f);

		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

		timeAtLastSecond = System.currentTimeMillis();
		fpsCounter=0;
	}



	public void onDrawFrame(GL10 gl) {
		if(Settings.RHDEBUG){
			starttime = System.currentTimeMillis();
			currentTimeTaken= System.currentTimeMillis()- starttime;
			Log.d("frametime", "time at beginning: " + Integer.toString((int)currentTimeTaken));
		}

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();

		if(Settings.RHDEBUG){
			currentTimeTaken= System.currentTimeMillis()- starttime;
			Log.d("frametime", "time after clear and loadident: " + Integer.toString((int)currentTimeTaken));
		}

		// Draw our scene.
		synchronized (root) {
			root.draw(gl);
		}
		fpsCounter++;


		if(Settings.RHDEBUG){
			currentTimeTaken= System.currentTimeMillis()- starttime;
			Log.d("frametime", "time after draw: " + Integer.toString((int)currentTimeTaken));
		}

		if((System.currentTimeMillis() - timeAtLastSecond) > 1000){
			timeAtLastSecond = System.currentTimeMillis();
			fps = fpsCounter;
			fpsCounter=0;
			if(Settings.RHDEBUG) {
				Log.d("framerate", "draws per second: " + Integer.toString(fpsCounter));
			}
		}

		firstFrameDone = true;
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Sets the current view port to the new size.

		// gl.glViewport(0, 0, width, height);
		// GLU.gluOrtho2D(gl, 0, width, 0, height);
		if(Settings.RHDEBUG)
			Log.d("frametime", "onSurfaceChanged called");
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		//GLU.gluOrtho2D(gl, 0, 1, 0, 2);
		GLU.gluOrtho2D(gl, 0, width, 0, height);
		//gl.glOrthox(0, width, 0, height, -5, 5);
		// gl.glOrthox(0, width, 0, height, -100, 100);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();



	}


	public void addMesh(Mesh mesh) {
		synchronized (root) {
			root.add(mesh);
		}
	}

	public void removeMesh(Mesh mesh) {
		synchronized (root) {
			root.remove(mesh);
		}
	}
}
