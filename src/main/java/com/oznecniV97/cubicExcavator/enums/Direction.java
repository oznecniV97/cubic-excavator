package com.oznecniV97.cubicExcavator.enums;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.client.FMLClientHandler;

public enum Direction {
	UP,
	DOWN,
	LEFT(FMLClientHandler.instance().getClient().gameSettings.keyBindLeft),
	RIGHT(FMLClientHandler.instance().getClient().gameSettings.keyBindRight),
	FRONT(FMLClientHandler.instance().getClient().gameSettings.keyBindForward),
	BACK(FMLClientHandler.instance().getClient().gameSettings.keyBindBack);
	private static Minecraft mc = Minecraft.getMinecraft();
	private net.minecraft.client.settings.KeyBinding key;
	private Direction(){
		this.key = null;
	}
	private Direction(net.minecraft.client.settings.KeyBinding key){
		this.key = key;
	}
	public net.minecraft.client.settings.KeyBinding getKeyBinding(){
		return this.key;
	}
	public Vec3i getVector(){
		return this.getVector(mc.player);
	}
	public Vec3i getVector(Entity entity){
		Vec3d avanti = entity.getForward();
		int x = 0;
		int z = 0;
		switch(this){
		case LEFT:
			if(Math.abs(avanti.x)>=Math.abs(avanti.z)){
				z = avanti.x>=0 ? -1 : 1;
			}else{
				x = avanti.z>=0 ? 1 : -1;
			}
			return new Vec3i(x, 0, z);
		case RIGHT:
			Vec3i left = LEFT.getVector(entity);
			return new Vec3i(-1* (int)left.getX(), 0, -1* (int)left.getZ());
		case FRONT:
			if(Math.abs(avanti.x)>=Math.abs(avanti.z)){
				x = avanti.x>=0 ? 1 : -1;
			}else{
				z = avanti.z>=0 ? 1 : -1;
			}
			return new Vec3i(x, 0, z);
		case BACK:
			Vec3i front = FRONT.getVector(entity);
			return new Vec3i(-1* (int)front.getX(), 0, -1* (int)front.getZ());
		case UP:
			return new Vec3i(0, 1, 0);
		case DOWN:
			return new Vec3i(0, -1, 0);
		default:
			return null;
		}
	}
}
