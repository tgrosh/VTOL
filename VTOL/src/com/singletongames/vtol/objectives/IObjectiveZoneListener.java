package com.singletongames.vtol.objectives;

import com.badlogic.gdx.physics.box2d.Body;

public interface IObjectiveZoneListener {
	public void onEnter(ObjectiveZone objectiveZone, Body body);
	public void onExit(ObjectiveZone objectiveZone, Body body);
}
