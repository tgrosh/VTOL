package com.singletongames.vtol;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;

import com.badlogic.gdx.math.Vector2;

public class TipBubble extends Entity {
	public enum ArrowPosition {
			LEFT,
			RIGHT,
			TOP,
			BOTTOM,
			LEFT_TOP,
			RIGHT_TOP,
			LEFT_BOTTOM,
			RIGHT_BOTTOM
		}
	private Sprite sFrame;
	private Sprite sArrow;
	float mX;
	float mY;
	float mWidth;
	float mHeight;
	String tipText;
	ArrowPosition arrowPosition;
	
	public TipBubble(float pX, float pY, float pWidth, float pHeight, String tipText) {
		this(pX,pY,pWidth,pHeight,tipText,null);
	}
	public TipBubble(float pX, float pY, float pWidth, float pHeight, String tipText, ArrowPosition arrowPosition) {
		this.mX = pX;
		this.mY = pY;
		this.mWidth = pWidth;
		this.mHeight = pHeight;
		this.tipText = tipText;
		this.arrowPosition = arrowPosition;
		
		Load();
	}
	
	private void Load(){
		this.setPosition(mX, mY);
		
		sFrame = new Sprite(0, 0, Resources.TipFrame, Resources.mEngine.getVertexBufferObjectManager());
		float scaleX = this.mWidth/Resources.TipFrame.getWidth();
		float scaleY = this.mHeight/Resources.TipFrame.getHeight();
		sFrame.setScaleCenter(0, 0);
		sFrame.setScaleX(scaleX);		
		sFrame.setScaleY(scaleY);
		
		Text text = new Text(0, 0, Resources.mFont_MessageGreen24, tipText, Resources.mEngine.getVertexBufferObjectManager());		
		text.setAutoWrap(AutoWrap.WORDS);
		text.setAutoWrapWidth(630*scaleX);
		text.setPosition((sFrame.getWidth()*scaleX)/2 - text.getWidth()/2, (sFrame.getHeight()*scaleY)/2 - text.getHeight()/2);
				
		this.attachChild(sFrame);
		this.attachChild(text);
		
		if (arrowPosition != null){
			sArrow = new Sprite(0, 0, Resources.TipArrow, Resources.mEngine.getVertexBufferObjectManager());
			transformArrow(sArrow, scaleX, scaleY);
			this.attachChild(sArrow);
		}
	}
	private void transformArrow(Sprite sArrow, float scaleX, float scaleY) {	
		float arrowPaddingX = 20;
		float arrowPaddingY = 15;
		float arrowX_Left = sArrow.getWidth()*-1 - arrowPaddingX;
		float arrowX_Right = (sFrame.getWidth()*scaleX) + arrowPaddingX;
		float arrowX_Center = (sFrame.getWidth()*scaleX)/2 - sArrow.getWidth()/2;		
		float arrowY_Top = sArrow.getHeight()*-1 - arrowPaddingY;
		float arrowY_Bottom = (sFrame.getHeight()*scaleY) + arrowPaddingY;
		float arrowY_Center = (sFrame.getHeight()*scaleY)/2 - sArrow.getHeight()/2;
		float arrowScale = (scaleX + scaleY) /2;
		
		sArrow.setScale(arrowScale);
		switch (arrowPosition){
			case LEFT:{
				sArrow.setPosition(arrowX_Left, arrowY_Center);				
				break;
			}
			case RIGHT:{
				sArrow.setPosition(arrowX_Right, arrowY_Center);
				sArrow.setRotation(180f);
				break;
			}
			case TOP:{
				sArrow.setPosition(arrowX_Center, arrowY_Top);
				sArrow.setRotation(90f);
				break;
			}
			case BOTTOM:{
				sArrow.setPosition(arrowX_Center, arrowY_Bottom);
				sArrow.setRotation(270);
				break;
			}
			case LEFT_TOP:{
				sArrow.setPosition(arrowX_Left + sArrow.getWidth()/2, arrowY_Top + sArrow.getHeight()/2);
				sArrow.setRotation(45);
				break;
			}
			case RIGHT_TOP:{
				sArrow.setPosition(arrowX_Right - sArrow.getWidth()/2, arrowY_Top + sArrow.getHeight()/2);
				sArrow.setRotation(135);
				break;
			}
			case LEFT_BOTTOM:{
				sArrow.setPosition(arrowX_Left + sArrow.getWidth()/2, arrowY_Bottom - sArrow.getHeight()/2);
				sArrow.setRotation(315);
				break;
			}
			case RIGHT_BOTTOM:{
				sArrow.setPosition(arrowX_Right - sArrow.getWidth()/2, arrowY_Bottom - sArrow.getHeight()/2);
				sArrow.setRotation(225);
				break;
			}
		}		
	}
}
