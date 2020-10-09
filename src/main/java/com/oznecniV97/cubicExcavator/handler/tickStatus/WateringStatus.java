package com.oznecniV97.cubicExcavator.handler.tickStatus;

import com.oznecniV97.cubicExcavator.enums.BotStatus;
import com.oznecniV97.cubicExcavator.enums.ToolsNeeded;
import com.oznecniV97.cubicExcavator.utilities.Utilities;
import com.oznecniV97.cubicExcavator.utilities.Utilities.BlockUtils;
import com.oznecniV97.cubicExcavator.utilities.Utilities.PlayerUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WateringStatus extends TickStatus {
	
	private boolean replaceBlock = false;
	private boolean placed = false;

	public WateringStatus(Boolean replaceBlock) {
		super();
		//salvo se il blocco deve essere rimpiazzato
		this.replaceBlock = replaceBlock;
		// imposto come oggetto utilizzato il secchio d'acqua
		ItemStack bucket = new ItemStack(ToolsNeeded.BUCKET.getItem());
		mc.player.inventory.setPickedItemStack(bucket);
		// start using
		start(FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem, BotStatus.WATERING);
	}
	
	@Override
	public void caseStatus(TickEvent.PlayerTickEvent event) {
		// Save actual looking block position
		BlockPos lookingBlockPos = Utilities.rayTrace(5, 1.0F, true).getBlockPos();
		if(placed){
			if(PlayerUtils.isInMainHand(ToolsNeeded.BUCKET.getItem())){
				placed = false;
				//se l'acqua era stata piazzata e ora è stata tolta
				if(replaceBlock){
					endCase(PlacingStatus.class);
				}else{
					endCase();
				}
			}
		}else if(BlockUtils.isWater(lookingBlockPos)){
			//controllo che l'acqua sia stata piazzata
			placed = true;
		}
	}
	
}
