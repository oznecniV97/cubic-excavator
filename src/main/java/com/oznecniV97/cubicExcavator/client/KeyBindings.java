package com.oznecniV97.cubicExcavator.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {

	public static KeyBinding pesca;
	public static KeyBinding right;
	public static KeyBinding up;
	public static KeyBinding down;
	public static KeyBinding rompi;

	public static void init() {
		pesca = new KeyBinding("key.pesca", Keyboard.KEY_P, "key.categories.misc");
		right = new KeyBinding("key.tieniPremutoAzione", Keyboard.KEY_L, "key.categories.misc");
		up = new KeyBinding("key.turnUp", Keyboard.KEY_I, "key.categories.misc");
		down = new KeyBinding("key.turnDown", Keyboard.KEY_K, "key.categories.misc");
		rompi = new KeyBinding("key.rompi", Keyboard.KEY_B, "key.categories.misc");

		// Register both KeyBindings to the ClientRegistry
		ClientRegistry.registerKeyBinding(pesca);
		ClientRegistry.registerKeyBinding(right);
		ClientRegistry.registerKeyBinding(up);
		ClientRegistry.registerKeyBinding(down);
		ClientRegistry.registerKeyBinding(rompi);
	}

}
