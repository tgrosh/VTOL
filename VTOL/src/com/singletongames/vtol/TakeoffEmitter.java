package com.singletongames.vtol;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.ZIndexSorter;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.sprite.Sprite;

public class TakeoffEmitter extends Entity {
	private CircleParticleEmitter takeoffEmitter;	
	private SpriteParticleSystem takeoffParticleSystemLeft, takeoffParticleSystemRight;
	
	public TakeoffEmitter(float x, float y){
		this.setX(x);
		this.setY(y);
		this.setZIndex(25);
		
		createTakeoffEmitter();	
		
		Resources.mEngine.registerUpdateHandler(new IUpdateHandler() {			
			@Override
			public void reset() {
			}			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				takeoffEmitter.setCenter(getX() - Resources.smokeParticle.getWidth()/2, getY() - Resources.smokeParticle.getHeight()/2);
			}
		});
	}
	
	public void Start(){
		takeoffParticleSystemLeft.setParticlesSpawnEnabled(true);
		takeoffParticleSystemRight.setParticlesSpawnEnabled(true);
	}
	
	public void Stop(){
		takeoffParticleSystemLeft.setParticlesSpawnEnabled(false);
		takeoffParticleSystemRight.setParticlesSpawnEnabled(false);
	}
	
	private void createTakeoffEmitter() {
		takeoffEmitter = new CircleParticleEmitter(this.getX(), this.getY(), 10);
		takeoffParticleSystemLeft = createTakeoffParticleSystem(1.5f, -80f, 80f);
		takeoffParticleSystemRight = createTakeoffParticleSystem(1.5f, 80f, 100f);

		Resources.mEngine.getScene().attachChild(takeoffParticleSystemLeft);
		//Resources.mEngine.getScene().attachChild(takeoffParticleSystemRight);
	}
	
	private SpriteParticleSystem createTakeoffParticleSystem(float duration, float minVelocity, float maxVelocity){
		SpriteParticleSystem takeoffParticleSystem = new SpriteParticleSystem(takeoffEmitter, 60, 60, 360, Resources.smokeParticle, Resources.mEngine.getVertexBufferObjectManager());		
		
		takeoffParticleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(1));
		takeoffParticleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(minVelocity, maxVelocity, 0, 0));
		takeoffParticleSystem.addParticleInitializer(new RotationParticleInitializer<Sprite>(0.0f, 360f));
		takeoffParticleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(duration));
		takeoffParticleSystem.addParticleInitializer(new ScaleParticleInitializer<Sprite>(1));

		takeoffParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0f, duration, 1f, 6f));		
		takeoffParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, duration, 1, 0));
			
		takeoffParticleSystem.setParticlesSpawnEnabled(false);
		takeoffParticleSystem.setZIndex(25);
		
		return takeoffParticleSystem;
	}
}
