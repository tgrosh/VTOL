package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.Body;

public class NoFlyListenerManager implements INoFlyListener {
	List<INoFlyListener> listeners = new ArrayList<INoFlyListener>();	
	public List<INoFlyListener> getListeners() {
		return listeners;
	}
	public void addListener(INoFlyListener listener) {
		this.listeners.add(listener);
	}

	static NoFlyListenerManager listener = new NoFlyListenerManager();	
	public static NoFlyListenerManager getInstance(){
		return listener;
	}
	@Override
	public void onEnter(Body body) {
		for (INoFlyListener listener: listeners){
			listener.onEnter(body);
		}
	}
	@Override
	public void onExit(Body body) {
		for (INoFlyListener listener: listeners){
			listener.onExit(body);
		}
	}
	
	
}
