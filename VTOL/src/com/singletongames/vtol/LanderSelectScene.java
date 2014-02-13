package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseElasticIn;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.util.modifier.ease.EaseSineIn;
import org.andengine.util.modifier.ease.EaseSineOut;


public class LanderSelectScene extends GameScene {
	private Scene mThis = this;
	
	List<LanderInfo> landers = LanderDB.getInstance().allLanders();
	int mCurrentLanderIndex = 0;
	private AnimatedSprite sInfoProgress1;
	private AnimatedSprite sInfoProgress2;
	private AnimatedSprite sInfoProgress3;
	private AnimatedSprite sInfoProgress4;
	private Text tName;
	private List<Sprite> landerSprites;
	private Sprite currentLanderSprite;
	private Text tDescription;
	private Sprite sFrame;
	private Rectangle rectTop;
	private Rectangle rectBottom;	
	
	private ILanderSelectListener listener;
	private boolean transitioning = false;

	private Text tLocked;

	private Text tTapToSelect;
	
	public LanderSelectScene(ILanderSelectListener listener) {
		this.listener = listener;
		if (landers != null && landers.size() > 0){
			Load();
		}
	}

	private void Load() {
		Util.ResetCamera((SmoothCamera) Resources.mEngine.getCamera());
				
		setBackgroundEnabled(false);
		
		Rectangle backDrop = new Rectangle(0, 0, Resources.CAMERA_WIDTH, Resources.CAMERA_HEIGHT, Resources.mEngine.getVertexBufferObjectManager());
		backDrop.setColor(Color.BLACK);
		backDrop.setAlpha(.4f);		
		
		sFrame = new Sprite(Resources.CAMERA_WIDTH/2 - Resources.LanderSelectFrame.getWidth()/2, Resources.CAMERA_HEIGHT/2 - Resources.LanderSelectFrame.getHeight()/2, Resources.LanderSelectFrame, Resources.mEngine.getVertexBufferObjectManager());
		sFrame.setScale(.5f);
		sFrame.setAlpha(0f);
		
		Text tCancel = new Text(669, 60, Resources.mFont_Yellow24, "Cancel", Resources.mEngine.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				listener.onCancel();
				return true;
			}
			
		};
		
		tTapToSelect = new Text(0, 0, Resources.mFont_Yellow24, "Tap to Select", Resources.mEngine.getVertexBufferObjectManager());
		tTapToSelect.setPosition(Resources.LanderSelectFrame.getWidth()/2 - tTapToSelect.getWidth()/2, 350);
		tTapToSelect.setVisible(false);
		
		tLocked = new Text(0, 0, Resources.mFont_Red48, "Locked", Resources.mEngine.getVertexBufferObjectManager());
		tLocked.setPosition(Resources.LanderSelectFrame.getWidth()/2 - tLocked.getWidth()/2, 335);
		tLocked.setVisible(false);
		
		Sprite sArrowLeft = new Sprite(45, Resources.LanderSelectFrame.getHeight()/2 - Resources.LanderSelectArrow.getHeight()/2, Resources.LanderSelectArrow, Resources.mEngine.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp() && !transitioning){
					Prev();
				}
				return true;
			}
			
		};
		sArrowLeft.setFlippedHorizontal(true);
		Sprite sArrowRight = new Sprite(Resources.LanderSelectFrame.getWidth() - Resources.LanderSelectArrow.getWidth() - 45, sArrowLeft.getY(), Resources.LanderSelectArrow, Resources.mEngine.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp() && !transitioning){
					Next();
				}
				return true;
			}
			
		};
		
		Sprite sInfoFrameLeft = new Sprite(Resources.LanderSelectFrame.getWidth()/2 - Resources.LanderSelectInfoFrame.getWidth() - 20, 388, Resources.LanderSelectInfoFrame, Resources.mEngine.getVertexBufferObjectManager());
		Sprite sInfoFrameRight = new Sprite(Resources.LanderSelectFrame.getWidth()/2 + 20, sInfoFrameLeft.getY(), Resources.LanderSelectInfoFrame, Resources.mEngine.getVertexBufferObjectManager());
		
		landerSprites = new ArrayList<Sprite>();
		for (int x=0; x<Resources.HighResLanders.size(); x++ ){
			TextureRegion tex = Resources.HighResLanders.get(x);
			
			Sprite s = new Sprite(Resources.LanderSelectFrame.getWidth()/2 - tex.getWidth()/2, 265 - tex.getHeight()/2, tex, Resources.mEngine.getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp() && !transitioning){
						SelectLander((Integer) this.getUserData());
					}
					return true;
				}
			};
			s.setUserData(x);
			
			landerSprites.add(s);
		}
		currentLanderSprite = landerSprites.get(0);		
		
		sInfoProgress1 = new AnimatedSprite(sInfoFrameRight.getWidth()/2 + 5, 13, Resources.LanderSelectInfoProgress, Resources.mEngine.getVertexBufferObjectManager());
		sInfoProgress2 = new AnimatedSprite(sInfoFrameRight.getWidth()/2 + 5, sInfoProgress1.getY() + 25, Resources.LanderSelectInfoProgress, Resources.mEngine.getVertexBufferObjectManager());
		sInfoProgress3 = new AnimatedSprite(sInfoFrameRight.getWidth()/2 + 5, sInfoProgress2.getY() + 25, Resources.LanderSelectInfoProgress, Resources.mEngine.getVertexBufferObjectManager());
		sInfoProgress4 = new AnimatedSprite(sInfoFrameRight.getWidth()/2 + 5, sInfoProgress3.getY() + 25, Resources.LanderSelectInfoProgress, Resources.mEngine.getVertexBufferObjectManager());
		
		tName = new Text(0, 0, Resources.mFont_White48, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", Resources.mEngine.getVertexBufferObjectManager());
		
		rectTop = new Rectangle(tName.getX() - 20, tName.getY() - 1, tName.getWidth() + 40, 8, Resources.mEngine.getVertexBufferObjectManager());
		rectTop.setColor(Color.BLACK);
		rectBottom = new Rectangle(rectTop.getX(), tName.getY() + tName.getHeight() - 2, rectTop.getWidth(), rectTop.getHeight(), Resources.mEngine.getVertexBufferObjectManager());
		rectBottom.setColor(Color.BLACK);
		
		tDescription = new Text(0, 0, Resources.mFont_White18, landers.get(mCurrentLanderIndex).getDescription(), Resources.mEngine.getVertexBufferObjectManager());
		tDescription.setAutoWrap(AutoWrap.WORDS);
		tDescription.setAutoWrapWidth(sInfoFrameLeft.getWidth() - 30);
		tDescription.setPosition(15, 10);
		
		Text attPower = new Text(0, 0, Resources.mFont_White24, "Power", Resources.mEngine.getVertexBufferObjectManager());
		attPower.setPosition(sInfoFrameRight.getWidth()/2 - attPower.getWidth() - 5, 15);
		Text attToughness = new Text(0, 0, Resources.mFont_White24, "Toughness", Resources.mEngine.getVertexBufferObjectManager());
		attToughness.setPosition(sInfoFrameRight.getWidth()/2 - attToughness.getWidth() - 5, attPower.getY() + 25);
		Text attFuel = new Text(0, 0, Resources.mFont_White24, "Fuel", Resources.mEngine.getVertexBufferObjectManager());
		attFuel.setPosition(sInfoFrameRight.getWidth()/2 - attFuel.getWidth() - 5, attToughness.getY() + 25);
		Text attWeight = new Text(0, 0, Resources.mFont_White24, "Weight", Resources.mEngine.getVertexBufferObjectManager());
		attWeight.setPosition(sInfoFrameRight.getWidth()/2 - attWeight.getWidth() - 5, attFuel.getY() + 25);
		
		ShowLanderInfo(mCurrentLanderIndex);
		
		sInfoFrameLeft.attachChild(tDescription);
		
		sInfoFrameRight.attachChild(attPower);
		sInfoFrameRight.attachChild(attToughness);
		sInfoFrameRight.attachChild(attFuel);
		sInfoFrameRight.attachChild(attWeight);
		
		sInfoFrameRight.attachChild(sInfoProgress1);
		sInfoFrameRight.attachChild(sInfoProgress2);
		sInfoFrameRight.attachChild(sInfoProgress3);
		sInfoFrameRight.attachChild(sInfoProgress4);
		
		sFrame.attachChild(tCancel);
		sFrame.attachChild(tTapToSelect);
		sFrame.attachChild(tLocked);
		sFrame.attachChild(sArrowLeft);
		sFrame.attachChild(sArrowRight);
		sFrame.attachChild(sInfoFrameLeft);
		sFrame.attachChild(sInfoFrameRight);
		sFrame.attachChild(tName);
		sFrame.attachChild(rectTop);
		sFrame.attachChild(rectBottom);
		if (landerSprites.size() > 0){
			sFrame.attachChild(currentLanderSprite);
		}
		
		this.attachChild(backDrop);
		this.attachChild(sFrame);
		
		this.registerTouchArea(tCancel);
		this.registerTouchArea(sArrowLeft);
		this.registerTouchArea(sArrowRight);	
		if (landers.get(mCurrentLanderIndex).isLocked()){
			Sprite sLocked = new Sprite(currentLanderSprite.getWidth()/2 - Resources.LanderSelectLocked.getWidth()/2,currentLanderSprite.getHeight()/2 - Resources.LanderSelectLocked.getHeight()/2,Resources.LanderSelectLocked,Resources.mEngine.getVertexBufferObjectManager());
			currentLanderSprite.attachChild(sLocked);
			tLocked.setVisible(true);
			tTapToSelect.setVisible(false);
		}
		else{
			this.registerTouchArea(currentLanderSprite);
			tLocked.setVisible(false);
			tTapToSelect.setVisible(true);
		}
		
		ScaleModifier scaleIn = new ScaleModifier(.5f, .5f, 1f, EaseBackOut.getInstance());
		AlphaModifier alphaIn = new AlphaModifier(.25f, 0f, 1f);
		ParallelEntityModifier par = new ParallelEntityModifier(scaleIn, alphaIn);
		sFrame.registerEntityModifier(par);
	}

	protected void SelectLander(int index) {		
		Resources.selectedLander = index;
		listener.onSelect();
	}

	private void ShowLanderInfo(final int Index){
		tName.setText(landers.get(Index).getName());
		tName.setPosition(Resources.LanderSelectFrame.getWidth()/2 - tName.getWidth()/2, 93);
		rectTop.setPosition(tName.getX() - 20, tName.getY() - 1);
		rectTop.setSize(tName.getWidth() + 40, 8);
		rectBottom.setPosition(rectTop.getX(), tName.getY() + tName.getHeight() - 2);
		rectBottom.setSize(rectTop.getWidth(), rectTop.getHeight());
		
		tDescription.setText(landers.get(Index).getDescription());
		
		sInfoProgress1.setCurrentTileIndex(getPowerRating(landers.get(Index).getMaxEngineThrust()));
		sInfoProgress2.setCurrentTileIndex(getToughnessRating(landers.get(Index).getToughness()));
		sInfoProgress3.setCurrentTileIndex(getFuelRating(landers.get(Index).getMaxFuel()));
		sInfoProgress4.setCurrentTileIndex(getWeightRating(landers.get(Index).getDensity()));
		
		//if current lander sprite is not the one we need to show
		if (!currentLanderSprite.equals(landerSprites.get(Index))){		
			MoveModifier moverOut = new MoveModifier(.2f, currentLanderSprite.getX(), currentLanderSprite.getX(), currentLanderSprite.getY(), currentLanderSprite.getY() + 10f, new IEntityModifierListener() {
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, final IEntity pItem) {
					Resources.mEngine.runOnUpdateThread(new Runnable() {					
						@Override
						public void run() {							
							float newY = currentLanderSprite.getY();
							currentLanderSprite.clearEntityModifiers();
							currentLanderSprite.detachSelf();						
							
							currentLanderSprite = landerSprites.get(Index);
							currentLanderSprite.setPosition(sFrame.getWidth()/2 - currentLanderSprite.getWidth()/2, newY);
							currentLanderSprite.setAlpha(0f);
							sFrame.attachChild(currentLanderSprite);
							if (landers.get(Index).isLocked()){
								Sprite sLocked = new Sprite(currentLanderSprite.getWidth()/2 - Resources.LanderSelectLocked.getWidth()/2,currentLanderSprite.getHeight()/2 - Resources.LanderSelectLocked.getHeight()/2,Resources.LanderSelectLocked,Resources.mEngine.getVertexBufferObjectManager());
								currentLanderSprite.attachChild(sLocked);
								tLocked.setVisible(true);
								tTapToSelect.setVisible(false);
							}
							else{
								mThis.registerTouchArea(currentLanderSprite);
								tLocked.setVisible(false);
								tTapToSelect.setVisible(true);
							}
							
							MoveModifier moverIn = new MoveModifier(.2f, currentLanderSprite.getX(), currentLanderSprite.getX(), currentLanderSprite.getY(), currentLanderSprite.getY() - 10f, new IEntityModifierListener() {
								@Override
								public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
								}								
								@Override
								public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
									transitioning = false;
								}
							}, EaseSineIn.getInstance());
							AlphaModifier alphaIn = new AlphaModifier(.2f, 0f, 1f);
							ParallelEntityModifier parIn = new ParallelEntityModifier(moverIn, alphaIn);
							currentLanderSprite.registerEntityModifier(parIn);
						}
					});
				}
			}, EaseSineOut.getInstance());
			AlphaModifier alphaOut = new AlphaModifier(.2f, 1f, 0f);
			ParallelEntityModifier parOut = new ParallelEntityModifier(moverOut, alphaOut);
			currentLanderSprite.registerEntityModifier(parOut);
		}
	}
	
	private void Next(){
		transitioning = true;
		mCurrentLanderIndex++;
		if (mCurrentLanderIndex > LanderDB.getInstance().allLanders().size()-1){
			mCurrentLanderIndex = 0;
		}
		ShowLanderInfo(mCurrentLanderIndex);
	}
	
	private void Prev(){
		transitioning = true;
		mCurrentLanderIndex--;
		if (mCurrentLanderIndex < 0){
			mCurrentLanderIndex = LanderDB.getInstance().allLanders().size()-1;
		}
		ShowLanderInfo(mCurrentLanderIndex);
	}
	
	private int getWeightRating(float value) {
		float min = .05f;
		float max = .25f;
				
		return (int) getRating(value, min, max);
	}

	private float getRating(float value, float minValue, float maxValue) {
		if (value < minValue) return 0;
		if (value > maxValue) return 10;
		float pct = (value / (maxValue - minValue)) * 10f;
		return pct;
	}

	private int getFuelRating(float value) {
		float min = 150f;
		float max = 500f;
				
		return (int) getRating(value, min, max);
	}

	private int getToughnessRating(float value) {
		float min = 2.5f;
		float max = 12f;
				
		return (int) getRating(value, min, max);
	}

	private int getPowerRating(float value) {
		float min = 10f;
		float max = 40f;
				
		return (int) getRating(value, min, max);
	}

}
