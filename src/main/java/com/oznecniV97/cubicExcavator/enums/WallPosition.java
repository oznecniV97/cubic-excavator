package com.oznecniV97.cubicExcavator.enums;

public enum WallPosition {
	UPPER_LEFT, LOWER_LEFT, UPPER_RIGHT, LOWER_RIGHT;
	public boolean isUpper(){
		return this.toString().startsWith("UPPER_");
	}
	public boolean isLower(){
		return !isUpper();
	}
	public boolean isLeft(){
		return this.toString().endsWith("_LEFT");
	}
	public boolean isRight(){
		return !isLeft();
	}
}
