package com.oznecniV97.cubicExcavator.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oznecniV97.cubicExcavator.enums.Direction;
import com.oznecniV97.cubicExcavator.enums.WallPosition;
import com.oznecniV97.cubicExcavator.handler.tickStatus.DiggingStatus;
import com.oznecniV97.cubicExcavator.handler.tickStatus.MovingStatus;
import com.oznecniV97.cubicExcavator.handler.tickStatus.TickStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3i;

public class PlayerMovements {

	public static Logger log = LogManager.getLogger(PlayerMovements.class);
	private static Minecraft mc = Minecraft.getMinecraft();
	private static EntityPlayerSP pl = mc.player;

	private static final float stdRotUp = 50.0F;
	private static final float stdRotDown = 50.0F;
	private static final float stdRotLeft = 600.0F;
	private static final float stdRotRight = 600.0F;

	// ----------------------------------------------Turn player

	public static void resetTurn() {
		Vec3i front = Direction.FRONT.getVector();
		int rot;
		if (front.getX() != 0) {
			rot = front.getX() > 0 ? 270 : 90;
		} else {
			rot = front.getZ() > 0 ? 0 : 180;
		}
		mc.player.setPositionAndRotationDirect(mc.player.posX, mc.player.posY, mc.player.posZ, rot, 0, 1, false);
	}

	public static void turnUp() {
		turnUp(stdRotUp);
	}

	public static void turnUp(float rotation) {
		mc.player.turn(0, rotation);
	}

	public static void turnDown() {
		turnDown(stdRotDown);
	}

	public static void turnDown(float rotation) {
		mc.player.turn(0, -1 * rotation);
	}
	
	public static void turnDownFixed(int rotation) {
		Vec3i front = Direction.FRONT.getVector();
		int rot;
		if (front.getX() != 0) {
			rot = front.getX() > 0 ? 270 : 90;
		} else {
			rot = front.getZ() > 0 ? 0 : 180;
		}
		mc.player.setPositionAndRotationDirect(mc.player.posX, mc.player.posY, mc.player.posZ, rot, rotation, 1, false);
	}

	public static void turnLeft() {
		turnLeft(stdRotLeft);
	}

	public static void turnLeft(float rotation) {
		mc.player.turn(-1 * rotation, 0);
	}

	public static void turnRight() {
		turnRight(stdRotRight);
	}

	public static void turnRight(float rotation) {
		mc.player.turn(rotation, 0);
	}

	// ----------------------------------------------Move player

	public static void goForwardUntilStop() {
		TickStatus.status = new MovingStatus(true, Direction.FRONT);
	}

	/** <b>ONLY FOR 1 BLOCK MOVEMENT<b> */
	public static void goToBlockForward() {
		TickStatus.status = new MovingStatus(false, Direction.FRONT);
	}

	public static void goBackwardUntilStop() {
		TickStatus.status = new MovingStatus(true, Direction.BACK);
	}

	/** <b>ONLY FOR 1 BLOCK MOVEMENT<b> */
	public static void goToBlockBackward() {
		TickStatus.status = new MovingStatus(false, Direction.BACK);
	}

	public static void goToLeftUntilStop() {
		TickStatus.status = new MovingStatus(true, Direction.LEFT);
	}

	/** <b>ONLY FOR 1 BLOCK MOVEMENT<b> */
	public static void goToLeftBlock() {
		TickStatus.status = new MovingStatus(false, Direction.LEFT);
	}

	public static void goToRightUntilStop() {
		TickStatus.status = new MovingStatus(true, Direction.RIGHT);
	}

	/** <b>ONLY FOR 1 BLOCK MOVEMENT<b> */
	public static void goToRightBlock() {
		TickStatus.status = new MovingStatus(false, Direction.RIGHT);
	}

	// ----------------------------------------------Player actions

	public static void breakLookingBlock(WallPosition wPos, boolean needWater) {
		// setDigging riceve se ha bisogno di acqua e se il blocco è sotto (per capire se deve rimpiazzarlo)
		TickStatus.status = new DiggingStatus(needWater, wPos);
	}

	public static void lookWallPos(WallPosition wPos, boolean needWater) {
		if(needWater){
			//se ho bisogno di acqua, invoco il metodo che mi fa guardare la parete interna
			lookInternalWallPos(wPos);
		}else{
			//se non ho bisogno di acqua, invoco il metodo che mi fa guardare il blocco diretto
			lookWallPos(wPos);
		}
	}
	
	/**metodo che guarda il blocco direttamente sulla parete esterna senza pensare a cosa potrebbe esserci dietro (serve in caso di no lava)*/
	private static void lookWallPos(WallPosition wp) {
		// set this coords based on where I need to look
		int y = wp.isUpper() ? 0 : 60;
		// rotation based on where I am actually rotated
		Vec3i front = Direction.FRONT.getVector();
		int rot;
		if (front.getX() != 0) {
			rot = front.getX() > 0 ? 270 : 90;
		} else {
			rot = front.getZ() > 0 ? 0 : 180;
		}
		pl.setPositionAndRotationDirect(pl.posX, pl.posY, pl.posZ, rot, y, 1, false);
	}
	
	/**metodo che guarda il blocco nella parete interna (serve in caso di lava)*/
	private static void lookInternalWallPos(WallPosition wp) {
		// set this coords based on where I am an where I need to look
		int x = 0, y = 0;
		switch (wp) {
		case LOWER_LEFT:
			x = 35;
			y = 45;
			break;
		case LOWER_RIGHT:
			x = -35;
			y = 45;
			break;
		case UPPER_LEFT:
		case UPPER_RIGHT:
			y=30;
			break;
		}
		// rotation based on where I am actually rotated
		Vec3i front = Direction.FRONT.getVector();
		int rot;
		if (front.getX() != 0) {
			rot = front.getX() > 0 ? 270 : 90;
		} else {
			rot = front.getZ() > 0 ? 0 : 180;
		}
		pl.setPositionAndRotationDirect(pl.posX, pl.posY, pl.posZ, rot + x, y, 1, false);
	}

}
