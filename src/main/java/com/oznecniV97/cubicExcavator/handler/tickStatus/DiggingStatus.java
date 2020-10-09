package com.oznecniV97.cubicExcavator.handler.tickStatus;

import com.oznecniV97.cubicExcavator.enums.BotStatus;
import com.oznecniV97.cubicExcavator.enums.ToolsNeeded;
import com.oznecniV97.cubicExcavator.enums.WallPosition;
import com.oznecniV97.cubicExcavator.utilities.Utilities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DiggingStatus extends TickStatus {
	
	private BlockPos oldLookingBlock;
	private Boolean needWater = false;
	private WallPosition wPos;

	public DiggingStatus(Boolean needWater, WallPosition wPos) {
		super();
		this.oldLookingBlock = null;
		// salvo se ho bisogno di acqua e se il blocco deve essere rimpiazzato
		this.needWater = needWater;
//		this.replaceBlock = wPos.isLower();
		this.wPos = wPos;
	}
	
	@Override
	public void caseStatus(TickEvent.PlayerTickEvent event) {
		if(oldLookingBlock == null){
			if(Utilities.PlayerUtils.isLookingAt(wPos)){
				// settare strumento adeguato in base al blocco che mi trovo avanti
				IBlockState lb = mc.world.getBlockState(Utilities.rayTrace(5, 1.0F).getBlockPos());
				ToolsNeeded tool = Utilities.DigUtils.getBestTool(lb);
				mc.player.inventory.setPickedItemStack(new ItemStack(tool.getItem()));
				// salvo la posizione del blocco che sto guardando
				oldLookingBlock = Utilities.rayTrace(5, 1.0F).getBlockPos();
				// start breaking
				start(FMLClientHandler.instance().getClient().gameSettings.keyBindAttack, BotStatus.DIGGING);
			}
		}else{
			// Save actual looking block position
			BlockPos newLookingBlock = Utilities.rayTrace(5, 1.0F).getBlockPos();
			if (!oldLookingBlock.equals(newLookingBlock)) {
				// stop breaking
				if(needWater){
					endCase(WateringStatus.class, wPos.isLower());
				}else{
					endCase();
				}
			}
		}
	}

}
