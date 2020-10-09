package com.oznecniV97.cubicExcavator.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oznecniV97.cubicExcavator.enums.Direction;
import com.oznecniV97.cubicExcavator.enums.ToolsNeeded;
import com.oznecniV97.cubicExcavator.enums.WallPosition;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;

public class Utilities {
	
	public static Logger log = LogManager.getLogger(Utilities.class);
	private static Minecraft mc = Minecraft.getMinecraft();

	public static class Integer {

		public static boolean tryParse(String s) {
			boolean ret;
			try {
				java.lang.Integer.parseInt(s);
				ret = true;
			} catch (Exception e) {
				ret = false;
			}
			return ret;
		}

	}
	
	public static class ListUtils {
		
		public static <T> List<T> fromSetToList(Set<T> set){
			return new ArrayList<T>(set);
		}
		
		public static <T> Set<T> fromListToSet(List<T> list){
			return new HashSet<T>(list);
		}
		
	}

	public static class CommandUtils {

		private ICommandSender sender;

		public CommandUtils(ICommandSender sender) {
			this.sender = sender;
		}

		public void printCommandMessage(Object obj) {
			printCommandMessage(obj.toString());
		}

		public void printCommandMessage(String message) {
			sender.sendMessage(new TextComponentString(message));
		}

	}

	public static class PlayerUtils {
		
		private static EntityPlayerSP pl = mc.player;

		public static NonNullList<ItemStack> getPlayerHotbar() {
			return getPlayerHotbar(pl.inventory.mainInventory);
		}

		public static NonNullList<ItemStack> getPlayerHotbar(NonNullList<ItemStack> playerInventory) {
			NonNullList<ItemStack> hotbar = NonNullList.create();
			for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
				hotbar.add(playerInventory.get(i));
			}
			return hotbar;
		}
		
		public static BlockPos getNotRoundedPosition(){
			int x = MathHelper.floor(pl.posX);
			int y = MathHelper.floor(pl.posY);
			int z = MathHelper.floor(pl.posZ);
			return new BlockPos(x, y, z);
		}
		
		public static boolean canMove(Direction dir){
			Vec3i vect = dir.getVector();
			BlockPos pos = getNotRoundedPosition().add(vect);
			BlockPos posUp = pos.add(0,1,0);
			return mc.world.getBlockState(pos).getBlock().isPassable(mc.world, pos) && mc.world.getBlockState(posUp).getBlock().isPassable(mc.world, posUp);
		}

		// TODO fare qualcosa nel caso di wPos = null
		public static boolean isInPosition(WallPosition wPos) {
			return canMove(Direction.RIGHT) == wPos.isLeft();
		}
		
		public static boolean isLookingAt(WallPosition wPos){
			BlockPos lookingBlock = Utilities.rayTrace(5, 1.0F).getBlockPos();
			BlockPos frPos = getNotRoundedPosition().add(Direction.FRONT.getVector());
			BlockPos blPos = null;
			switch(wPos){
			case LOWER_LEFT:
				if(canMove(Direction.RIGHT)){
					blPos = frPos;
				}else{
					blPos = frPos.add(Direction.LEFT.getVector());
				}
				break;
			case LOWER_RIGHT:
				if(canMove(Direction.RIGHT)){
					blPos = frPos.add(Direction.RIGHT.getVector());
				}else{
					blPos = frPos;
				}
				break;
			case UPPER_LEFT:
				if(canMove(Direction.RIGHT)){
					blPos = frPos.add(Direction.UP.getVector());
				}else{
					blPos = frPos.add(Direction.UP.getVector()).add(Direction.LEFT.getVector());
				}
				break;
			case UPPER_RIGHT:
				if(canMove(Direction.RIGHT)){
					blPos = frPos.add(Direction.UP.getVector()).add(Direction.RIGHT.getVector());
				}else{
					blPos = frPos.add(Direction.UP.getVector());
				}
				break;
			}
			return blPos.equals(lookingBlock);
		}
		
		public static boolean isInMainHand(Item item){
			return pl.inventory.getCurrentItem().getItem().equals(item);
		}
		
		public static boolean isInOffHand(Item item){
			return isInInventory(pl.inventory.offHandInventory, item);
		}
		
		public static boolean isInHotbar(Item item){
			return isInInventory(getPlayerHotbar(), item);
		}
		
		public static boolean isInInventory(Item item){
			return isInInventory(pl.inventory.mainInventory, item);
		}
		
		private static boolean isInInventory(NonNullList<ItemStack> inv, Item item){
        	for(ItemStack h:inv){
        		if(h.getItem().equals(item)){
        			return true;
        		}
        	}
        	return false;
		}
		
	}
	
	public static class BlockUtils{
		
		private static WorldClient wl = mc.world;
		
		public static boolean isAir(BlockPos pos) {
			return isBlock(pos, Blocks.AIR);
		}
		
		public static boolean isLava(BlockPos pos) {
			return isBlock(pos, Blocks.LAVA) || isBlock(pos, Blocks.FLOWING_LAVA);
		}
		
		public static boolean isWater(BlockPos pos) {
			return isBlock(pos, Blocks.WATER) || isBlock(pos, Blocks.FLOWING_WATER);
		}
		
		public static boolean isIronTrapdoor(BlockPos pos) {
			return isBlock(pos, Blocks.IRON_TRAPDOOR);
		}
		
		private static boolean isBlock(BlockPos pos, Block block) {
			return wl.getBlockState(pos).getBlock().equals(block);
		}
		
	}
	
	public static class DigUtils{
		
		public static boolean wallFinished(Map<BlockPos, ?> map) {
			return wallFinished(map.keySet());
		}
		
		public static boolean wallFinished(Set<BlockPos> sPos) {
			return wallFinished(ListUtils.fromSetToList(sPos));
		}
		
		public static boolean wallFinished(List<BlockPos> lPos) {
			boolean ret = true;
			if(lPos!=null && lPos.size()>0){
				for(int i = 0;ret && i<lPos.size();i++){
					ret = Utilities.BlockUtils.isAir(lPos.get(i));
				}
			}else{
				ret = false;
			}
			return ret;
		}
		
		public static boolean needWater(Map<BlockPos, ?> map) {
			return needWater(map.keySet());
		}
		
		public static boolean needWater(Set<BlockPos> sPos) {
			return needWater(ListUtils.fromSetToList(sPos));
		}
		
		public static boolean needWater(List<BlockPos> lPos) {
			boolean ret = false;
			if(lPos!=null && lPos.size()>0){
				for(BlockPos pos:lPos){
					ret = needWater(pos);
					if(ret)
						return true;
				}
			}
			return ret;
		}
		
		public static boolean needWater(BlockPos pos) {
			return Utilities.BlockUtils.isLava(pos.add(Direction.FRONT.getVector())) 
					|| Utilities.BlockUtils.isLava(pos.add(Direction.RIGHT.getVector())) 
					|| Utilities.BlockUtils.isLava(pos.add(Direction.LEFT.getVector()))
					|| Utilities.BlockUtils.isLava(pos.add(Direction.DOWN.getVector()))
					|| Utilities.BlockUtils.isLava(pos.add(Direction.UP.getVector()));
		}
		
		/**Ritorna la posizione (wall based) del blocco da rompere basata sulla presenza o meno di lava*/
		public static WallPosition getWallBlock(Map<BlockPos, WallPosition> map, boolean theresLava) {
			WallPosition wp = null;
			for (BlockPos key : map.keySet()) {
				if (!BlockUtils.isAir(key)) {
					if (theresLava) {
						// caso in cui c'è della lava
						if(needWater(key)){
							return map.get(key);
						}
					} else {
						// caso in cui non c'è lava
						return map.get(key);
					}
				}
			}
			return wp;
		}

		public static boolean isLavaOnTop(Map<BlockPos, WallPosition> wallMap) {
			boolean ret = false;
			for(BlockPos key:wallMap.keySet()){
				WallPosition wp = wallMap.get(key);
				if(wp.isUpper()){
					ret = Utilities.BlockUtils.isLava(key.add(Direction.UP.getVector()));
				}
				if(ret)
					return true;
			}
			return ret;
		}
		
		public static ToolsNeeded getBestTool(IBlockState state){
			for(ToolsNeeded tool:ToolsNeeded.values()){
				log.info("check on "+tool);
				if(tool.canHarvest(state)){
					log.info("OK");
					return tool;
				}
			}
			log.error("no item found for this block: "+state);
			return null;
		}
		
	}
	
	public static RayTraceResult rayTrace(double blockReachDistance, float partialTicks){
        return rayTrace(blockReachDistance, partialTicks, false);
	}
	
	public static RayTraceResult rayTrace(double blockReachDistance, float partialTicks, boolean stopOnLiquid){
        return rayTrace(mc.player, blockReachDistance, partialTicks, stopOnLiquid);
	}
	
	public static RayTraceResult rayTrace(Entity entity, double blockReachDistance, float partialTicks, boolean stopOnLiquid){
		Vec3d vec3d = entity.getPositionEyes(partialTicks);
        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return entity.world.rayTraceBlocks(vec3d, vec3d2, stopOnLiquid, false, true);
	}

//	public static class VectorUtils {
//
//		public static class Vec3d extends net.minecraft.util.math.Vec3d {
//
//			public Vec3d(double xIn, double yIn, double zIn) {
//				super(xIn, yIn, zIn);
//			}
//			
//			public Vec3d(Vec3i vector){
//		        super(vector);
//		    }
//
//		}
//
//	}
	
}
