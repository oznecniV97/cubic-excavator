package com.oznecniV97.cubicExcavator.handler.tickStatus;

import com.oznecniV97.cubicExcavator.enums.BotStatus;
import com.oznecniV97.cubicExcavator.enums.ToolsNeeded;
import com.oznecniV97.cubicExcavator.utilities.Utilities;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlacingStatus extends TickStatus {
	
	private BlockPos placingBlockPos;
	private boolean canStart;

	public PlacingStatus() {
		this(ToolsNeeded.COBBLESTONE);
	}

	public PlacingStatus(ToolsNeeded item) {
		// salvo la posizione di dove sarà posizionato il blocco basandomi sul blocco che sto guardando e sulla sua facciata
		this(item, null);
	}
	
	public PlacingStatus(ToolsNeeded item, BlockPos placingBlockPos) {
		super();
		// salvo la posizione di dove sarà posizionato il blocco basandomi sul blocco che sto guardando e sulla sua facciata
		canStart = false;
		if(placingBlockPos==null){
			RayTraceResult oldTrace = Utilities.rayTrace(5, 1.0F);
			this.placingBlockPos = oldTrace.getBlockPos().add(oldTrace.sideHit.getDirectionVec());
		}else{
			this.placingBlockPos = placingBlockPos;
		}
		// imposto l'oggetto da utilizzare
		ItemStack itemS = new ItemStack(item.getItem());
		mc.player.inventory.setPickedItemStack(itemS);
	}
	
	@Override
	public void caseStatus(TickEvent.PlayerTickEvent event) {
		if(!canStart){
			//prendo il blocco dove piazzerei se lo facessi in questo momento
			RayTraceResult oldTrace = Utilities.rayTrace(5, 1.0F);
			BlockPos lookingBlock = oldTrace.getBlockPos().add(oldTrace.sideHit.getDirectionVec());
			//se il blocco è lo stesso di quello dove ho prefissato di piazzare, procedo
			canStart = lookingBlock.equals(placingBlockPos);
			if(canStart){
				// start using
				start(FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem, BotStatus.PLACING);
			}
		}else{
			// If the BlockPos saved isn't air anymore, the block is placed
			if (!Utilities.BlockUtils.isAir(placingBlockPos)) {
				canStart = false;
				//stop placing
				endCase();
			}
		}
	}
	
}
