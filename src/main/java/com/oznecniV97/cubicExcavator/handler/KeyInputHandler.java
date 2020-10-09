package com.oznecniV97.cubicExcavator.handler;

import com.oznecniV97.cubicExcavator.Excavator;
import com.oznecniV97.cubicExcavator.Fisherman;
import com.oznecniV97.cubicExcavator.client.KeyBindings;
import com.oznecniV97.cubicExcavator.client.PlayerMovements;
import com.oznecniV97.cubicExcavator.utilities.Utilities.CommandUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputHandler {

	public static Logger log = LogManager.getLogger(KeyInputHandler.class);
	static int num = 0;

	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(KeyBindings.pesca.isPressed()){
        	//start pesca
        	Fisherman.getInstance().startOrStop();
        }
        if(KeyBindings.right.isPressed()){
//        	Utilities.PlayerUtils.lookInternalWallPos(WallPosition.UPPER_RIGHT);
//        	IBlockState lb = Minecraft.getMinecraft().world.getBlockState(Utilities.rayTrace(5, 1.0F).getBlockPos());
//        	ItemStack curItem = Minecraft.getMinecraft().player.inventory.getCurrentItem();
//        	log.info("Item: "+curItem+" - Harvest: "+curItem.canHarvestBlock(lb));
//        	TickStatus.status = new WateringStatus(true);
//        	PlayerMovements.turnDown(550F);
//        	Minecraft mc = Minecraft.getMinecraft();
//        	Vec3i front = Direction.FRONT.getVector();
//    		int rot;
//    		if (front.getX() != 0) {
//    			rot = front.getX() > 0 ? 270 : 90;
//    		} else {
//    			rot = front.getZ() > 0 ? 0 : 180;
//    		}
//    		mc.player.setPositionAndRotationDirect(mc.player.posX, mc.player.posY, mc.player.posZ, rot, 85, 1, false);
        	KeyBinding.setKeyBindState(FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem.getKeyCode(), true);
        }
        if(KeyBindings.up.isPressed()){
        	//continua dritto finché non trovi un muro
        	Minecraft.getMinecraft().setIngameNotInFocus();
        	PlayerMovements.goForwardUntilStop();
        }
        if(KeyBindings.down.isPressed()){
        	Minecraft mc = Minecraft.getMinecraft();
        	EntityPlayerSP pl = mc.player;
    		ICommandSender sender = pl.getCommandSenderEntity();
    		CommandUtils com = new CommandUtils(sender);
        	EntityFishHook efh = new EntityFishHook(mc.world, pl);
        	com.printCommandMessage(efh.caughtEntity!=null);
        	log.debug("Pesce preso: " + efh.caughtEntity!=null);
//        	Minecraft mc = Minecraft.getMinecraft();
        	//drop dello slot 1 dall'inventario
//        	mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
//        	mc.playerController.windowClick(mc.player.inventoryContainer.windowId, -999, 0, ClickType.PICKUP, mc.player);
        }
        if(KeyBindings.rompi.isPressed()){
	        Excavator.getInstance().startOrStop();
        }
    }

}
