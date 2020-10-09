package com.oznecniV97.cubicExcavator.enums;

public enum BotStatus {
	INACTIVE, // used to inactivate the event handler
	DIGGING, // used when the player break the block in front of it
	MOVING, // used when the player is moving
	WATERING, // used when the player use the water bucket
	PLACING, // used when the player place a block
	SEARCHING, // TODO used when the player go to search the nearest chest with wood inside
	EMPTYING, // TODO used when I need to empty my inventory from the rubbish (need rubbish list)
	CRAFTING, // TODO nun sacc si serv
	FISHING; // used when the player start to fishing
}