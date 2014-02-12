package com.singletongames.vtol;


import java.util.ArrayList;
import java.util.List;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseExponentialOut;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.modifier.ease.EaseStrongOut;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class LanderScene extends GameScene implements SensorEventListener {
	private Scene mThis = this;
	private HUD mHud;
	private Sprite throttleButton;
	float throttleBackgroundPadding = 10f;
	float currentThrottle = 0f;
	float throttleStartingPosition;
	float throttleMaxPosition;
	Sprite throttleBackground;
	boolean movingThrottle = false;
	Text throttlePercent;
	Rectangle fuelLevel;
	ColorModifier fuelColorModifier;
	boolean paused = false;
	protected PinchZoomDetector mPinchZoomDetector;
	private boolean mPreview;
	private ButtonSprite pauseButton;
	private Sprite fuelGaugeBackground;
	private Sprite fuelGaugeOverlay;
	private int fireworksCount;
	private List<ILanderSceneListener> listeners = new ArrayList<ILanderSceneListener>();
	ObjectiveManager objMgr;
	int chapterID, levelID;
	
	private Lander currentLander; 
	
	public LanderScene(boolean preview, int chapterID, int levelID){
		this.chapterID = chapterID;
		this.levelID = levelID;
		
		Resources.mEngine.registerUpdateHandler(new FPSLogger(5));
		
		Util.ResetCamera((SmoothCamera) Resources.mEngine.getCamera());
		mHud = Util.NewHud(Resources.mEngine.getCamera());
		mPreview = preview;
		
		Resources.mCurrentLevel = LevelDB.getInstance().getLevel(chapterID, levelID);
		
		TimerHandler delay = new TimerHandler(1f, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				Load();
			}
		});
		this.registerUpdateHandler(delay);
	}
	
	protected void Load() {
		Util.InitializePhysicsWorld(Resources.mEngine, new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		Resources.mPhysicsWorld.setContactListener(GameContactListener.getInstance());
		
		Resources.mEngine.registerUpdateHandler(new IUpdateHandler() {			
			@Override
			public void reset() {
			}			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (Resources.mCurrentLevel != null && Resources.mCurrentLevel.getLander() != null){
                	float fuelPct = Resources.mCurrentLevel.getLander().getCurrentFuelPercentage();
                	updateFuel(fuelPct);
                }
			}
		});
		
		mHud.setTouchAreaBindingOnActionDownEnabled(true);
		mHud.setTouchAreaBindingOnActionMoveEnabled(true);
		
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		
		this.setOnSceneTouchListener(new IOnSceneTouchListener() {			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
//				mScrollDetector.setEnabled(true);
//                mScrollDetector.onTouchEvent(pSceneTouchEvent);
	
                if (mPreview && Resources.mCurrentLevel != null && Resources.mCurrentLevel.getPreview() != null){
                	Resources.mCurrentLevel.getPreview().SkipToEnd();
                }
				return true;
			}
		});
//		this.mScrollDetector = new SurfaceScrollDetector(new IScrollDetectorListener() {
//			@Override
//			public void onScroll(ScrollDetector pScollDetector, int pPointerID,float pDistanceX, float pDistanceY) {
//				final float zoomFactor = ((ZoomCamera) Resources.mEngine.getCamera()).getZoomFactor();
//				Resources.mEngine.getCamera().offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
//			}
//			@Override
//			public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
//				((SmoothCamera) Resources.mEngine.getCamera()).setEasingEnabled(false);
//				((SmoothCamera) Resources.mEngine.getCamera()).setMaxVelocity(10000f, 10000f);
//			}
//			@Override
//			public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
//				((SmoothCamera) Resources.mEngine.getCamera()).setEasingEnabled(true);
//				((SmoothCamera) Resources.mEngine.getCamera()).setMaxVelocity(400f, 400f);
//			}
//		});
//		
		SensorManager sensorManager = (SensorManager)Resources.mActivity.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                        
        throttleBackground = new Sprite(30, Resources.CAMERA_HEIGHT - 30 - Resources.mThrottleBackground.getHeight(), Resources.mThrottleBackground, Resources.mEngine.getVertexBufferObjectManager());
        throttleStartingPosition = throttleBackground.getY() + throttleBackground.getHeight() - throttleBackgroundPadding - Resources.mThrottleButton.getHeight();
        throttleMaxPosition = throttleBackground.getY() + throttleBackgroundPadding;
        throttleButton = new Sprite(throttleBackground.getX() + throttleBackground.getWidth()/2 - Resources.mThrottleButton.getWidth()/2, throttleStartingPosition, Resources.mThrottleButton, Resources.mEngine.getVertexBufferObjectManager()){
			private float throttleButtonTouchY;
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (!paused){
					if (pSceneTouchEvent.isActionDown()){
						movingThrottle = true;	
						throttleButtonTouchY = pSceneTouchEvent.getY();
					}
					else if (pSceneTouchEvent.isActionUp()){
						movingThrottle = false;
						throttleButton.setPosition(throttleButton.getX(), throttleStartingPosition);
					}
					else if (pSceneTouchEvent.isActionMove()){
						if (movingThrottle){
							float moveAmount = throttleButtonTouchY - pSceneTouchEvent.getY();							
							throttleButton.setPosition(throttleButton.getX(), throttleButton.getY() - moveAmount);
							if (throttleButton.getY() < throttleMaxPosition){
								throttleButton.setPosition(throttleButton.getX(), throttleMaxPosition);
							}
							else if (throttleButton.getY() > throttleStartingPosition){
								throttleButton.setPosition(throttleButton.getX(), throttleStartingPosition);
							}
							throttleButtonTouchY = pSceneTouchEvent.getY();
						}
					}
					
					currentThrottle = (throttleStartingPosition - throttleButton.getY()) / (throttleStartingPosition - throttleMaxPosition); //0-1
					throttlePercent.setText(String.valueOf((int)(currentThrottle*100)) + "%");
					throttlePercent.setPosition(throttleButton.getX() + throttleButton.getWidth() + 30, throttleButton.getY() + throttleButton.getHeight()/2 - throttlePercent.getHeight()/2);
					if (Resources.mCurrentLevel != null && Resources.mCurrentLevel.getLander() != null){
						Resources.mCurrentLevel.getLander().setThrottle(currentThrottle);
					}
					return true;
				}
				else{
					return false;
				}
			}
			
		};		
		mHud.attachChild(throttleBackground);
		mHud.attachChild(throttleButton);
		mHud.registerTouchArea(throttleButton);
		
		throttlePercent = new Text(0f, 0f, Resources.mFont_Green48, "0123456789%", 11, Resources.mEngine.getVertexBufferObjectManager());
		throttlePercent.setText(String.valueOf((int)(currentThrottle*100)) + "%");
		throttlePercent.setPosition(throttleButton.getX() + throttleButton.getWidth() + 30, throttleButton.getY() + throttleButton.getHeight()/2 - throttlePercent.getHeight()/2);
		mHud.attachChild(throttlePercent);
		
		fuelGaugeBackground = new Sprite(30, 30, Resources.mFuelGaugeBackground, Resources.mEngine.getVertexBufferObjectManager());
		fuelGaugeOverlay = new Sprite(30, 30, Resources.mFuelGaugeOverlay, Resources.mEngine.getVertexBufferObjectManager());
		fuelLevel = new Rectangle(fuelGaugeBackground.getX() + 3f, fuelGaugeBackground.getY() + 10f, 74f, 300f, Resources.mEngine.getVertexBufferObjectManager());
		fuelLevel.setScaleCenterY(fuelLevel.getHeight());		
		fuelLevel.setColor(Color.GREEN);
		
		mHud.attachChild(fuelGaugeBackground);
		mHud.attachChild(fuelLevel);
		mHud.attachChild(fuelGaugeOverlay);

		pauseButton = new ButtonSprite(0, 0, Resources.PauseButton, Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
				if (!paused){
					Pause();
				}
				else{
					Resume();
				}
			}
		});
		pauseButton.setPosition(Resources.CAMERA_WIDTH - pauseButton.getWidth() - 10, Resources.CAMERA_HEIGHT - pauseButton.getHeight() - 10);
		mHud.attachChild(pauseButton);
		mHud.registerTouchArea(pauseButton);	
		
		if (mPreview){
			setHudElementsVisible(false);
		}
		
        LoadTMXLevel();
		
		objMgr = new ObjectiveManager(this, new Rectangle(Resources.CAMERA_WIDTH - 300, 50, 300, Resources.CAMERA_HEIGHT - 250, Resources.mEngine.getVertexBufferObjectManager()), Resources.mCurrentLevel.getChapterID(), Resources.mCurrentLevel.getLevelID(), new IObjectiveManagerListener() {
			@Override
			public void onAllObjectivesComplete() {
				LevelDB.getInstance().unlockLevel(chapterID, levelID+1); //unlock the next level
				endScene(true);
			}
		});
		this.getHud().attachChild(objMgr);
	}
	
	protected void endScene(boolean success) {
		disableThrottle();
		currentLander.stopEngines();
				
		TimerHandler timer = new TimerHandler(1f, false, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				setHudElementsVisible(false);
				
				Text text1 = new Text(0,0,Resources.mFont_Green96, "MISSION", Resources.mEngine.getVertexBufferObjectManager());
				text1.setPosition(Resources.CAMERA_WIDTH/2 - text1.getWidth()/2, Resources.CAMERA_HEIGHT/2 - text1.getHeight() + 20);
				text1.setScale(0f);				
				text1.setScaleCenterY(text1.getHeight());
				
				ScaleModifier textScale1 = new ScaleModifier(.25f, 0f, 1f, new IEntityModifierListener() {					
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					}					
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						final Text text2 = new Text(0,0,Resources.mFont_Green96, "COMPLETE", Resources.mEngine.getVertexBufferObjectManager());
						text2.setPosition(Resources.CAMERA_WIDTH/2 - text2.getWidth()/2, Resources.CAMERA_HEIGHT/2 - 20);
						text2.setScale(0f);
						text2.setScaleCenterY(0);
						
						ScaleModifier textScale2 = new ScaleModifier(.5f, 0f, 1f, new IEntityModifierListener() {							
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
							}							
							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								ButtonSprite restartbutton = new ButtonSprite(Resources.CAMERA_WIDTH/2 - Resources.RestartButton.getWidth() - 20, text2.getY() + text2.getHeight() + 50, Resources.RestartButton, Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {
									@Override
									public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
										Restart();
									}
								});
								ButtonSprite menubutton = new ButtonSprite(Resources.CAMERA_WIDTH/2 - Resources.MenuButton.getWidth()/2, restartbutton.getY() + restartbutton.getHeight() + 50, Resources.MenuButton, Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {
									@Override
									public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
										MainMenu();
									}
								});	
								ButtonSprite nextbutton = new ButtonSprite(Resources.CAMERA_WIDTH/2 + 20, restartbutton.getY(), Resources.NextButton, Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {
									@Override
									public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
										NextLevel();
									}
								});	
								
								mHud.attachChild(restartbutton);
								mHud.attachChild(menubutton);
								mHud.attachChild(nextbutton);
								mHud.registerTouchArea(nextbutton);
								mHud.registerTouchArea(restartbutton);
								mHud.registerTouchArea(menubutton);
							}
						}, EaseStrongIn.getInstance());
						text2.registerEntityModifier(textScale2);
						mHud.attachChild(text2);
					}
				}, EaseStrongIn.getInstance());
				text1.registerEntityModifier(textScale1);
								
				mHud.attachChild(text1);				
			}
		});
		Resources.mEngine.registerUpdateHandler(timer);
	}

	private void disableThrottle() {
		currentThrottle = 0;		
		throttleButton.setY(throttleStartingPosition);
		mHud.unregisterTouchArea(throttleButton);
	}
	
	private void enableThrottle() {
		currentThrottle = 0;
		throttleButton.setY(throttleStartingPosition);
		mHud.registerTouchArea(throttleButton);
	}

	private void LoadTMXLevel() {
		Resources.mCurrentLevel.Load(this, Resources.DEBUG_DRAW, true, new IScenePreviewListener() {			
			@Override
			public void onFinish() {
				if (Resources.mCurrentLevel.getLander() != null){
					Resources.mCurrentLevel.getLander().FocusCamera();
					Resources.mCurrentLevel.getLander().startEngines();
					setHudElementsVisible(true);
				}
			}
			@Override
			public void onCancel() {
				if (Resources.mCurrentLevel.getLander() != null){
					Resources.mCurrentLevel.getLander().FocusCameraDirect();
					Resources.mCurrentLevel.getLander().startEngines();
					setHudElementsVisible(true);
				}
			}
		});
		currentLander = Resources.mCurrentLevel.getLander();
		
		TMXTiledMap map = Resources.mCurrentLevel.getMap();
		float mapHeight = map.getTileRows() * map.getTileHeight();
		float mapWidth = map.getTileColumns() * map.getTileWidth();		
		((SmoothCamera) Resources.mEngine.getCamera()).setBounds(0,0,mapWidth,mapHeight);
		((SmoothCamera) Resources.mEngine.getCamera()).setBoundsEnabled(true);
		
		for (NoFlyZone noFly: Resources.mCurrentLevel.getNoFlyZones()){
			noFly.addListener(NoFlyListenerManager.getInstance());
		}

		for (ObjectiveZone zone: Resources.mCurrentLevel.getObjectiveZones()){
			zone.addListener(new IObjectiveZoneListener() {				
				@Override
				public void onExit(ObjectiveZone objectiveZone, Body body) {
					if (body.getUserData() != null && body.getUserData().equals("Lander")){
						//Debug.w("DEBUG: Objective Zone onExit: " + objectiveZone.getId());
						for (ILanderSceneListener listener: listeners){
							listener.onObjectiveZoneExit(objectiveZone);
						}
					}
				}				
				@Override
				public void onEnter(ObjectiveZone objectiveZone, Body body) {
					if (body.getUserData() != null && body.getUserData().equals("Lander")){
						//Debug.w("DEBUG: Objective Zone onEnter. Listener count: " + listeners.size());
						for (ILanderSceneListener listener: listeners){
							listener.onObjectiveZoneEnter(objectiveZone);
						}
					}
				}
			});
		}
		
		if (mPreview && Resources.mCurrentLevel.getPreview() != null){
			Resources.mCurrentLevel.getPreview().Start();
		}
		else{
			currentLander.FocusCameraDirect();
			currentLander.startEngines();
			setHudElementsVisible(true);
		}
		
		IPhysicsSpriteListener launchPadListener = new IPhysicsSpriteListener() {			
			@Override
			public void onContact(Fixture fixtureA, Fixture fixtureB) {
				if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("LaunchPad") &&	fixtureB.getUserData() != null && fixtureB.getUserData().equals("LanderBase")){
					for(ILanderSceneListener l: listeners){
						l.onSafeReturn();
					}
				}
			}
		};
		Resources.mCurrentLevel.getLaunchPad().getListeners().add(launchPadListener);
		
		if (Resources.mCurrentLevel.getLandingPad() != null){
			IPhysicsSpriteListener landingPadListener = new IPhysicsSpriteListener() {			
				@Override
				public void onContact(Fixture fixtureA, Fixture fixtureB) {
					if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("LandingPad") &&
							fixtureB.getUserData() != null && fixtureB.getUserData().equals("LanderBase")){
						ShowFireworks(Resources.mCurrentLevel.getLandingPad().getX() + Resources.mCurrentLevel.getLandingPad().getWidth()/2, Resources.mCurrentLevel.getLandingPad().getY() - 200f);
					}
				}
			};
			Resources.mCurrentLevel.getLandingPad().getListeners().add(landingPadListener);
		}
	}
	
	protected void ShowFireworks(final float x, final float y) {
		fireworksCount = 5;
		
		Resources.mEngine.registerUpdateHandler(new TimerHandler(.5f, false, new ITimerCallback() {			
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				int randX = Resources.rand.nextInt(600) - 300;
				int randY = Resources.rand.nextInt(60) - 30;
				
				AnimatedSprite fireworks = new AnimatedSprite(x + randX - Resources.fireworks.getTextureRegion(0).getWidth()/2, y + randY - Resources.fireworks.getTextureRegion(0).getHeight()/2, Resources.fireworks, Resources.mEngine.getVertexBufferObjectManager());
				fireworks.setZIndex(1);
				fireworks.setScale(2f);
				
				mThis.attachChild(fireworks);
				mThis.sortChildren();
				
				fireworks.animate(50, false);
				
				fireworksCount--;
				if (fireworksCount>0){
					pTimerHandler.reset();
				}
			}
		}));		
	}

	private void setHudElementsVisible(boolean visible) {
		throttleBackground.setVisible(visible);
		throttleButton.setVisible(visible);
		if (visible){
			this.registerTouchArea(throttleButton);
		}
		else {
			this.unregisterTouchArea(throttleButton);
		}		
		fuelGaugeBackground.setVisible(visible);
		fuelLevel.setVisible(visible);
		fuelGaugeOverlay.setVisible(visible);
		pauseButton.setVisible(visible);
		throttlePercent.setVisible(visible);
		if (objMgr != null) objMgr.setVisible(visible);
	}

	private void updateFuel(float currentFuelPercent){
		fuelLevel.setScaleY(currentFuelPercent);
		
		if (currentFuelPercent < .75 && currentFuelPercent > .25f){
			if (fuelColorModifier == null || fuelColorModifier.isFinished()){
				fuelColorModifier = new ColorModifier(1f, fuelLevel.getColor(), Color.YELLOW);
				fuelLevel.registerEntityModifier(fuelColorModifier);
			}
		}
		else if (currentFuelPercent >= .75){
			if (fuelColorModifier == null || fuelColorModifier.isFinished()){
				fuelColorModifier = new ColorModifier(1f, fuelLevel.getColor(), Color.GREEN);
				fuelLevel.registerEntityModifier(fuelColorModifier);
			}
		}
		else if (currentFuelPercent <= .25){
			if (fuelColorModifier == null || fuelColorModifier.isFinished()){
				fuelColorModifier = new ColorModifier(1f, fuelLevel.getColor(), Color.RED);
				fuelLevel.registerEntityModifier(fuelColorModifier);
			}
		}
	}
			
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
        		if (!paused){
        			float accellerometerSpeed0 = event.values[0];
	                float accellerometerSpeed1 = event.values[1];
	                float accellerometerSpeed = accellerometerSpeed0;
	                
	                int rotation = Resources.mActivity.getWindowManager().getDefaultDisplay().getRotation();
        			switch (rotation){
	        			case Surface.ROTATION_0:{
	        				accellerometerSpeed = accellerometerSpeed0; 
	        				break;
	        			}
	        			case Surface.ROTATION_90:{
	        				accellerometerSpeed = accellerometerSpeed1 * -1; 
	        				break;
	        			}
	        			case Surface.ROTATION_180:{
	        				accellerometerSpeed = accellerometerSpeed0 * -1;
	        				break;
	        			}
	        			case Surface.ROTATION_270:{
	        				accellerometerSpeed = accellerometerSpeed1;
	        				break;
	        			}
        			}
	                
	                if (Resources.mCurrentLevel != null && Resources.mCurrentLevel.getLander() != null){
	                	//Resources.mCurrentLevel.getLander().setThrusterRatio(accellerometerSpeedX);
	                	Resources.mCurrentLevel.getLander().setAngle(accellerometerSpeed*-9f);
	                }
        		}
                break;
        }
	}

	@Override
	public void Pause() {
		setHudElementsVisible(false);
		Resources.mEngine.unregisterUpdateHandler(Resources.mPhysicsWorld);					
		Resources.mCurrentLevel.getLander().setPaused(true);
		
		PauseScene pauseScene = new PauseScene(new IOnMenuItemClickListener() {						
			@Override
			public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
				switch (pMenuItem.getID()){
					case 1:{
						//main menu button									
						MainMenu();
						break;
					}
					case 2:{
						//resume button
						Resume();
						break;
					}
					case 3:{
						//restart button
						Restart();
						break;
					}
				}							
				return false;
			}
		});	
		Resources.mEngine.getScene().setChildScene(pauseScene, false, true, true);
		
		paused = true;
		
		super.Pause();
	}

	@Override
	public void Resume() {
		setHudElementsVisible(true);
		Resources.mEngine.getScene().clearChildScene();
		Resources.mEngine.registerUpdateHandler(Resources.mPhysicsWorld);
		Resources.mCurrentLevel.getLander().setPaused(false);
		paused = false;
		super.Resume();
	}

	@Override
	public void Back() {
		Pause();
		super.Back();
	}

	public List<ILanderSceneListener> getListeners() {
		return listeners;
	}
	
	public void addListener(ILanderSceneListener listener) {
		this.listeners.add(listener);
	}
	
	public HUD getHud() {
		return mHud;
	}



	
	private void Restart() {
		this.detachChildren();
		this.clearChildScene();
		Resources.mCurrentLevel.dispose();
		Resources.mEngine.setScene(new LanderScene(false, chapterID, levelID));
	}

	private void MainMenu() {
		this.detachChildren();
		this.clearChildScene();
		Resources.mEngine.getCamera().setHUD(null);
		Resources.mCurrentLevel.dispose();
		Resources.mEngine.setScene(new MainMenuScene());
	}
	
	private void NextLevel() {
		this.detachChildren();
		this.clearChildScene();
		Resources.mEngine.getCamera().setHUD(null);
		Resources.mCurrentLevel.dispose();
		if (LevelDB.getInstance().getLevel(this.chapterID, this.levelID+1) != null){			
			Resources.mEngine.setScene(new LanderScene(true, chapterID, levelID + 1));
		}
		else{
			Resources.mEngine.setScene(new LevelSelectScene(this.chapterID));
		}
	}
}
