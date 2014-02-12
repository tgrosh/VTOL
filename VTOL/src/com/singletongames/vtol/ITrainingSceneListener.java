package com.singletongames.vtol;

public interface ITrainingSceneListener {
	public void onThrottleIncreased();
	public void onLanderTakeoff();
	public void onObjectiveReached(int index);
}
