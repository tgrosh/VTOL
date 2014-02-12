package com.singletongames.vtol;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;


public class ChapterSelectScene extends GameScene {
	private Scene mthis = this;
	private HUD mHud;
	
	public ChapterSelectScene() {
		Load();
	}

	private void Load() {
//		Util.ResetCamera((SmoothCamera) Resources.mEngine.getCamera());
//		mHud = Util.NewHud(Resources.mEngine.getCamera());
//		
//		Resources.mCurrentLevel = new Level(Resources.mEngine, "0-0", 0, "0-0", false, false, false, 0);
//		Resources.mCurrentLevel.Load(this, Resources.DEBUG_DRAW, false, null);		
//		float mapScale = .25f;
//		Resources.mCurrentLevel.getMap().getTMXLayers().get(0).setScaleCenter(0, 0);
//		Resources.mCurrentLevel.getMap().getTMXLayers().get(0).setScale(mapScale);				
//		((SmoothCamera) Resources.mEngine.getCamera()).setCenterDirect((Resources.CAMERA_WIDTH/2), (Resources.CAMERA_HEIGHT/2));
		
		Sprite bg = new Sprite(0,0,Resources.MainMenuBackground,Resources.mEngine.getVertexBufferObjectManager());
		bg.setScale(1.1f);
		this.attachChild(bg);
		
		Sprite titleBar = new Sprite(Resources.CAMERA_WIDTH/2 - Resources.titleBar.getWidth()/2, 0, Resources.titleBar, Resources.mEngine.getVertexBufferObjectManager());
		this.attachChild(titleBar);
		
		Text title = new Text(0,0,Resources.mFont_Yellow24, "Chapter Select", Resources.mEngine.getVertexBufferObjectManager());
		title.setPosition(titleBar.getWidth()/2 - title.getWidth()/2, titleBar.getHeight()/2 - title.getHeight()/2 - 2);
		titleBar.attachChild(title);
		
		ButtonSprite backButton = new ButtonSprite(20, 20, Resources.BackButton, Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {				
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
				Util.FadeToBlack(mthis, new MainMenuScene());
			}
		});
		this.registerTouchArea(backButton);
		this.attachChild(backButton);
		
		int numCols = 4;
		int leftPadding = 120;
		int rightPadding = 120;
		int topPadding = 180;
		int bottomPadding = 180;
		
		int columnSize = (Resources.CAMERA_WIDTH - leftPadding - rightPadding) / numCols;
		int numRows = 3; //(ChapterDB.getInstance().allChapters().size() / numCols);
//		if (ChapterDB.getInstance().allChapters().size() % numCols > 0){
//			numRows++;
//		}
		int rowSize = (Resources.CAMERA_HEIGHT - topPadding - bottomPadding) / numRows;
		
		for (final Chapter chapter:ChapterDB.getInstance().allChapters()){
			int columnIndex = chapter.getId()%numCols;
			int rowIndex = chapter.getId()/numCols;
			
			ButtonSprite icon = new ButtonSprite(
					leftPadding + (columnIndex*columnSize) + columnSize/2 - Resources.chapterIcons.get(chapter.getId()).getWidth()/2, 
					topPadding + (rowIndex*rowSize)  + rowSize/2 - Resources.chapterIcons.get(chapter.getId()).getHeight()/2, Resources.chapterIcons.get(chapter.getId()), Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {				
				@Override
				public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {					
					//Resources.mArcadeButton1.play();
					Util.FadeToBlack(mthis, new LevelSelectScene(chapter.getId()));
				}
			});
			icon.setUserData(chapter);
			icon.setEnabled(!chapter.isLocked());
			
			Text t = new Text(0, 0, Resources.mFont_Blue36, chapter.getName(), Resources.mEngine.getVertexBufferObjectManager());
			t.setPosition(icon.getWidth()/2 - t.getWidth()/2, icon.getHeight() + 10);
			icon.attachChild(t);
			
			this.registerTouchArea(icon);
			this.attachChild(icon);
		}
		
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
		Resources.mEngine.runOnUpdateThread(new Runnable() {			
			@Override
			public void run() {
				mthis.clearUpdateHandlers();
				mthis.detachChildren();				
				Util.FadeToBlack(mthis , new MainMenuScene());				
			}
		});		
	}
		
}
