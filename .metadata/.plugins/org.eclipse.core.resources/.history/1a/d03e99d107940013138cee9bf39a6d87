package com.singletongames.vtol;

public class LanderInfo  {
	
	protected int id;
	protected String name;
	protected String description;
	protected float maxEngineThrust;
	protected float maxFuel;
	protected float fuelPerSecond;
	protected float toughness;
	protected float density;
	protected boolean locked;
		
	
	public LanderInfo(int id, String name, String description, float maxEngineThrust, float maxFuel, float fuelPerSecond, float toughness, float density, boolean locked) {
		this.id = id;
		this.maxEngineThrust = maxEngineThrust;
		this.maxFuel = maxFuel;
		this.fuelPerSecond = fuelPerSecond;
		this.toughness = toughness;
		this.density = density;
		this.name = name;
		this.description = description;
		this.locked = locked;
	}


	public float getMaxEngineThrust() {
		return maxEngineThrust;
	}
	public void setMaxEngineThrust(float maxEngineThrust) {
		this.maxEngineThrust = maxEngineThrust;
	}
	public float getMaxFuel() {
		return maxFuel;
	}
	public void setMaxFuel(float maxFuel) {
		this.maxFuel = maxFuel;
	}
	public float getFuelPerSecond() {
		return fuelPerSecond;
	}
	public void setFuelPerSecond(float fuelPerSecond) {
		this.fuelPerSecond = fuelPerSecond;
	}
	public float getToughness() {
		return toughness;
	}
	public void setToughnessf(float toughness) {
		this.toughness = toughness;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public float getDensity() {
		return density;
	}


	public void setDensity(float density) {
		this.density = density;
	}


	@Override
	public String toString() {
		return String.format("{ID: %1$s, Name: %2$s, maxEngineThrust: %3$s, maxFuel: %4$s, fuelPerSecond: %5$s, toughness: %6$s, density: %7$s}", String.valueOf(this.getId()), this.getName(), String.valueOf(this.getMaxEngineThrust()), String.valueOf(this.getMaxFuel()), String.valueOf(this.getFuelPerSecond()), String.valueOf(this.getToughness()), String.valueOf(this.getDensity()));
	}

	
	
}
