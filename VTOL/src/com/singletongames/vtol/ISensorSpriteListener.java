package com.singletongames.vtol;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface ISensorSpriteListener {
	public void onSensorBeginContact(Fixture fixtureA, Fixture fixtureB);
	public void onSensorEndContact(Fixture fixtureA, Fixture fixtureB);
}
