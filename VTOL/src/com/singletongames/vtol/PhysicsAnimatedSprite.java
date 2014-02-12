package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.singletongames.vtol.Util.BodyShape;

public class PhysicsAnimatedSprite extends AnimatedSprite {
	FixtureDef mFixtureDef;
	PhysicsConnector mPhysicsConnector;	
	Body mBody;
	BodyType mBodyType;
	BodyShape mBodyShape;
	Vector2[] mVertices = null;
	boolean markedForDeath = false;
	List<FixtureDef> mFixtureDefs = null;
	List<Object> mFixtureUserData = null;
	Object mBodyUserData = null;
	List<IPhysicsSpriteListener> listeners = new ArrayList<IPhysicsSpriteListener>();
	
	public PhysicsAnimatedSprite(float pX, float pY,ITiledTextureRegion pTiledTextureRegion, FixtureDef pFixtureDef, Object userData, IPhysicsSpriteListener listener) {
		this(pX, pY, pTiledTextureRegion, pFixtureDef, userData, BodyType.StaticBody, Util.BodyShape.Box, listener);
	}
	
	public PhysicsAnimatedSprite(float pX, float pY,ITiledTextureRegion pTiledTextureRegion, FixtureDef pFixtureDef, Object userData, BodyType pBodyType, Util.BodyShape pBodyShape, IPhysicsSpriteListener listener) {
		super(pX, pY, pTiledTextureRegion, Resources.mEngine.getVertexBufferObjectManager());
		mFixtureDef = pFixtureDef;
		mBodyType = pBodyType;
		mBodyShape = pBodyShape;
		//mBody = Util.CreateBody(this, pFixtureDef, pBodyType, pBodyShape);
		registerContactListener();
		if (listener != null) {			
			listeners.add(listener);
		}	
		Load();
	}
	
	public PhysicsAnimatedSprite(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, FixtureDef pFixtureDef, BodyType pBodyType, Vector2[] vertices, IPhysicsSpriteListener listener) {
		super(pX, pY, pTiledTextureRegion, Resources.mEngine.getVertexBufferObjectManager());
		mFixtureDef = pFixtureDef;
		mBodyType = pBodyType;
		mBodyShape = Util.BodyShape.Polygon;
		mVertices = vertices;		
		//mBody = Util.CreateBody(this, pFixtureDef, pBodyType, Util.BodyShape.Polygon, vertices, null, fixtureUserData, bodyUserData);		
		registerContactListener();
		if (listener != null) {			
			listeners.add(listener);
		}	
		Load();
	}

	public PhysicsAnimatedSprite(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, List<FixtureDef> defs, BodyType pBodyType, List<Object> fixtureUserData, Object bodyUserData, IPhysicsSpriteListener listener){
		super(pX, pY, pTiledTextureRegion, Resources.mEngine.getVertexBufferObjectManager());
		mBodyType = pBodyType;
		mBodyShape = Util.BodyShape.Fixtures;
		mFixtureDefs = defs;
		mFixtureUserData = fixtureUserData;
		mBodyUserData = bodyUserData;
		registerContactListener();
		if (listener != null) {			
			listeners.add(listener);
		}
		
		Load();
	}
	
	protected void registerContactListener() {
		GameContactListener.getInstance().registerContactListener(new ContactListener() {			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}			
			@Override
			public void endContact(Contact contact) {}			
			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureA().getBody().equals(mBody)){
					for (IPhysicsSpriteListener listener: listeners){
						listener.onContact(contact.getFixtureA(), contact.getFixtureB());
					}
				}
				else if (contact.getFixtureB().getBody().equals(mBody)){
					for (IPhysicsSpriteListener listener: listeners){
						listener.onContact(contact.getFixtureB(), contact.getFixtureA());
					}
				}
			}
		});
	}
	
	private void Load() {		
		ApplyPhysics();
	}


	private void ApplyPhysics() {
		if (mPhysicsConnector != null){
			Resources.mPhysicsWorld.unregisterPhysicsConnector(mPhysicsConnector);			
		}
		if (mBody != null){
			Resources.mPhysicsWorld.destroyBody(mBody);
		}
		if (mVertices != null){
			mVertices = Util.TransformVertices(this, mVertices);
		}		
		mBody = Util.CreateBody(this, mFixtureDef, mBodyType, mBodyShape, mVertices, mFixtureDefs, mFixtureUserData, mBodyUserData);
		if (mBodyUserData == null) mBody.setUserData(this);
		this.setUserData(mBody);
		mPhysicsConnector = new PhysicsConnector(this, mBody);
		Resources.mPhysicsWorld.registerPhysicsConnector(mPhysicsConnector);
	}
	

	@Override
	public void setFlippedVertical(boolean pFlippedVertical) {
		super.setFlippedVertical(pFlippedVertical);		
		ApplyPhysics();
	}

	@Override
	public void setFlippedHorizontal(boolean pFlippedHorizontal) {
		super.setFlippedHorizontal(pFlippedHorizontal);		
		ApplyPhysics();
	}

	@Override
	public void setFlipped(boolean pFlippedHorizontal, boolean pFlippedVertical) {
		super.setFlipped(pFlippedHorizontal, pFlippedVertical);
		
		ApplyPhysics();
	}

	public void destroy(){		
		if (this.hasParent()){
			this.detachSelf();
		}
		
		this.clearUpdateHandlers();
	}
	
	public List<IPhysicsSpriteListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<IPhysicsSpriteListener> listeners) {
		this.listeners = listeners;
	}
}
