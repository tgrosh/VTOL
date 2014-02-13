package com.singletongames.vtol.objectives;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.singletongames.vtol.GameContactListener;
import com.singletongames.vtol.Resources;
import com.singletongames.vtol.Util;

public class ObjectiveZone extends Rectangle {
	int id;
	private Body body;
	List<IObjectiveZoneListener> listeners = new ArrayList<IObjectiveZoneListener>();
	
	public ObjectiveZone(float pX, float pY, float pWidth, float pHeight, int id) {
		super(pX, pY, pWidth, pHeight, Resources.mEngine.getVertexBufferObjectManager());
		
		this.id = id;		
		this.setVisible(false);
		FixtureDef def = PhysicsFactory.createFixtureDef(0, 0, 0);
		def.isSensor = true;
		body = PhysicsFactory.createBoxBody(Resources.mPhysicsWorld, this, BodyType.StaticBody, def);
		body.setUserData("ObjectiveZone");
		
		GameContactListener.getInstance().registerContactListener(new ContactListener() {			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}			
			@Override
			public void endContact(Contact contact) {				
				Body contactBody = Util.getContactedBody(contact, body);
				if (contactBody != null){
					//Debug.w("DEBUG: Ending Contact with Objective Zone " + ObjectiveZone.this.id);
					for (IObjectiveZoneListener listener: listeners){						
						listener.onExit(ObjectiveZone.this, contactBody);
					}
				}
			}			
			@Override
			public void beginContact(Contact contact) {
				Body contactBody = Util.getContactedBody(contact, body);
				if (contactBody != null){
					//Debug.w("DEBUG: Beginning Contact with Objective Zone " + ObjectiveZone.this.id);
					for (IObjectiveZoneListener listener: listeners){
						listener.onEnter(ObjectiveZone.this, contactBody);
					}
				}
			}
		});
	}

	public void addListener(IObjectiveZoneListener listener) {
		listeners.add(listener);
	}

	public int getId() {
		return id;
	}

}
