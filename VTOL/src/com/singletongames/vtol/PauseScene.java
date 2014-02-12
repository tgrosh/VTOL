package com.singletongames.vtol;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseBackOut;

public class PauseScene extends MenuScene {
	private Engine mEngine = Resources.mEngine;	
	
	
	public PauseScene() {
		super(Resources.mEngine.getCamera());
		showPause();
	}

	public PauseScene(IOnMenuItemClickListener pOnMenuItemClickListener) {
		super(Resources.mEngine.getCamera(),pOnMenuItemClickListener);
		showPause();
	}
	
	private void showPause(){
		setBackgroundEnabled(false);

		Rectangle backDrop = new Rectangle(0, 0, Resources.CAMERA_WIDTH, Resources.CAMERA_HEIGHT, mEngine.getVertexBufferObjectManager());
		backDrop.setColor(Color.BLACK);
		backDrop.setAlpha(.4f);
		this.attachChild(backDrop);
				
		AlphaModifier spriteFader = new AlphaModifier(.5f, 0, 1);
		
		Text pausedText = new Text(0,0,Resources.mFont_Blue72, "PAUSED", mEngine.getVertexBufferObjectManager());
		pausedText.setPosition(backDrop.getWidth()/2 - pausedText.getWidth()/2, 300);
		pausedText.registerEntityModifier(spriteFader.deepCopy());
		backDrop.attachChild(pausedText);
		
		SpriteMenuItem menuItemResume = new SpriteMenuItem(2, Resources.ResumeButton.getTextureRegion(0), mEngine.getVertexBufferObjectManager());
		menuItemResume.setPosition((Resources.CAMERA_WIDTH/2) - (Resources.ResumeButton.getTextureRegion(0).getWidth()) -50,  pausedText.getY() + pausedText.getHeight() + 50);
		
		SpriteMenuItem menuItemRestart = new SpriteMenuItem(3, Resources.RestartButton.getTextureRegion(0), mEngine.getVertexBufferObjectManager());
		menuItemRestart.setPosition((Resources.CAMERA_WIDTH/2) + 50,  menuItemResume.getY());
		
		SpriteMenuItem menuItemMain = new SpriteMenuItem(1, Resources.MenuButton.getTextureRegion(0), mEngine.getVertexBufferObjectManager());
		menuItemMain.setPosition((Resources.CAMERA_WIDTH/2) - (Resources.MenuButton.getTextureRegion(0).getWidth()/2), menuItemResume.getY() + menuItemResume.getHeight() + 50);
		
				
		this.addMenuItem(menuItemMain);
		this.addMenuItem(menuItemResume);
		this.addMenuItem(menuItemRestart);
	}

	

}
