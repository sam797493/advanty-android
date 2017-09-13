package games.adventure.com.advanty;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import games.adventure.com.advanty.R;

import java.util.HashMap;

public class SoundManager {
	 
	static private SoundManager _instance;
	private static SoundPool mSoundPool;
	private static HashMap<Integer, Integer> mSoundPoolMap;
	private static AudioManager  mAudioManager;
	private static Context mContext;
 
	private SoundManager()
	{
	}

	static synchronized public SoundManager getInstance()
	{
	    if (_instance == null)
	      _instance = new SoundManager();
	    return _instance;
	 }

	public static  void initSounds(Context theContext)
	{
		 mContext = theContext;
	     mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	     mSoundPoolMap = new HashMap<Integer, Integer>();
	     mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	} 
 

	public static void addSound(int Index,int SoundID)
	{
		mSoundPoolMap.put(Index, mSoundPool.load(mContext, SoundID, 1));
	}

	public static void loadSounds()
	{

		mSoundPoolMap.put(3, mSoundPool.load(mContext, R.raw.jump, 1));
		mSoundPoolMap.put(4, mSoundPool.load(mContext, R.raw.save, 1));
		mSoundPoolMap.put(5, mSoundPool.load(mContext, R.raw.slow , 1));
		mSoundPoolMap.put(6, mSoundPool.load(mContext, R.raw.trampoline, 1));
		mSoundPoolMap.put(7, mSoundPool.load(mContext, R.raw.petenicesplash , 1));
		mSoundPoolMap.put(8, mSoundPool.load(mContext, R.raw.bonus, 1));
		mSoundPoolMap.put(9, mSoundPool.load(mContext, R.raw.ninek, 1));
		
	}
 
	
	public static void playSound(int index,float speed, float volumeL, float volumeR, int loopMode)
	{
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	     if(mSoundPoolMap.get(index)!=null)
	    	 mSoundPool.play(mSoundPoolMap.get(index), streamVolume*volumeL, streamVolume*volumeR, 1, loopMode, speed);		
	}

	public static void playSound(int index,float speed)
	{
		playSound(index, speed, 1.0f, 1.0f, 0);
	}
 

	public static void stopSound(int index)
	{
		mSoundPool.stop(mSoundPoolMap.get(index));
	}
 

	public static void cleanup()
	{
		if (mSoundPool != null) mSoundPool.release();
		mSoundPool = null;
		if (mSoundPoolMap != null) mSoundPoolMap.clear();
		if (mAudioManager != null) mAudioManager.unloadSoundEffects();
	    _instance = null;
 
	}
 
}