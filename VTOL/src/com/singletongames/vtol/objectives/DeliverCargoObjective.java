package com.singletongames.vtol.objectives;

import org.andengine.util.debug.Debug;

import com.singletongames.vtol.Cargo;
import com.singletongames.vtol.CargoDrop;
import com.singletongames.vtol.ILanderSceneListener;
import com.singletongames.vtol.LanderScene;
import com.singletongames.vtol.LandingPad;

public class DeliverCargoObjective extends Objective implements ILanderSceneListener {
	private int cargoId = -1;
	private int cargoDropId = -1;
	
	public DeliverCargoObjective(ObjectiveManager manager, LanderScene scene, int objectiveID, String description, int cargoId, int cargoDropId, boolean hidden, IObjectiveListener listener) {
		this(manager, scene, objectiveID, description,cargoId,cargoDropId, hidden, -1, listener);
	}

	public DeliverCargoObjective(ObjectiveManager manager, LanderScene scene, int objectiveID, String description, int cargoId, int cargoDropId, boolean hidden, int prerequisiteID, IObjectiveListener listener) {
		super(manager, scene, objectiveID, description, hidden, prerequisiteID, listener);
		this.cargoId = cargoId;
		this.cargoDropId = cargoDropId;
		scene.addListener(this);
	}

	@Override
	public void onThrottleChange(float currentThrottle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPreviewComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPreviewStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraLookComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLanderTakeoff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLanderTouchdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSafeLanding(LandingPad pad) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSafeReturn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLanderDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMissionFail() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMissionSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectiveComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectiveZoneEnter(ObjectiveZone objectiveZone) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectiveZoneExit(ObjectiveZone objectiveZone) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNoFlyEnter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNoFlyExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCargoPickup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCargoDeliver(Cargo cargo, CargoDrop drop) {
		Debug.w("DEBUG: Cargo Delivered: Cargo=" + cargo.getId() + ", CargoDrop=" + drop.getId());
		if (prerequisiteComplete && cargo.getId() == cargoId && drop.getId() == cargoDropId){
			setComplete();
		}
	}

	public int getCargoId() {
		return cargoId;
	}

	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}

	public int getCargoDropId() {
		return cargoDropId;
	}

	public void setCargoDropId(int cargoDropId) {
		this.cargoDropId = cargoDropId;
	}

}
