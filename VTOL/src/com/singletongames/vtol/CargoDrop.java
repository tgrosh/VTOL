package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class CargoDrop extends PhysicsAnimatedSprite {
	int id = -1;
	List<ICargoDropListener> cargoDropListeners = new ArrayList<ICargoDropListener>();
	
	public CargoDrop(float pX, float pY, int id, ICargoDropListener listener) {
		super(pX,pY, Resources.CargoDrop, getFixtureDefs(Resources.CargoDrop), BodyType.StaticBody, getFixtureUserData(), "CargoDrop", null);
		this.id = id;		
		
		IPhysicsSpriteListener l = new IPhysicsSpriteListener() {			
			@Override
			public void onContact(Fixture fixtureA, Fixture fixtureB) {
				//Debug.w("DEBUG: Contact with Cargo Drop");
				if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("CargoDrop")){
					//Debug.w("DEBUG: Contact with Cargo Drop Sensor");
					if (fixtureB.getBody().getUserData() != null && fixtureB.getBody().getUserData().getClass().getSuperclass().equals(Cargo.class)){
						//if a cargo body drops onto the cargo drop sensor
						Cargo cargo = (Cargo) fixtureB.getBody().getUserData();
						for (ICargoDropListener listener: cargoDropListeners){
							listener.onCargoDelivered(cargo);
						}
						
						glow(cargo);
					}
				}
			}
		};
		
		if (listener != null) this.cargoDropListeners.add(listener);
		this.listeners.add(l);
		
		this.animate(new long[] {250,250,250,250}, 1, 4, true);
	}
	
	public CargoDrop(float pX, float pY, ICargoDropListener listener) {
		this(pX,pY, -1, listener);		
	}

	private static List<FixtureDef> getFixtureDefs(TiledTextureRegion tex) {		
		List<FixtureDef> defs = new ArrayList<FixtureDef>();
		
		defs.add(Util.createPolygonFixtureDef(getBodyVerticesLeft(tex), PhysicsFactory.createFixtureDef(1000f, .05f, .5f)));
		defs.add(Util.createPolygonFixtureDef(getBodyVerticesMiddle(tex), PhysicsFactory.createFixtureDef(1000f, .05f, .5f)));
		defs.add(Util.createPolygonFixtureDef(getBodyVerticesRight(tex), PhysicsFactory.createFixtureDef(1000f, .05f, .5f)));
		defs.add(Util.createPolygonFixtureDef(getDropAreaVertices(tex), PhysicsFactory.createFixtureDef(0f, 0f, 0f, true)));

		return defs;
	}
	
	protected static ArrayList<Object> getFixtureUserData(){
		ArrayList<Object> result = new ArrayList<Object>();
		
		result.add(null);
		result.add(null);
		result.add(null);
		result.add("CargoDrop");
		
		return result;
	}
	
	private static Vector2[] getBodyVerticesLeft(TiledTextureRegion tex) {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(0, 97));
		vertices[1] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(12, 20));		
		vertices[2] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(40, 21));
		vertices[3] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(47, 50));
		
		return vertices;
	}
	
	private static Vector2[] getBodyVerticesRight(TiledTextureRegion tex) {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(199, 50));
		vertices[1] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(206, 21));		
		vertices[2] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(233, 20));
		vertices[3] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(245, 97));
		
		return vertices;
	}
	
	private static Vector2[] getBodyVerticesMiddle(TiledTextureRegion tex) {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(47, 50));
		vertices[1] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(199, 50));		
		vertices[2] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(199, 97));
		vertices[3] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(47, 97));
		
		return vertices;
	}
	
	private static Vector2[] getDropAreaVertices(TiledTextureRegion tex) {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(47, 50));
		vertices[1] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(199, 50));		
		vertices[2] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(199, 97));
		vertices[3] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(47, 97));
		
		return vertices;
	}
	
	public void addListener(ICargoDropListener listener){
		if (listener != null) this.cargoDropListeners.add(listener);
	}
	
	public int getId() {
		return id;
	}

	
	private void glow(Cargo cargo) {
		stopAnimation();
		setCurrentTileIndex(0);		
		
		Sprite glow = new Sprite(this.getX() + this.getWidth()/2 - Resources.CargoDropGlow.getWidth()/2, this.getY() - 91, Resources.CargoDropGlow, Resources.mEngine.getVertexBufferObjectManager());
		glow.setAlpha(0f);
		glow.setZIndex(cargo.getZIndex()+1);						
		AlphaModifier alpha1 = new AlphaModifier(.25f, 0f, 1f);
		DelayModifier delay = new DelayModifier(1f);
		AlphaModifier alpha2 = new AlphaModifier(.25f, 1f, 0f, new IEntityModifierListener() {							
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}							
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, final IEntity pItem) {
				Resources.mEngine.runOnUpdateThread(new Runnable() {									
					@Override
					public void run() {
						pItem.detachSelf();
					}
				});								
			}
		});
		SequenceEntityModifier seq = new SequenceEntityModifier(alpha1, delay, alpha2);
		glow.registerEntityModifier(seq);
		

		Resources.mEngine.getScene().attachChild(glow);
		Resources.mEngine.getScene().sortChildren();
	}
}
