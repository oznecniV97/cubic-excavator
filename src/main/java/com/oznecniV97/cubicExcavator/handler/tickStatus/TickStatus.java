package com.oznecniV97.cubicExcavator.handler.tickStatus;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oznecniV97.cubicExcavator.Excavator;
import com.oznecniV97.cubicExcavator.enums.BotStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public abstract class TickStatus {
	
	//public part
	public static TickStatus status = null;
	
	//private part
	private static Logger log = LogManager.getLogger(TickStatus.class);
	static Minecraft mc = Minecraft.getMinecraft();
	static BotStatus state = BotStatus.INACTIVE;
	static KeyBinding key = null;
	
	public abstract void caseStatus(TickEvent.PlayerTickEvent event);
	
	public BotStatus getState() {
		return state;
	}
	
	void start(KeyBinding k, BotStatus s){
		// press and save key
		key = k;
		KeyBinding.setKeyBindState(key.getKeyCode(), true);
		// set status
		state = s;
	}
	
	void endCase(Class<? extends TickStatus> statusClass, Object... constructorParams) {
		intEndCase(statusClass, constructorParams);
	}
	
	void endCase() {
		intEndCase(null);
//		Excavator.getInstance().nextStep();
	}
	
	private void intEndCase(Class<? extends TickStatus> statusClass, Object... constructorParams){
		state = BotStatus.INACTIVE;
		if(key!=null){
			KeyBinding.setKeyBindState(key.getKeyCode(), false);
		}else{
			KeyBinding.unPressAllKeys();
		}
		if(statusClass==null){
			TickStatus.status = null;
		}else{
			TickStatus ts = null;
			try {
				Class<?>[] parameterTypes = new Class<?>[constructorParams.length];
				for(int i=0;i<constructorParams.length;i++){
					parameterTypes[i] = constructorParams[i].getClass();
				}
				ts = statusClass.getConstructor(parameterTypes).newInstance(constructorParams);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				TickStatus.status = null;
				log.error(e);
				log.error(e.getStackTrace());
			}
			TickStatus.status = ts;
		}
	}
	
}
