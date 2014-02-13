package com.singletongames.vtol;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.util.preferences.SimplePreferences;

public class LevelSelectScene extends GameScene {
	int chapterID;
	Scene mThis = this;
	
	public LevelSelectScene(int chapterID) {
		this.chapterID = chapterID;
		Util.ResetCamera((SmoothCamera) Resources.mEngine.getCamera());
		
		Load();
	}

	private void Load() {		
		Sprite bg = new Sprite(0,0,Resources.MainMenuBackground,Resources.mEngine.getVertexBufferObjectManager());
		bg.setScale(1.1f);
		this.attachChild(bg);
		
		Sprite titleBar = new Sprite(Resources.CAMERA_WIDTH/2 - Resources.titleBars.get(chapterID).getWidth()/2, 0, Resources.titleBars.get(chapterID), Resources.mEngine.getVertexBufferObjectManager());
		this.attachChild(titleBar);
		
		Text title = new Text(0,0,Resources.mFont_Yellow24, "Level Select", Resources.mEngine.getVertexBufferObjectManager());
		title.setPosition(titleBar.getWidth()/2 - title.getWidth()/2, titleBar.getHeight()/2 - title.getHeight()/2 - 2);
		titleBar.attachChild(title);
		
		ButtonSprite backButton = new ButtonSprite(20, 20, Resources.BackButton, Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {				
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
				Util.FadeToBlack(mThis, new ChapterSelectScene());
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
		
		for (final Level lvl:LevelDB.getInstance().getLevels(chapterID)){
			int columnIndex = (lvl.getLevelID()-1)%numCols;
			int rowIndex = (lvl.getLevelID()-1)/numCols;
			ButtonSprite icon = new ButtonSprite(
					leftPadding + (columnIndex*columnSize) + columnSize/2 - Resources.chapterIcons.get(lvl.getChapterID()).getWidth()/2, 
					topPadding + (rowIndex*rowSize)  + rowSize/2 - Resources.chapterIcons.get(lvl.getChapterID()).getHeight()/2, Resources.chapterIcons.get(lvl.getChapterID()), Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {				
				@Override
				public void onClick(final ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {				
					//Resources.mArcadeButton1.play();
					
					mThis.setChildScene(new LanderSelectScene(new ILanderSelectListener() {						
						@Override
						public void onSelect() {
							mThis.clearChildScene();
							Resources.mCurrentLevel = (Level) pButtonSprite.getUserData();
							Util.FadeToBlack(mThis, new LanderScene(true, lvl.getChapterID(), lvl.getLevelID()));
						}
						
						@Override
						public void onCancel() {
							mThis.clearChildScene();
						}
					}), false, true, true);
				}
			});
			icon.setUserData(lvl);
			icon.setEnabled(!lvl.isLevelLocked());
			
			Text t = new Text(0, 0, Resources.mFont_Blue36, lvl.getName(), Resources.mEngine.getVertexBufferObjectManager());
			t.setPosition(icon.getWidth()/2 - t.getWidth()/2, icon.getHeight() + 10);
			icon.attachChild(t);
			
			this.registerTouchArea(icon);
			this.attachChild(icon);
		}

		if (chapterID == 0 && !SimplePreferences.getInstance(Resources.mActivity).getBoolean("TrainingMessageShown", false)){
			//Training!
			MessageBox.show(this, "VTOL Training", "Welcome to VTOL Training! \nBefore you get started, we have to make sure you know what you're doing! Let's walk through some simple Training exercises first, then we can get you going on the real thing. The following exercises should prepare you for an exciting career at VTOL, the galaxy's premiere shipping and transport company! \nGood luck, recruit!",
				new IMessageBoxListener() {						
					@Override
					public void onClose() {
						SimplePreferences.getInstance(Resources.mActivity).edit().putBoolean("TrainingMessageShown", true).commit();
					}
				});
		}
		
		Util.FadeIn(this);
	}

	@Override
	public void Pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Back() {
		Resources.mEngine.runOnUpdateThread(new Runnable() {			
			@Override
			public void run() {
				mThis.clearUpdateHandlers();
				mThis.detachChildren();				
				Util.FadeToBlack(mThis , new ChapterSelectScene());				
			}
		});		
	}
		
}
