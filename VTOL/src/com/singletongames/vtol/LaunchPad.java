package com.singletongames.vtol;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LaunchPad extends PhysicsSprite {
	
	public LaunchPad(float pX, float pY, IPhysicsSpriteListener listener) {
		super(pX, pY + 2, Resources.LaunchPad, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "LaunchPad", BodyType.StaticBody, getVertices(), listener);
	}

	private static Vector2[] getVertices() {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(165, 180));
		vertices[1] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(165, 170));		
		vertices[2] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(417, 170));
		vertices[3] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(417, 180));
		
		return vertices;
	}
	
}
