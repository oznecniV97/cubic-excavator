package com.oznecniV97.cubicExcavator.enums;

import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ToolsNeeded{
	PICKAXE(Items.STONE_PICKAXE),
	SHOVEL(Items.STONE_SHOVEL),
	TORCH(Item.getItemFromBlock(Blocks.TORCH)),
	COBBLESTONE(Item.getItemFromBlock(Blocks.COBBLESTONE)),
	BUCKET(Items.WATER_BUCKET),
	DIAMOND_PICKAXE(Items.DIAMOND_PICKAXE);
	private Item item;
	private ToolsNeeded(Item item){
		this.item = item;
	}
	public Item getItem(){
		return this.item;
	}
	public Set<String> getTools(){
		return this.getItem().getToolClasses(new ItemStack(item));
	}
	public boolean canHarvest(IBlockState state){
		boolean ret = false;
		if(PICKAXE.getTools().contains(state.getBlock().getHarvestTool(state))){
			ret = this.getItem().canHarvestBlock(state);
		}else{
			ret = this.getTools().contains(state.getBlock().getHarvestTool(state));
		}
		return ret;
	}
}