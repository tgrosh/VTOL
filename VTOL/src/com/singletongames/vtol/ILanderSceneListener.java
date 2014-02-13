package com.singletongames.vtol;

import com.singletongames.vtol.objectives.ObjectiveZone;

public interface ILanderSceneListener {
	public void onThrottleChange(float currentThrottle);
	public void onPreviewComplete();
	public void onPreviewStart();
	public void onCameraLookComplete();
	public void onLanderTakeoff();
	public void onLanderTouchdown();
	public void onSafeLanding(LandingPad pad);
	public void onSafeReturn();
	public void onLanderDestroy();
	public void onMissionFail();
	public void onMissionSuccess();
	public void onObjectiveComplete();
	public void onObjectiveZoneEnter(ObjectiveZone objectiveZone);
	public void onObjectiveZoneExit(ObjectiveZone objectiveZone);
	public void onNoFlyEnter();
	public void onNoFlyExit();
	public void onCargoPickup();
	public void onCargoDeliver();
}
