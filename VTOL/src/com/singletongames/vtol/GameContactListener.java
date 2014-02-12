package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements	com.badlogic.gdx.physics.box2d.ContactListener {
	private static GameContactListener instance = new GameContactListener();
	
	List<com.badlogic.gdx.physics.box2d.ContactListener> listeners = new ArrayList<com.badlogic.gdx.physics.box2d.ContactListener>();
	
	@Override
	public void beginContact(Contact contact) {
		for (com.badlogic.gdx.physics.box2d.ContactListener listener: listeners){
			listener.beginContact(contact);
		}
	}

	@Override
	public void endContact(Contact contact) {
		for (com.badlogic.gdx.physics.box2d.ContactListener listener: listeners){
			listener.endContact(contact);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		for (com.badlogic.gdx.physics.box2d.ContactListener listener: listeners){
			listener.preSolve(contact, oldManifold);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		for (com.badlogic.gdx.physics.box2d.ContactListener listener: listeners){
			listener.postSolve(contact, impulse);
		}
	}

	public void registerContactListener(com.badlogic.gdx.physics.box2d.ContactListener listener){		
		listeners.add(listener);
	}

	public static GameContactListener getInstance() {
		return instance;
	}

}
