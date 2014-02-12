package com.singletongames.vtol;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.sprite.Sprite;

public class ExhaustEmitter extends Entity {
	private CircleParticleEmitter exhaustEmitter;	
	private SpriteParticleSystem exhaustParticleSystem;
	
	public ExhaustEmitter(float x, float y){
		this.setX(x);
		this.setY(y);
		this.setZIndex(15);
		
		createExhaustEmitter();
		
		Resources.mEngine.registerUpdateHandler(new IUpdateHandler() {			
			@Override
			public void reset() {
			}			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				exhaustEmitter.setCenter(getX() - Resources.smokeParticle.getWidth()/2, getY() - Resources.smokeParticle.getHeight()/2);
			}
		});
	}
	
	public void Start(){
		exhaustParticleSystem.setParticlesSpawnEnabled(true);
	}
	
	public void Stop(){
		exhaustParticleSystem.setParticlesSpawnEnabled(false);
	}
	
	private void createExhaustEmitter() {
		exhaustEmitter = new CircleParticleEmitter(this.getX(), this.getY(), 10);	
	
		exhaustParticleSystem = new SpriteParticleSystem(exhaustEmitter, 60, 60, 360, Resources.smokeParticle, Resources.mEngine.getVertexBufferObjectManager());
		
		exhaustParticleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(0));
		exhaustParticleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-2, 2, 5, 10));
		exhaustParticleSystem.addParticleInitializer(new RotationParticleInitializer<Sprite>(0.0f, 360.0f));
		exhaustParticleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(1.5f));

		exhaustParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, 1.5f, 1.0f, 2.0f));
		exhaustParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, .1f, 0, 1));
		exhaustParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(.5f, 1.5f, 1, 0));
	
		exhaustParticleSystem.setParticlesSpawnEnabled(false);
		exhaustParticleSystem.setZIndex(15);
		
		Resources.mEngine.getScene().attachChild(exhaustParticleSystem);
	}
}
