package com.singletongames.vtol;

import java.util.Iterator;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.Shape;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;

public class GameScene extends Scene implements IGameScene {

	@Override
	public void Pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Dispose() {
		this.detachChildren();
		this.clearTouchAreas();
		
		Iterator<Body> allMyBodies = Resources.mPhysicsWorld.getBodies();
        while(allMyBodies.hasNext())
        {
             try {
                 final Body myCurrentBody = allMyBodies.next();
                 Resources.mEngine.runOnUpdateThread(new Runnable(){
                     @Override
                     public void run() {
                         Resources.mPhysicsWorld.destroyBody(myCurrentBody);                
                     }
                 });
            } catch (Exception e) {
                Debug.e(e);
            }
        }
 
        Iterator<Joint> allMyJoints = Resources.mPhysicsWorld.getJoints();
        while(allMyJoints.hasNext())
        {
             try {
                 final Joint myCurrentJoint = allMyJoints.next();
                 Resources.mEngine.runOnUpdateThread(new Runnable(){
                     @Override
                     public void run() {
                         Resources.mPhysicsWorld.destroyJoint(myCurrentJoint);                
                     }
                 });
            } catch (Exception e) {
                Debug.e(e);
            }
        }
                
        this.reset();
        this.detachSelf();
//        Resources.mPhysicsWorld.clearForces();
//        Resources.mPhysicsWorld.clearPhysicsConnectors();
//        Resources.mPhysicsWorld.reset();
	}

}
