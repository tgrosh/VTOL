package com.singletongames.vtol.objectives;

public interface IObjectiveListener {
	public void onComplete(Objective objective);
	public void onFail(Objective objective);
}
