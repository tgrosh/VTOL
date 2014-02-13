package com.singletongames.vtol;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.background.modifier.ColorBackgroundModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import android.app.AlertDialog;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;

public class MainMenuScene extends GameScene {
	protected SmoothCamera mCamera = (SmoothCamera) Resources.mEngine.getCamera();
	protected Engine mEngine = Resources.mEngine;
	Scene mScene = this;
	
	public MainMenuScene(){
		Util.ResetCamera(mCamera);

		if (Resources.ClearPrefernces){
			Resources.mActivity.runOnUiThread(new Runnable() {			
				@Override
				public void run() {
					AlertDialog.Builder alert = new AlertDialog.Builder(Resources.mActivity);
			        alert.setTitle("Clearing Preferences");
			        alert.setMessage("Preferences have been cleared");
			        alert.setPositiveButton("OK", null);
			        alert.show();	
				}
			});	
		}
		
		TimerHandler delay = new TimerHandler(1f, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				Load();
			}
		});
		this.registerUpdateHandler(delay);
				
			
	}

	protected void Load() {
//		Resources.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
//				
//		Resources.mCurrentLevel = new Level(Resources.mEngine, "0-0", 0, "0-0", false, false, false, 0);
//		Resources.mCurrentLevel.Load(this, Resources.DEBUG_DRAW, false, null);		
//		float mapScale = .25f;
//		Resources.mCurrentLevel.getMap().getTMXLayers().get(0).setScaleCenter(0, 0);
//		Resources.mCurrentLevel.getMap().getTMXLayers().get(0).setScale(mapScale);				
//		mCamera.setCenterDirect((Resources.CAMERA_WIDTH/2), (Resources.CAMERA_HEIGHT/2));
			
		
		Sprite bg = new Sprite(0,0,Resources.MainMenuBackground,Resources.mEngine.getVertexBufferObjectManager());
		bg.setScale(1.1f);
		this.attachChild(bg);
		
		Sprite logo = new Sprite(Resources.CAMERA_WIDTH/2 - Resources.vtol_logo.getWidth()/2, 150, Resources.vtol_logo, mEngine.getVertexBufferObjectManager());
		this.attachChild(logo);
		
		ButtonSprite startButton = new ButtonSprite(0,0,Resources.StartButton, Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {				
				Util.FadeToBlack(mScene, new ChapterSelectScene());
			}
		});		
		startButton.setPosition(Resources.CAMERA_WIDTH/2 - startButton.getWidth()/2, logo.getY() + logo.getHeight() + 100);
		this.attachChild(startButton);
		this.registerTouchArea(startButton);
		
		Util.FadeIn(this);
	}

	@Override
	public void Pause() {
	}

	@Override
	public void Resume() {
	}

	@Override
	public void Back() {
		System.exit(0);		
	}	
}
