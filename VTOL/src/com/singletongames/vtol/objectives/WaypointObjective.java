package com.singletongames.vtol.objectives;

import com.badlogic.gdx.math.Vector2;
import com.singletongames.vtol.LanderScene;

public class WaypointObjective extends ZoneObjective {
	Vector2 center;
	
	public WaypointObjective(ObjectiveManager manager, LanderScene scene, int id, int objectiveZoneID, String description, Vector2 center, IObjectiveListener listener) {
		super(manager, scene, id, objectiveZoneID, description, true,listener);
		this.center = center;
	}

	public WaypointObjective(ObjectiveManager manager, LanderScene scene,int id, int objectiveZoneID, String description,int prerequisiteID, Vector2 center, IObjectiveListener listener) {
		super(manager, scene, id, objectiveZoneID, description, true,prerequisiteID, listener);
		this.center = center;
	}

	public Vector2 getCenter(){
		return center;
	}
}
