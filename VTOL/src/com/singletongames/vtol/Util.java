package com.singletongames.vtol;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectProperty;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTileSet;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXTiledMapProperty;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.GradientFontFactory;
import org.andengine.opengl.font.GradientStrokeFont;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Util {
	protected static Text GetStringTexture(final String text, float posX, float posY, int maxCharacters, Font font, Engine engine)	{
		try {			
			if (font != null)
			{
				return new Text(posX, posY, font, text, maxCharacters, engine.getVertexBufferObjectManager());
			}
		} catch (Exception e) {
			Debug.e(e);			
		}
		
		return null;	
	}
	
	public static BitmapTextureAtlas getBitmapTextureAtlas(String path){
		BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        
        try {
            InputStream in = Resources.mActivity.getResources().getAssets().open(path);
            BitmapFactory.decodeStream(in, null, opt);
        } catch (IOException e) {
            Debug.e("TextureCache", "Could not load texture [" + path + "]", e);
        }
                
        BitmapTextureAtlas texAtlas = new BitmapTextureAtlas(Resources.mEngine.getTextureManager(), opt.outWidth, opt.outHeight, TextureOptions.BILINEAR);
        
        return texAtlas;
	}
	
	public static TextureRegion GetTextureRegion(final String path)
	{			 
		BitmapTextureAtlas tex;
		TextureRegion reg = null;
		try {
			tex = getBitmapTextureAtlas(path); //new BitmapTextureAtlas(pEngine.getTextureManager(), x, y,TextureOptions.BILINEAR);
			reg = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tex, Resources.mActivity, path, 0, 0);
			tex.load();
			
		} catch (IllegalArgumentException e) {
			Debug.e(e);
		}
		
		return reg;
	}
	public static TiledTextureRegion GetTiledTextureRegion(final String path, int columns, int rows)
	{			 
		BitmapTextureAtlas tex;
		TiledTextureRegion reg = null;
		try {
			tex = getBitmapTextureAtlas(path); //new BitmapTextureAtlas(pEngine.getTextureManager(), x, y,TextureOptions.BILINEAR);
			reg = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(tex, Resources.mActivity, path, 0, 0, columns, rows);
			tex.load();
			
		} catch (IllegalArgumentException e) {
			Debug.e(e);
		}
		
		return reg;
	}
	
//	public static GradientStrokeFont GetGradientStrokeFont(final int size, final int fillColorTop, final int fillColorBottom, final int strokeColor, int strokeWidth, Engine pEngine) {
//		return GetGradientStrokeFont(size, fillColorTop, fillColorBottom, strokeColor, strokeWidth, false, pEngine);
//	}
	public static GradientStrokeFont GetGradientStrokeFont(final int size, final int fillColorTop, final int fillColorBottom, final int strokeColor, int strokeWidth, boolean Bold, Engine pEngine) {
		GradientStrokeFont mFont;
		Typeface face = Resources.mNeon;
		if (Bold){
			face = Resources.mNeonBold;
		}
		mFont = GradientFontFactory.createStroke(pEngine.getFontManager(), new BitmapTextureAtlas(pEngine.getTextureManager(), 800, 400, TextureOptions.BILINEAR), face, size, true, fillColorTop, fillColorBottom, strokeWidth, strokeColor);
		
		mFont.load();
		return mFont;
	}	
	public static StrokeFont GetStrokeFont(final int size, final int fillColor, final int strokeColor, int strokeWidth, boolean Bold, Engine pEngine) {
		StrokeFont mFont;
		Typeface face = Resources.mNeon;
		if (Bold){
			face = Resources.mNeonBold;
		}
		mFont = FontFactory.createStroke(pEngine.getFontManager(), new BitmapTextureAtlas(pEngine.getTextureManager(), 800, 400, TextureOptions.BILINEAR), face, size, true, fillColor, strokeWidth, strokeColor);
		
		mFont.load();
		return mFont;
	}	

	public static Vector2[] ReverseVertices(Vector2[] vertices){
		Vector2[] result = new Vector2[vertices.length];
		int index=0;
		for (int x=vertices.length-1;x>=0;x--){
			result[index] = new Vector2(vertices[x]);
			index++;
		}
		
		return result;
	}
	
	enum BodyShape{
		Box,
		Circle,
		Polygon,
		Fixtures
	}
	
	public static Body CreateBody(Sprite sprite, FixtureDef pFixtureDef, BodyType pBodyType, Util.BodyShape pBodyShape){
		return CreateBody(sprite, pFixtureDef, pBodyType, pBodyShape, null, null, null, null);
	}
	public static Body CreateBody(Sprite sprite, FixtureDef pFixtureDef, BodyType pBodyType, Util.BodyShape pBodyShape, Vector2[] vertices, List<FixtureDef> fixtureDefs, List<Object> fixtureUserData, Object bodyUserData){		
		Body b = null;
		if (pBodyShape == Util.BodyShape.Box){
			b = PhysicsFactory.createBoxBody(Resources.mPhysicsWorld, sprite, pBodyType, pFixtureDef);	
			if (b.getFixtureList() != null && b.getFixtureList().size() > 0 && fixtureUserData != null && fixtureUserData.size() > 0){
				b.getFixtureList().get(0).setUserData(fixtureUserData.get(0));	
			}	
			b.setUserData(bodyUserData);
		}
		else if (pBodyShape == Util.BodyShape.Circle){
			b = PhysicsFactory.createCircleBody(Resources.mPhysicsWorld, sprite, pBodyType, pFixtureDef);
			if (b.getFixtureList() != null && b.getFixtureList().size() > 0 && fixtureUserData != null && fixtureUserData.size() > 0){
				b.getFixtureList().get(0).setUserData(fixtureUserData.get(0));	
			}	
			b.setUserData(bodyUserData);
		}
		else if (pBodyShape == Util.BodyShape.Polygon){
			b = PhysicsFactory.createPolygonBody(Resources.mPhysicsWorld, sprite, vertices, pBodyType, pFixtureDef);
			if (b.getFixtureList() != null && b.getFixtureList().size() > 0 && fixtureUserData != null && fixtureUserData.size() > 0){
				b.getFixtureList().get(0).setUserData(fixtureUserData.get(0));	
			}	
			b.setUserData(bodyUserData);
		}
		else if (pBodyShape == Util.BodyShape.Fixtures){
			if (fixtureDefs == null || fixtureDefs.size() == 0) return null;
			BodyDef bd = new BodyDef();
			bd.type = pBodyType;
			bd.position.set(sprite.getSceneCenterCoordinates()[0]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, sprite.getSceneCenterCoordinates()[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
			b = Resources.mPhysicsWorld.createBody(bd);
			b.setUserData(bodyUserData);
			for (int x=0; x< fixtureDefs.size(); x++){
				FixtureDef def = fixtureDefs.get(x);
				Fixture fix = b.createFixture(def);
				if (fixtureUserData != null && fixtureUserData.size() > x){
					fix.setUserData(fixtureUserData.get(x));
				}
			}
		}
		
		return b;
	}
	
	public static Vector2[] TransformVertices(Sprite sprite, Vector2[] pVertices){
		return TransformVertices(sprite.isFlippedHorizontal(), sprite.isFlippedVertical(), pVertices);
	}
	public static Vector2[] TransformVertices(boolean flippedHorizontal, boolean flippedVertical, Vector2[] pVertices){
		if (pVertices == null) return null;
		
		if (flippedHorizontal){
			for (Vector2 v: pVertices){
				v.x *= -1;
			}
			pVertices = Util.ReverseVertices(pVertices);
		}		
		if (flippedVertical){
			for (Vector2 v: pVertices){
				v.y *= -1;
			}
			pVertices = Util.ReverseVertices(pVertices);
		}
		
		return pVertices;
	}
	
	
	
	public static FixtureDef getSensorFixtureDef(FixtureDef pFixtureDef){
		pFixtureDef.isSensor = true;
		return pFixtureDef;
	}
	
	public static Body getContactedBody(Contact contact, Body sourceBody){
		if (contact.getFixtureA().getBody().equals(sourceBody)){
			return contact.getFixtureB().getBody();
		}
		else if (contact.getFixtureB().getBody().equals(sourceBody)){
			return contact.getFixtureA().getBody();
		}
		return null;		
	}
	
	public static String getTMXTiledMapProperty(TMXProperties<TMXTiledMapProperty> properties, String propertyName, String defaultValue){
		String returnValue = defaultValue;
		for (TMXTiledMapProperty prop: properties){
			if (prop.getName().toLowerCase().equals(propertyName.toLowerCase())){
				returnValue = prop.getValue();
			}
		}
		return returnValue;
	}
	public static float getTMXTiledMapProperty(TMXProperties<TMXTiledMapProperty> properties, String propertyName, float defaultValue){
		return Float.parseFloat(getTMXTiledMapProperty(properties, propertyName, String.valueOf(defaultValue)));
	}
	public static int getTMXTiledMapProperty(TMXProperties<TMXTiledMapProperty> properties, String propertyName, int defaultValue){
		return Integer.parseInt(getTMXTiledMapProperty(properties, propertyName, String.valueOf(defaultValue)));
	}
	
	public static String getTMXObjectProperty(TMXProperties<TMXObjectProperty> properties, String propertyName, String defaultValue){
		String returnValue = defaultValue;
		for (TMXObjectProperty prop: properties){
			if (prop.getName().toLowerCase().equals(propertyName.toLowerCase())){
				returnValue = prop.getValue();
			}
		}
		return returnValue;
	}
	public static float getTMXObjectProperty(TMXProperties<TMXObjectProperty> properties, String propertyName, float defaultValue){
		return Float.parseFloat(getTMXObjectProperty(properties, propertyName, String.valueOf(defaultValue)));
	}
	public static int getTMXObjectProperty(TMXProperties<TMXObjectProperty> properties, String propertyName, int defaultValue){
		return Integer.parseInt(getTMXObjectProperty(properties, propertyName, String.valueOf(defaultValue)));
	}

	public static int getTMXTilePropertyValue(TMXTileSet tiles, int gid, String name, int defaultValue){
		return Integer.parseInt(getTMXTilePropertyValue(tiles,gid,name,String.valueOf(defaultValue)));
	}
	public static float getTMXTilePropertyValue(TMXTileSet tiles, int gid, String name, float defaultValue){
		return Float.parseFloat(getTMXTilePropertyValue(tiles,gid,name,String.valueOf(defaultValue)));
	}
	public static String getTMXTilePropertyValue(TMXTileSet tiles, int gid, String name, String defaultValue){		
		try {
			TMXProperties<TMXTileProperty> props = tiles.getTMXTilePropertiesFromGlobalTileID(gid);
			if (props != null){
				for (TMXTileProperty prop: props){
					if (prop.getName().toLowerCase().equals(name.toLowerCase())){
						return prop.getValue();
					}
				}
			}
			else {
				Debug.w("No " + name + " defined for tile gid " + gid + ". Please add a " + name + " property to this tile");
			}
		} catch (Exception e) {
			Debug.e(e);
		}	
		
		return defaultValue;
	}
		
	public static String getTMXTilePropertyValue(TMXTiledMap map, int gid, String name, String defaultValue){		
		try {
			for (TMXTileSet tiles: map.getTMXTileSets()){
				if (tiles.getFirstGlobalTileID() <= gid){
					TMXProperties<TMXTileProperty> props = tiles.getTMXTilePropertiesFromGlobalTileID(gid);
					if (props != null){
						for (TMXTileProperty prop: props){
							if (prop.getName().toLowerCase().equals(name.toLowerCase())){
								return prop.getValue();
							}
						}
					}
				}
			}			
		} catch (Exception e) {
			Debug.e(e);
		}	
		
		Debug.w("No " + name + " defined for tile gid " + gid + ". Please add a " + name + " property to this tile");
		return defaultValue;
	}
	
	
	public static float GetImpactForce(Contact contact) {		
		Vector2 contactPos = contact.getWorldManifold().getPoints()[0];
		Vector2 va = contact.getFixtureA().getBody().getLinearVelocityFromWorldPoint(contactPos);
		va.sub(contact.getFixtureB().getBody().getLinearVelocityFromWorldPoint(contactPos));
		float iForce=va.len();
		//Debug.w("Impact Force Registered at " + iForce);
		return iForce;
	}
	
	public enum SimpletonTextColorScheme
	{
		GRADIENT_GREEN,
		GRADIENT_BLUE,
		GRADIENT_BLUEGREEN,
		GRADIENT_GREY,
		GRADIENT_REDORANGE,
		GRADIENT_RED,
		GRADIENT_GREEN_GRAY,
		TRANSPARENT,
		WHITE
	}
	
	public static GradientStrokeFont GetGradientStrokeFont(final int size, SimpletonTextColorScheme ColorScheme, final int strokeColor, int strokeWidth, boolean Bold, Engine pEngine) {
		int fillColorTop;
		int fillColorBottom;
		
		if (ColorScheme == SimpletonTextColorScheme.GRADIENT_GREY)
		{
			fillColorTop = Color.rgb(204,204,204);
			fillColorBottom = Color.rgb(102,102,102);
		}
		else if (ColorScheme == SimpletonTextColorScheme.GRADIENT_GREEN)
		{
			fillColorTop = Color.rgb(0,255,0);
			fillColorBottom = Color.rgb(0,153,0);
		}
		else if (ColorScheme == SimpletonTextColorScheme.GRADIENT_GREEN_GRAY)
		{
			fillColorTop = Color.rgb(0,255,0);
			fillColorBottom = Color.rgb(140,170,140);
		}
		else if (ColorScheme == SimpletonTextColorScheme.GRADIENT_RED)
		{
			fillColorTop = Color.rgb(255,0,0);
			fillColorBottom = Color.rgb(153,0,0);
		}
		else if (ColorScheme == SimpletonTextColorScheme.GRADIENT_BLUEGREEN)
		{
			fillColorTop = Color.rgb(0,153,255);
			fillColorBottom = Color.rgb(54,204,154);
		}
		else if (ColorScheme == SimpletonTextColorScheme.GRADIENT_BLUE)
		{
			fillColorTop = Color.rgb(0,204,204);
			fillColorBottom = Color.rgb(0,102,153);
		}
		else if (ColorScheme == SimpletonTextColorScheme.GRADIENT_REDORANGE)
		{
			fillColorTop = Color.rgb(255,255,0);
			fillColorBottom = Color.rgb(255,153,0);
		}
		else if (ColorScheme == SimpletonTextColorScheme.TRANSPARENT)
		{
			fillColorTop = Color.TRANSPARENT;
			fillColorBottom = Color.TRANSPARENT;
		}
		else if (ColorScheme == SimpletonTextColorScheme.WHITE)
		{
			fillColorTop = Color.WHITE;
			fillColorBottom = Color.WHITE;
		}
		else
		{
			fillColorTop = Color.rgb(0,0,0);
			fillColorBottom = Color.rgb(0,0,0);
		}
		
		return GetGradientStrokeFont(size, fillColorTop, fillColorBottom, strokeColor, strokeWidth, Bold, pEngine);
	}
	
	public static Level getLevel(int id){
		for (Level lvl: LevelDB.getInstance().allLevels()){
			if (lvl.getLevelID() == id){
				return lvl;
			}
		}
		
		return null;
	}
	
	public static void InitializePhysicsWorld(Engine engine, Vector2 gravity, boolean allowSleep){
		//ClearPhysicsBodies(engine);
		if (Resources.mPhysicsWorld != null){
			engine.unregisterUpdateHandler(Resources.mPhysicsWorld);
			Resources.mPhysicsWorld.dispose();
		}
		Resources.mPhysicsWorld = new PhysicsWorld(gravity, allowSleep);
		engine.registerUpdateHandler(Resources.mPhysicsWorld);
	}
	
	public static void FadeIn(Scene thisScene){
		Rectangle r = new Rectangle(0, 0, Resources.mEngine.getCamera().getWidth(), Resources.mEngine.getCamera().getHeight(), Resources.mEngine.getVertexBufferObjectManager());
		r.setColor(org.andengine.util.color.Color.BLACK);
		r.setAlpha(1);
		AlphaModifier fader = new AlphaModifier(.3f, 1, 0, new IEntityModifierListener() {								
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}								
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {				
				pItem.detachSelf();
			}
		});
		r.registerEntityModifier(fader);
		thisScene.attachChild(r);
	}
	public static void FadeToBlack(final Scene thisScene, final Scene destinationScene) {
		Rectangle r = new Rectangle(0, 0, Resources.mEngine.getCamera().getWidth(), Resources.mEngine.getCamera().getHeight(), Resources.mEngine.getVertexBufferObjectManager());
		r.setColor(org.andengine.util.color.Color.BLACK);
		r.setAlpha(0);
		AlphaModifier fader = new AlphaModifier(.3f, 0, 1, new IEntityModifierListener() {								
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}								
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {				
				if (destinationScene != null){
					((IGameScene) thisScene).Dispose();
					Resources.mEngine.setScene(destinationScene);
				}
			}
		});
		r.registerEntityModifier(fader);
		thisScene.attachChild(r);
	}
		
	public static HUD NewHud(Camera camera){
		HUD h = new HUD();
		camera.setHUD(h);
		return h;
	}
	
	public static void ResetCamera(SmoothCamera camera){
		camera.setChaseEntity(null);
		camera.setCenterDirect(Resources.CAMERA_WIDTH/2, Resources.CAMERA_HEIGHT/2);
		camera.setZoomFactorDirect(1f);
		camera.clearUpdateHandlers();
	}


	/** Gives the coordinates on the given sprite in meters
	 * @param iTextureRegion
	 * @param pixelVector
	 * @return
	 */
	public static Vector2 getBodyPoint(ITextureRegion iTextureRegion,	Vector2 pixelVector) {
		Sprite s = new Sprite(0, 0, iTextureRegion, Resources.mEngine.getVertexBufferObjectManager());
		
		return getBodyPoint(s, pixelVector);
	}
	
	/** Gives the coordinates on the given sprite in meters
	 * @param sBody
	 * @param pixelVector
	 * @return
	 */
	public static Vector2 getBodyPoint(Sprite sBody, Vector2 pixelVector){
		Vector2 result = new Vector2();
		
		float bodyX = (pixelVector.x - (sBody.getWidth()/2)) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float bodyY = (pixelVector.y - (sBody.getHeight()/2)) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		
		result.set(bodyX, bodyY);
		
		return result;		
	}

	public static FixtureDef createPolygonFixtureDef(Vector2[] vertices, FixtureDef def) {
		PolygonShape basePoly;
		basePoly = new PolygonShape();
		basePoly.set(vertices);
		def.shape = basePoly;
		
		return def;
	}
	
	public static float getPointDistance(Vector2 pointA, Vector2 pointB){
		float a = pointA.x - pointB.x;
		float b = pointA.y - pointB.y;
		float c = (float) Math.sqrt(Math.abs(a*a) + Math.abs(b*b));
		
		return c;
	}
	
	public static void CenterScreen(RectangularShape object){
		object.setPosition(Resources.CAMERA_WIDTH/2 - object.getWidth()/2, Resources.CAMERA_HEIGHT/2 - object.getHeight()/2);
	}
	public static void Center(RectangularShape parent, RectangularShape object){
		object.setPosition(parent.getWidth()/2 - object.getWidth()/2, parent.getHeight()/2 - object.getHeight()/2);
	}

	
}

