package com.singletongames.vtol;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LandingPad extends PhysicsSprite {
	int id = -1;
	
	public int getId() {
		return id;
	}

	public LandingPad(float pX, float pY, IPhysicsSpriteListener listener) {
		super(pX, pY, Resources.LandingPad, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "LandingPad", BodyType.StaticBody, getVertices(), listener);		
	}
	
	public LandingPad(float pX, float pY, int id, IPhysicsSpriteListener listener) {
		super(pX, pY, Resources.LandingPad, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "LandingPad", BodyType.StaticBody, getVertices(), listener);
		this.id = id;
	}

	private static Vector2[] getVertices() {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(Resources.LandingPad, new Vector2(13, 60));
		vertices[1] = Util.getBodyPoint(Resources.LandingPad, new Vector2(29, 28));		
		vertices[2] = Util.getBodyPoint(Resources.LandingPad, new Vector2(243, 28));
		vertices[3] = Util.getBodyPoint(Resources.LandingPad, new Vector2(258, 60));
		
		return vertices;
	}
	
}
