package com.oznecniV97.cubicExcavator;

import com.oznecniV97.cubicExcavator.client.KeyBindings;
import com.oznecniV97.cubicExcavator.handler.KeyInputHandler;
import com.oznecniV97.cubicExcavator.handler.TickHandler;
import com.oznecniV97.cubicExcavator.proxy.CommonProxy;
import com.oznecniV97.cubicExcavator.utilities.References;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = References.MODID, name = References.MOD_NAME, version = References.VERSION)
public class CubicExcavator {

//	@Instance
//	public CubicExcavator instance;

	@SidedProxy(clientSide = References.CLIENT_PROXY_CLASS, serverSide = References.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	@EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		KeyBindings.init();
		// aggiunta cattura tasti
		MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
		// aggiunta evento scan tick
		MinecraftForge.EVENT_BUS.register(new TickHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent event) {

	}

}
