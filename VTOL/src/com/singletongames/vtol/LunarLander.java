package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class LunarLander extends Lander {

	public LunarLander(float pX, float pY, ILanderListener listener) {
		super(pX - Resources.mLander.getWidth()/2, pY - Resources.mLander.getHeight(), LanderDB.getInstance().getLander(1), Resources.mLander, getFixtureDefs(Resources.mLander), getFixtureUserData(), listener);
	}

	@Override
	protected List<Vector2> getExhaustPoints() {
		List<Vector2> p = new ArrayList<Vector2>();
		p.add(new Vector2(0f,(Resources.mLander.getHeight()/2)));
		
		return p;
	}

	protected static Vector2[] getBodyVertices(TiledTextureRegion sBody){
		Vector2[] vertices = new Vector2[6];
		vertices[0] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(0, 102f));
		vertices[1] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(51f, 15f));		
		vertices[2] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(69f, 15f));
		vertices[3] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(120f, 102f));
		vertices[4] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(116f, 106f));
		vertices[5] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(4f, 106f));
		
		return vertices;
	}

	protected static ArrayList<Object> getFixtureUserData(){
		ArrayList<Object> result = new ArrayList<Object>();
		
		result.add(null);
		result.add("LanderBase");
		
		return result;
	}
	
	protected static Vector2[] getBaseVertices(TiledTextureRegion sBody) {
		Vector2[] vertices = new Vector2[3];
		vertices[0] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(10f, 107f));
		vertices[1] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(60f, 60f));
		vertices[2] = Util.getBodyPoint(sBody.getTextureRegion(0), new Vector2(110f, 107f));
		
		return vertices;
	}
	
	private static List<FixtureDef> getFixtureDefs(TiledTextureRegion mLander) {		
		List<FixtureDef> defs = new ArrayList<FixtureDef>();
		
		defs.add(Util.createPolygonFixtureDef(getBodyVertices(mLander), PhysicsFactory.createFixtureDef(LanderDB.getInstance().getLander(0).density, .05f, .75f)));
		defs.add(Util.createPolygonFixtureDef(getBaseVertices(mLander), PhysicsFactory.createFixtureDef(0f, 0f, 0f, true)));
				
		return defs;
	}
	

}
