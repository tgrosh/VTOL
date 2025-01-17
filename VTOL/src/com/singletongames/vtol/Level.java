package com.singletongames.vtol;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.IDisposable;
import org.andengine.util.debug.Debug;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Pair;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.singletongames.vtol.objectives.ObjectiveZone;

public class Level implements IDisposable {	
	private int mLevelID;
	private int mChapterID;
	private String name;
	private boolean mLevelLocked = true;
	private boolean mLevelFinished = false;
	private boolean mLevelGemCaptured = false;
	private int mLevelTime = 0;
	private TMXTiledMap map;
	Engine mEngine;
	private Lander lander;
	private LaunchPad launchPad;
	private LandingPad landingPad;
	private List<CargoDrop> cargoDrops = new ArrayList<CargoDrop>();
	private List<NoFlyZone> noFlyZones = new ArrayList<NoFlyZone>();
	private ScenePreview preview;
	private List<ObjectiveZone> objectiveZones = new ArrayList<ObjectiveZone>();
	
	public Level(Engine pEngine, int ChapterID, int pLevelID, String name, boolean pLevelLocked, boolean pLevelFinished, boolean pLevelGemCaptured, int pLevelTime) {		
		this.mLevelID = pLevelID;
		this.mChapterID = ChapterID;
		this.mEngine = pEngine;
		this.mLevelLocked = pLevelLocked;
		this.mLevelFinished = pLevelFinished;
		this.mLevelGemCaptured = pLevelGemCaptured;
		this.mLevelTime = pLevelTime;
		this.name = name;
	}

	public int getLevelID() {
		return mLevelID;
	}

	public boolean isLevelLocked() {
		return mLevelLocked;
	}

	public void setLevelLocked(boolean mLevelLocked) {
		this.mLevelLocked = mLevelLocked;
	}

	public boolean isLevelFinished() {
		return mLevelFinished;
	}

	public void setLevelFinished(boolean mLevelFinished) {
		this.mLevelFinished = mLevelFinished;
	}

	public boolean isLevelGemCaptured() {
		return mLevelGemCaptured;
	}

	public void setLevelGemCaptured(boolean mLevelGemCaptured) {
		this.mLevelGemCaptured = mLevelGemCaptured;
	}

	public int getLevelTime() {
		return mLevelTime;
	}

	public void setLevelTime(int mLevelTime) {
		this.mLevelTime = mLevelTime;
	}
	public TMXTiledMap getMap() {
		return map;
	}

	public void setMap(TMXTiledMap map) {
		this.map = map;
	}
	
	public static Level getNextLevel(){
		for (Level lvl: LevelDB.getInstance().allLevels()){
			if (lvl.getChapterID() == Resources.mCurrentLevel.getChapterID() && lvl.getLevelID() == Resources.mCurrentLevel.getLevelID() + 1){
				return lvl;
			}
		}
		
		return null;
	}

	public void Load(Scene scene, boolean DebugDraw, boolean physics, IScenePreviewListener scenePreviewListener){
		final TMXLoader tmxLoader = new TMXLoader(Resources.mActivity.getAssets(), mEngine.getTextureManager(), mEngine.getVertexBufferObjectManager());
		try {
			map = tmxLoader.loadFromAsset("levels/Level" + this.getChapterID() + "-" + this.getLevelID() + ".tmx");
		} catch (TMXLoadException e) {
			Debug.e(e);
			return;
		}	
		
		//background or other graphics. not objects
		for (TMXLayer layer: map.getTMXLayers()){
			layer.detachSelf();
			scene.attachChild(layer);
		}
		
		for (TMXObjectGroup grp:map.getTMXObjectGroups()){
			for (TMXObject obj:grp.getTMXObjects()){
				if (grp.getName().equals("Props") && physics){ //just draw props
					 ITextureRegion tex = map.getTextureRegionFromGlobalTileID(obj.getGid());
					 Sprite prop = new Sprite(obj.getX(), obj.getY() - 120f, tex, Resources.mEngine.getVertexBufferObjectManager());
					 scene.attachChild(prop);
				}
				else if (grp.getName().equals("Physics") && physics){ //box2d physics layer
					if (obj.getTMXObjectPolyline().isEmpty()){ //not a poly line, so a rectangle (we dotn support elipse)
						Rectangle rect = new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), Resources.mEngine.getVertexBufferObjectManager());
						rect.setVisible(false);
						PhysicsFactory.createBoxBody(Resources.mPhysicsWorld, rect, BodyType.StaticBody, PhysicsFactory.createFixtureDef(1000, 0, .2f)); //fixture def should be configurable per tiled rectangle
						scene.attachChild(rect);
					}
					else{
						LinkedList<Pair<Integer, Integer>> vertices = obj.getTMXObjectPolyline();
						if (vertices == null) {
							Debug.e("Invalide vertices for Polyline");
						}
						else{
							Vector2[] vectors = new Vector2[vertices.size()];
							for(int i=0; i < vertices.size(); i++){
								Pair<Integer, Integer> pair = vertices.get(i);								
								vectors[i] = new Vector2((pair.first+obj.getX())/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (pair.second+obj.getY())/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
							}
							
							ChainShape myChain = new ChainShape();
							myChain.createChain(vectors);
							
			                Body mChainBody;
			                
			                BodyDef mBodyDef = new BodyDef();
			                mBodyDef.type = BodyType.StaticBody;
			                mChainBody = Resources.mPhysicsWorld.createBody(mBodyDef);
			                
			                FixtureDef mFixtureDef = new FixtureDef();
			                mFixtureDef.shape = myChain;
			                mChainBody.createFixture(mFixtureDef);
			                
			                myChain.dispose();
						}
					}
				}
				else if (grp.getName().equals("Preview")){ //camera preview layer
					if (!obj.getTMXObjectPolyline().isEmpty()){ //is a polyline
						float startX = obj.getX();
						float startY = obj.getY();
						
						LinkedList<Pair<Integer,Integer>> previewPoints = obj.getTMXObjectPolyline();
						List<Vector2> newPoints = new ArrayList<Vector2>();
						
						for (Pair<Integer,Integer> point: previewPoints){
							newPoints.add(new Vector2(startX + point.first, startY + point.second));
						}
						
						preview = new ScenePreview(Resources.mEngine.getScene(), (SmoothCamera) Resources.mEngine.getCamera(), newPoints, scenePreviewListener);
					}
				}
				else if (grp.getName().equals("NoFly")){
					if (obj.getTMXObjectPolyline().isEmpty()){ //not a poly line, so a rectangle
						NoFlyZone noFly = new NoFlyZone(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());						
						scene.attachChild(noFly);
						this.noFlyZones.add(noFly);
					}
				}
				else if (grp.getName().equals("Objectives")){
					if (obj.getTMXObjectPolyline().isEmpty()){ //not a poly line, so a rectangle						
						String type = Util.getTMXObjectProperty(obj.getTMXObjectProperties(), "type", "");
						if (type.equals("ZoneObjective") || type.equals("ObjectiveZone")){
							int id = Util.getTMXObjectProperty(obj.getTMXObjectProperties(), "id", -1);
							if (id >= 0){
								ObjectiveZone zone = new ObjectiveZone(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), id);
								scene.attachChild(zone);
								objectiveZones.add(zone);
							}
						}
					}
				}
				else {
					int objGid = obj.getGid();
					if (objGid != 0){
						String tileProp = Util.getTMXTilePropertyValue(map, objGid, "type", "");
						if (!tileProp.equals("")){	
							if (tileProp.equals("CargoDrop")){
								int id = Util.getTMXObjectProperty(obj.getTMXObjectProperties(), "id", -1);
								CargoDrop drop = new CargoDrop(obj.getX() + 30 - Resources.CargoDrop.getWidth()/2, obj.getY() - Resources.CargoDrop.getHeight(), id, null);
								drop.setZIndex(15);
								cargoDrops.add(drop);
								scene.attachChild(drop);
							}
							else if (tileProp.equals("WoodenBox")){
								int id = Util.getTMXObjectProperty(obj.getTMXObjectProperties(), "id", -1);
								WoodenBox box = new WoodenBox(obj.getX() + 30 - Resources.WoodenBox.getWidth()/2, obj.getY() - 60f, id);
								box.setZIndex(20);
								scene.attachChild(box);
							}
							else if (tileProp.equals("LaunchPad")){
								LaunchPad pad = new LaunchPad(obj.getX() + 30 - Resources.LaunchPad.getWidth()/2, obj.getY() - 180, null);
								pad.setZIndex(10);
								scene.attachChild(pad);
								this.launchPad = pad;
							}
							else if (tileProp.equals("LandingPad")){
								int id = Util.getTMXObjectProperty(obj.getTMXObjectProperties(), "id", -1);
								LandingPad pad = new LandingPad(obj.getX() + 30 - Resources.LandingPad.getWidth()/2, obj.getY() - 60, id, null);
								pad.setZIndex(10);
								scene.attachChild(pad);
								this.landingPad = pad;
							}
							else if (tileProp.equals("Lander")){
								switch (Resources.selectedLander){
									case 0:{
										lander = new BasicLander(obj.getX() + 30 - Resources.landerHauler.getWidth()/2, obj.getY(), null);										
										break;
									}
									case 1:{
										lander = new LunarLander(obj.getX() + 30 - Resources.landerLuna.getWidth()/2, obj.getY(), null);
										break;
									}
									default:{
										lander = new BasicLander(obj.getX() + 30 - Resources.landerHauler.getWidth()/2, obj.getY(), null);
									}
								}
								lander.setZIndex(20);
								scene.attachChild(lander);								
							}
						}//if (tileProp != "")
					}//if (objGid != 0){	
				}
			}//for (TMXObject obj:grp.getTMXObjects())
		}//for (TMXObjectGroup grp
		
		
		if (DebugDraw){
			DebugRenderer debug = new DebugRenderer(Resources.mPhysicsWorld, mEngine.getVertexBufferObjectManager());
			debug.setZIndex(1000);
			scene.attachChild(debug);
		}
				
		scene.sortChildren();
	}

	public Lander getLander() {
		return lander;
	}

	public List<NoFlyZone> getNoFlyZones() {
		return noFlyZones;
	}

	@Override
	public boolean isDisposed() {
		return (map == null);
	}

	@Override
	public void dispose() throws AlreadyDisposedException {
		if (map == null) return;
		for (TMXLayer layer: map.getTMXLayers()){
			layer.dispose();
		}
		map = null;
	}

	public ScenePreview getPreview() {
		return preview;
	}

	public float getWidth(){
		return this.map.getTileColumns() * this.map.getTileWidth();
	}
	
	public float getHeight(){
		return this.map.getTileRows() * this.map.getTileHeight();
	}

	public int getChapterID() {
		return mChapterID;
	}

	public void setChapterID(int mChapterID) {
		this.mChapterID = mChapterID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LaunchPad getLaunchPad() {
		return launchPad;
	}

	public LandingPad getLandingPad() {
		return landingPad;
	}

	public List<ObjectiveZone> getObjectiveZones() {
		return objectiveZones;
	}

	public ObjectiveZone getObjectiveZone(int objectiveZoneId){
		for (ObjectiveZone z: objectiveZones){
			if (z.getId() == objectiveZoneId){
				return z;
			}
		}
		return null;
	}

	public List<CargoDrop> getCargoDrops() {
		return cargoDrops;
	}
}
