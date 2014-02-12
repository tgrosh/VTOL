package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseSineInOut;

import com.badlogic.gdx.math.Vector2;

public class ScenePreview {
	Entity previewEntity = new Entity();
	List<Vector2> previewPoints = new ArrayList<Vector2>();
	SmoothCamera camera;
	Scene scene;
	List<IScenePreviewListener> listeners = new ArrayList<IScenePreviewListener>();
	float previewDelay = 3f;
	float maxZoomOut = .75f;
	private float previewSpeed = 1;
	private List<IEntityModifier> modifiers;
	private Text skipText;
	
	public ScenePreview(Scene scene, SmoothCamera camera, List<Vector2> previewPoints){
		this.previewPoints = previewPoints;
		this.camera = camera;
		this.scene = scene;
		
		if (this.previewPoints.size() > 0){
			previewEntity.setPosition(this.previewPoints.get(0).x, this.previewPoints.get(0).y);
		}
	}
	
	public ScenePreview(Scene scene, SmoothCamera camera, List<Vector2> previewPoints, IScenePreviewListener listener){
		this.previewPoints = previewPoints;
		this.camera = camera;
		this.scene = scene;
		if (listener != null) listeners.add(listener);
		
		if (this.previewPoints.size() > 0){
			previewEntity.setPosition(this.previewPoints.get(0).x, this.previewPoints.get(0).y);
		}
	}
	
	public void Start(){
		if (this.previewPoints.size() == 0) return;
		
		Vector2 previousPoint = new Vector2(previewEntity.getX(), previewEntity.getY());
		modifiers = new ArrayList<IEntityModifier>();
		
		DelayModifier delay = new DelayModifier(previewDelay, new IEntityModifierListener() {			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				camera.setZoomFactor(maxZoomOut);
			}
		});
		modifiers.add(delay);
		
		for (Vector2 point: previewPoints){
			float a = point.x - previousPoint.x;
			float b = point.y - previousPoint.y;
			float c = (float) Math.sqrt(Math.abs(a*a) + Math.abs(b*b));
			float duration = c / camera.getMaxVelocityX();
			
			MoveModifier mover = new MoveModifier(duration, previousPoint.x, point.x, previousPoint.y, point.y, EaseSineInOut.getInstance());			
			modifiers.add(mover);
			previousPoint = point;
		}
		
		this.scene.attachChild(previewEntity);		
		camera.setCenterDirect(previewEntity.getX(), previewEntity.getY());
		camera.setChaseEntity(previewEntity);
		
		SequenceEntityModifier seq = new SequenceEntityModifier(new IEntityModifierListener() {			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, final IEntity pItem) {				
				camera.setZoomFactor(1f);
				camera.setChaseEntity(null);
				for (IScenePreviewListener listener: listeners){					
					listener.onFinish();					
				}
				Resources.mEngine.runOnUpdateThread(new Runnable() {					
					@Override
					public void run() {
						pItem.detachSelf();
						skipText.detachSelf();
					}
				});	
			}
		}, modifiers.toArray(new IEntityModifier[0]));
		previewEntity.registerEntityModifier(seq);
		
		skipText = new Text(0, 0, Resources.mFont_Yellow48, "TAP TO SKIP PREVIEW", Resources.mEngine.getVertexBufferObjectManager());
		skipText.setPosition(Resources.CAMERA_WIDTH/2 - skipText.getWidth()/2, Resources.CAMERA_HEIGHT - skipText.getHeight() - 30);
		AlphaModifier alpha1 = new AlphaModifier(.3f, 1, 0);
		AlphaModifier alpha2 = new AlphaModifier(.3f, 0, 1);
		DelayModifier delaySkip = new DelayModifier(1f);
		SequenceEntityModifier seqSkip = new SequenceEntityModifier(new IEntityModifier[]{alpha1,alpha2,delaySkip});
		LoopEntityModifier loopSkip = new LoopEntityModifier(seqSkip);
		skipText.registerEntityModifier(loopSkip);
		this.camera.getHUD().attachChild(skipText);
	}

	public float getPreviewDelay() {
		return previewDelay;
	}

	public void setPreviewDelay(float previewDelay) {
		this.previewDelay = previewDelay;
	}

	public void SkipToEnd() {		
		Rectangle r = new Rectangle(0, 0, 10000, 10000, Resources.mEngine.getVertexBufferObjectManager());
		r.setColor(org.andengine.util.color.Color.BLACK);
		r.setAlpha(0);
		
		AlphaModifier fader1 = new AlphaModifier(.3f, 0, 1, new IEntityModifierListener() {								
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}								
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {				
				camera.setZoomFactor(1f);
				camera.setChaseEntity(null);
				for (IScenePreviewListener listener: listeners){					
					listener.onCancel();					
				}
				Resources.mEngine.runOnUpdateThread(new Runnable() {					
					@Override
					public void run() {
						previewEntity.clearEntityModifiers();
						previewEntity.detachSelf();
					}
				});				
			}
		});
		AlphaModifier fader2 = new AlphaModifier(.3f, 1, 0);
		SequenceEntityModifier seq = new SequenceEntityModifier(new IEntityModifier[]{fader1,fader2});
		r.registerEntityModifier(seq);
		Resources.mEngine.getScene().attachChild(r);
		
		Resources.mEngine.runOnUpdateThread(new Runnable() {			
			@Override
			public void run() {
				skipText.detachSelf();
			}
		});
	}

}
