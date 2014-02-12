package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsFactory;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class NoFlyZone extends Rectangle {

	private Body body;
	List<INoFlyListener> listeners = new ArrayList<INoFlyListener>();
	
	public NoFlyZone(float pX, float pY, float pWidth, float pHeight) {
		super(pX, pY, pWidth, pHeight, Resources.mEngine.getVertexBufferObjectManager());

		this.setVisible(false);
		FixtureDef def = PhysicsFactory.createFixtureDef(0, 0, 0);
		def.isSensor = true;
		body = PhysicsFactory.createBoxBody(Resources.mPhysicsWorld, this, BodyType.StaticBody, def);
		body.setUserData("NoFly");
		
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
					for (INoFlyListener listener: listeners){
						listener.onExit(contactBody);
					}
				}
			}			
			@Override
			public void beginContact(Contact contact) {
				Body contactBody = Util.getContactedBody(contact, body);
				if (contactBody != null){
					for (INoFlyListener listener: listeners){
						listener.onEnter(contactBody);
					}
				}
			}
		});
	}

	public void addListener(INoFlyListener listener) {
		listeners.add(listener);
	}

}
