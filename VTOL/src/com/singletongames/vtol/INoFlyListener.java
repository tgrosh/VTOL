package com.singletongames.vtol;

import com.badlogic.gdx.physics.box2d.Body;

public interface INoFlyListener {
	public void onEnter(Body body);
	public void onExit(Body body);
}
