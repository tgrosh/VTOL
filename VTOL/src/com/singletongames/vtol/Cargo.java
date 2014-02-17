package com.singletongames.vtol;

import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.modifier.IModifier;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Cargo extends PhysicsSprite {
	private boolean attachable = true;
	private int id = -1;
	
	public Cargo(float pX, float pY, int id, ITextureRegion texture, List<FixtureDef> fixtureDefs, List<Object> fixtureUserData) {		
		super(pX, pY, texture, fixtureDefs, BodyType.DynamicBody, fixtureUserData, null, null);
		this.setId(id);
		
		this.mBody.setLinearDamping(.25f);
	}
	
	public void fadeAndDestroy(){
		setAttachable(false);
		
		AlphaModifier alpha = new AlphaModifier(1f, 1f, 0f, new IEntityModifierListener() {						
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, final IEntity pItem) {
				Resources.mEngine.runOnUpdateThread(new Runnable() {								
					@Override
					public void run() {
						Cargo.this.destroy();
					}
				});
			}
		});
		this.registerEntityModifier(alpha);
	}

	public boolean isAttachable() {
		return attachable;
	}

	public void setAttachable(boolean attachable) {
		this.attachable = attachable;
		for (Fixture fix: mBody.getFixtureList()){
			if (fix.getUserData() != null && fix.getUserData().equals("CargoAttachment")){
				
			}
		}
	}

	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}
}
