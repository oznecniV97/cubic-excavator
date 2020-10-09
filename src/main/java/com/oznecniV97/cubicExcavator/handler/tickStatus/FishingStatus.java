package com.oznecniV97.cubicExcavator.handler.tickStatus;

import com.oznecniV97.cubicExcavator.enums.BotStatus;
import com.oznecniV97.cubicExcavator.enums.ToolsNeeded;
import com.oznecniV97.cubicExcavator.enums.WallPosition;
import com.oznecniV97.cubicExcavator.utilities.Utilities;
import com.oznecniV97.cubicExcavator.utilities.Utilities.CommandUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FishingStatus extends TickStatus {
	
	private boolean finito;
	private boolean iniziato;
	private EntityFishHook efh;
	private CommandUtils com;
	
	public FishingStatus() {
		super();
		this.finito = false;
		this.iniziato = false;
		Minecraft mc = Minecraft.getMinecraft();
    	EntityPlayerSP pl = mc.player;
		ICommandSender sender = pl.getCommandSenderEntity();
		com = new CommandUtils(sender);
    	efh = new EntityFishHook(mc.world, pl);
	}
	
	@Override
	public void caseStatus(TickEvent.PlayerTickEvent event) {
		if(efh.caughtEntity==null){
			if(!iniziato){
				iniziato = true;
				// start breaking
				start(FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem, BotStatus.FISHING);
			}else{
				KeyBinding.setKeyBindState(key.getKeyCode(), false);
//				KeyBinding.unPressAllKeys();
			}
		}else{
			com.printCommandMessage("PESCATO!");
			endCase();
		}
	}

}
