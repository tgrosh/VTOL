package com.singletongames.vtol;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface IPhysicsSpriteListener {
	public void onContact(Fixture fixtureA, Fixture fixtureB);
}
