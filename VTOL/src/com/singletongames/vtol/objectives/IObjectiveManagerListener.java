package com.singletongames.vtol.objectives;

public interface IObjectiveManagerListener {
	public void onAllObjectivesComplete();
	public void onObjectiveComplete(Objective objective);
	public void onObjectiveFail(Objective objective);
}
