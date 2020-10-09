package com.oznecniV97.cubicExcavator.handler;

import com.oznecniV97.cubicExcavator.handler.tickStatus.TickStatus;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickHandler {

	// Called when the client tick
	@SubscribeEvent
	public void onClientTick(TickEvent.PlayerTickEvent event) {
		if(TickStatus.status!=null){
			TickStatus.status.caseStatus(event);
		}
	}
	
}