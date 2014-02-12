package com.singletongames.vtol;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.preferences.SimplePreferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;

public class MainActivity extends BaseGameActivity {
	private Camera camera;		
	public BaseGameActivity mActivity = this;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new SmoothCamera(0, 0, Resources.CAMERA_WIDTH, Resources.CAMERA_HEIGHT, 800f, 800f, .1f, true);
		//camera = new ZoomCamera(0, 0, Resources.CAMERA_WIDTH, Resources.CAMERA_HEIGHT);		
		EngineOptions opt = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);		
		opt.getRenderOptions().setDithering(true);
		opt.getAudioOptions().setNeedsMusic(true);
		opt.getAudioOptions().setNeedsSound(true);		
		opt.getTouchOptions().setNeedsMultiTouch(true);
		HUD mHud = new HUD();
		camera.setHUD(mHud);
		return opt;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)	throws Exception {
		try {
			Resources.mEngine = mEngine;
			Resources.mActivity = this;
			
			Resources.mNeon = Typeface.createFromAsset(mActivity.getAssets(), "fonts/PolenticalNeon.ttf");
			Resources.mNeonBold = Typeface.createFromAsset(mActivity.getAssets(), "fonts/PolenticalNeonBold.ttf");
			Resources.mFont_Blue48 = Util.GetGradientStrokeFont(48, Util.SimpletonTextColorScheme.GRADIENT_BLUE,Color.BLACK, 2, true, mEngine);
			Resources.mSingletonLogo = Util.GetTextureRegion("gfx/SingletonLogo.png");
		} catch (Exception e) {
			Debug.e(e);
		}
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)	throws Exception {
		final Scene mSplashScene = new Scene();
		
		mSplashScene.setBackgroundEnabled(true);
		Sprite sLogo = new Sprite(camera.getWidth()/2 - Resources.mSingletonLogo.getWidth()/2,camera.getHeight()/2 - Resources.mSingletonLogo.getHeight()/2,Resources.mSingletonLogo,mEngine.getVertexBufferObjectManager());
		
		Text t = new Text(0, 0, Resources.mFont_Blue48, "LOADING...", mEngine.getVertexBufferObjectManager());
		t.setPosition(camera.getWidth() - t.getWidth() - 5,	camera.getHeight() - t.getHeight() - 5);
		
		mSplashScene.attachChild(t);
		mSplashScene.attachChild(sLogo);
		pOnCreateSceneCallback.onCreateSceneFinished(mSplashScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		mEngine.registerUpdateHandler(new TimerHandler(.01f, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				try {
					Resources.Load(mActivity);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//mEngine.setScene(new MainMenuScene(mEngine));
				mEngine.setScene(new MainMenuScene());
			}
		}));
				
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
    public void onResumeGame() {
        super.onResumeGame();
        if (this.mEngine != null && !this.mEngine.isRunning()) 
        {
            this.mEngine.start();
        }
        
        if (Resources.ClearPrefernces){
        	SimplePreferences.getInstance(this).edit().clear().commit();        	
        }
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();
        SimplePreferences.getInstance(mActivity).edit().putInt("TrainingProgress", Resources.TrainingProgress);
        ((IGameScene) this.mEngine.getScene()).Pause();
        this.mEngine.stop();        
    }
    
    @Override                                                                
    public boolean onKeyDown(int keyCode, KeyEvent event) {                  
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	((IGameScene) this.mEngine.getScene()).Back();
            return true;                                                 
        }                                                                    
        return super.onKeyDown(keyCode, event);                              
    }    

}
