package com.singletongames.vtol;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseBackOut;

public class MessageBox {
	private static MessageBox instance = new MessageBox();
	
	public static void show(Scene scene, String title, String message){
		show(scene, title, message);
	}
	public static void show(Scene scene, String title, String message, final IMessageBoxListener listener) {
		//Scene scene = Resources.mEngine.getScene();
		final Sprite sFrame = new Sprite(Resources.CAMERA_WIDTH/2 - Resources.MessageFrame.getWidth()/2, Resources.CAMERA_HEIGHT/2 - Resources.MessageFrame.getHeight()/2, Resources.MessageFrame, Resources.mEngine.getVertexBufferObjectManager());
		sFrame.setScale(.5f);
		sFrame.setAlpha(0f);
		
		Text tClose = new Text(685, 60, Resources.mFont_Yellow24, "Close", Resources.mEngine.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Resources.mEngine.runOnUpdateThread(new Runnable() {						
					@Override
					public void run() {
						if (listener != null){
							listener.onClose();
						}
						if (sFrame.hasParent()){
							sFrame.detachSelf();	
						}
					}
				});
				
				return true;
			}				
		};
		
		Text tTitle = new Text(0, 0, Resources.mFont_Yellow36, title, Resources.mEngine.getVertexBufferObjectManager());
		tTitle.setPosition(sFrame.getWidth()/2 - tTitle.getWidth()/2, 20);
		
		String[] messages = message.split("\\n");
		int currentMsgY = 160;
		int pSpace = 15;
		for (String msg: messages){
			Text tMessage = new Text(0, 0, Resources.mFont_MessageGreen24, msg, Resources.mEngine.getVertexBufferObjectManager());
			tMessage.setPosition(90, currentMsgY);
			tMessage.setAutoWrap(AutoWrap.WORDS);
			tMessage.setAutoWrapWidth(sFrame.getWidth() - 180);
			
			sFrame.attachChild(tMessage);
			
			currentMsgY+=tMessage.getHeight() + pSpace;
		}
		
		sFrame.attachChild(tTitle);
		sFrame.attachChild(tClose);
		
		ScaleModifier scaleIn = new ScaleModifier(.5f, .5f, 1f, EaseBackOut.getInstance());
		AlphaModifier alphaIn = new AlphaModifier(.25f, 0f, 1f);
		ParallelEntityModifier par = new ParallelEntityModifier(scaleIn, alphaIn);
		sFrame.registerEntityModifier(par);
		
		scene.registerTouchArea(tClose);
		scene.attachChild(sFrame);
	}
	
	public static MessageBox getInstance() {
		return instance;
	}
}
