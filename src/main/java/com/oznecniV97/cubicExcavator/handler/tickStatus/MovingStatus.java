package com.oznecniV97.cubicExcavator.handler.tickStatus;

import com.oznecniV97.cubicExcavator.enums.BotStatus;
import com.oznecniV97.cubicExcavator.enums.Direction;
import com.oznecniV97.cubicExcavator.utilities.Utilities;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MovingStatus extends TickStatus {
	
	private Direction direction;
	private BlockPos oldPlayerPos = null;

	public MovingStatus(boolean untilStop) {
		this(untilStop, Direction.FRONT);
	}
	
	public MovingStatus(boolean untilStop, Direction dir) {
		super();
		// Salvo la posizione verso la quale devo andare
		direction = dir;
		// Se non devo continuare finché non trovo uno stop, setto la posizione attuale
		if (!untilStop) {
			oldPlayerPos = mc.player.getPosition();
		}
		// start moving
		start(dir.getKeyBinding(), BotStatus.MOVING);
	}
	
	@Override
	public void caseStatus(TickEvent.PlayerTickEvent event) {
		if (oldPlayerPos != null) {
			if (!oldPlayerPos.equals(mc.player.getPosition())) {
				// stop moving
				endCase();
			}
		} else {
			// controllo se posso andare avanti
			if (!Utilities.PlayerUtils.canMove(direction)) {
				// stop moving
				endCase();
			}
		}
	}
	
}
