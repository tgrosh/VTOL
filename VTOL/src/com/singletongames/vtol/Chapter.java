package com.singletongames.vtol;

import java.util.List;

public class Chapter {
	private String Name;
	private int Id;
	private boolean Locked;
	private boolean Finished;
	
	public Chapter(String name, int id, boolean locked, boolean finished) {
		Name = name;
		Id = id;
		setLocked(locked);
		setFinished(finished);
	}


	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}


	public boolean isLocked() {
		return Locked;
	}


	public void setLocked(boolean locked) {
		Locked = locked;
	}


	public boolean isFinished() {
		return Finished;
	}


	public void setFinished(boolean finished) {
		Finished = finished;
	}


	public List<Level> getLevels() {
		return LevelDB.getInstance().getLevels(Id);
	}
	
}
