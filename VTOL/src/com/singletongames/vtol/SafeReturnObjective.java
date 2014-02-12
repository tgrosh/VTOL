package com.singletongames.vtol;

import org.andengine.util.debug.Debug;

public class SafeReturnObjective extends Objective {
	
	
	public SafeReturnObjective(LanderScene scene, int objectiveID, String description, IObjectiveListener listener) {
		super(scene, objectiveID, description, listener);
		
		Load();
	}

	public SafeReturnObjective(LanderScene scene, int objectiveID, String description, int prerequisiteID, IObjectiveListener listener) {
		super(scene, objectiveID, description, prerequisiteID, listener);
		
		Load();
	}
	
	private void Load() {
		scene.addListener(new ILanderSceneListener() {			
			@Override
			public void onThrottleChange(float currentThrottle) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSafeLanding(LandingPad pad) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPreviewStart() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPreviewComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onObjectiveZoneExit(ObjectiveZone objZone) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onObjectiveZoneEnter(ObjectiveZone objZone) {
				
			}
			
			@Override
			public void onObjectiveComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onNoFlyExit() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onNoFlyEnter() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMissionSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMissionFail() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLanderTouchdown() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLanderTakeoff() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLanderDestroy() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCameraLookComplete() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSafeReturn() {		
				SafeReturnObjective.super.setComplete();
			}

			@Override
			public void onCargoPickup() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCargoDeliver() {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
