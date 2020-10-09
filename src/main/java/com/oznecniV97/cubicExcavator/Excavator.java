package com.oznecniV97.cubicExcavator;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oznecniV97.cubicExcavator.client.PlayerMovements;
import com.oznecniV97.cubicExcavator.enums.BotStatus;
import com.oznecniV97.cubicExcavator.enums.Direction;
import com.oznecniV97.cubicExcavator.enums.ToolsNeeded;
import com.oznecniV97.cubicExcavator.enums.WallPosition;
import com.oznecniV97.cubicExcavator.handler.tickStatus.PlacingStatus;
import com.oznecniV97.cubicExcavator.handler.tickStatus.TickStatus;
import com.oznecniV97.cubicExcavator.utilities.Utilities;
import com.oznecniV97.cubicExcavator.utilities.Utilities.CommandUtils;
import com.oznecniV97.cubicExcavator.utilities.Utilities.DigUtils;
import com.oznecniV97.cubicExcavator.utilities.Utilities.PlayerUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

//Classe orchestratore
public class Excavator {

	private enum ModStatus {
		STARTED, STOPPED
	}

	public static Logger log = LogManager.getLogger(Excavator.class);
	private static Minecraft mc = Minecraft.getMinecraft();
	// singleton instance
	private static Excavator instance;
	// CommandUtils per inviare messaggi tramite chat
	public CommandUtils com = null;
	// Stato esterno (avviato o no)
	private ModStatus exStat = ModStatus.STOPPED;
	// Stato interno (gestisce il nextStep dell'orchestratore)
	private BotStatus orcStat = BotStatus.INACTIVE;
	// contatore per capire quanti blocchi ho scavato dall'ultima torcia
	private int contTorcie = 0;

	private Excavator() {
		EntityPlayerSP pl = mc.player;
		ICommandSender sender = pl.getCommandSenderEntity();
		com = new CommandUtils(sender);
	}

	// get actual excavator instance (singleton)
	public static Excavator getInstance() {
		if (instance == null)
			instance = new Excavator();
		return instance;
	}

	// metodo invocato dalla pressione del tasto per avviare/fermare il bot
	public void startOrStop() {
		if (exStat.equals(ModStatus.STOPPED)) {
			start();
		} else {
			stop(true);
		}
	}

	private void start() {
		try {
			// set started status
			exStat = ModStatus.STARTED;
//			mc.setIngameNotInFocus();
			inizio();
		} catch (Exception e) {
			log.error("ERRORE: è schiattato start!");
			log.error(e);
		}
	}

	// come start, ma per ricominciare dopo aver rotto uno strato
	private void inizio() {
		if (exStat.equals(ModStatus.STARTED)) {
			// check strumenti
			boolean str = checkStrumenti();
			// se strumenti ok, parti con il bot
			if (!str) {
				com.printCommandMessage("Verificare che i seguenti strumenti siano nell'inventario rapido:");
				for (ToolsNeeded tool : ToolsNeeded.values()) {
					com.printCommandMessage(tool);
				}
				stop(false);
			} else {
				// check posizione
				posizionati();
			}
		}
	}

	// stop method
	private void stop(boolean message) {
		try {
			// set stopped status
			exStat = ModStatus.STOPPED;
			orcStat = BotStatus.INACTIVE;
			if (message)
				com.printCommandMessage("STOP");
		} catch (Exception e) {
			log.error("ERRORE: è schiattato stop!");
			log.error(e);
		}
	}

	private boolean checkStrumenti() {
		NonNullList<ItemStack> hotbar = Utilities.PlayerUtils.getPlayerHotbar();
		boolean ret = false;
		for (ToolsNeeded tool : ToolsNeeded.values()) {
			for (ItemStack elem : hotbar) {
				// per ogni strumento, controlla sia presente nella hotbar
				ret = tool.getItem().equals(elem.getItem());
				if (ret)
					break;
			}
			if (!ret)
				break;
		}
		return ret;
	}

	/*
	 * partendo dalla tua posizione attuale:
	 * 1. vai avanti finché non trovi un blocco di fronte a te
	 * 2. controlla nel laterale dove hai spazio:
	 *  1. hai spazio a destra --> OK
	 *  2. hai spazio a sinistra --> spostati a sinistra e OK
	 *  3. non hai spazio --> KO
	 */
	private void posizionati() {
		if (exStat.equals(ModStatus.STARTED)) {
			boolean ret = false;
			if(contTorcie>=9){
				this.orcStat = BotStatus.MOVING;
				piazzaTorcia();
				return;
			}else{
				PlayerMovements.resetTurn();
			}
			// --------------Evento temporizzato
			// punto 1
			// controllo se avanti ho uno spazio
			if (Utilities.PlayerUtils.canMove(Direction.FRONT)) {
				// se sì, inizio il processo di avanzamento e poi torno null per attendere
				this.orcStat = BotStatus.MOVING;
				PlayerMovements.goForwardUntilStop();
				return;
			} else {
				this.orcStat = BotStatus.INACTIVE;
			}
			// --------------Controllo finale
			// punto 2.1
			if (Utilities.PlayerUtils.canMove(Direction.RIGHT)) {
				ret = true;
				// punto 2.2
			} else if (Utilities.PlayerUtils.canMove(Direction.LEFT)) {
				this.orcStat = BotStatus.MOVING;
				PlayerMovements.goToLeftBlock();
				return;
			}
			// check posizione
			if (!ret) {
				com.printCommandMessage("Attenzione, necessario un muro piatto min 2x2 per iniziare.");
				stop(false);
			} else {
				scava();
				// com.printCommandMessage("OK");
			}
		}
	}

	private void piazzaTorcia() {
		//guarda verso il basso (per terra)
		PlayerMovements.turnDownFixed(85);
		//acquisisci il blocco dove dovrà andare la torcia (sotto i piedi del giocatore, quindi la posizione del giocatore)
		BlockPos placingBlockPos = PlayerUtils.getNotRoundedPosition();
		//piazza la torcia
		TickStatus.status = new PlacingStatus(ToolsNeeded.TORCH, placingBlockPos);
		contTorcie = 0;
	}

	/*
	 * 1. controlla che i cubi avanti a te non siano aria (ci sia qualcosa da scavare)
	 * 2. controlla se uno dei quattro blocchi della parete ha vicino della lava 
	 *  1. caso particolare lava in alto 
	 *   1. manda messaggio errore e ferma tutto 
	 *  2. caso no lava 
	 *   1. posizionati avanti a un blocco 
	 *   2. guarda il blocco 
	 *   3. fai scavare 
	 *  3. caso particolare lava in basso a destra e a sinistra
	 *   1. posizionati avanti a uno dei due blocchi che ha la lava vicino
	 *   2. guarda il blocco
	 *   3. fai scavare il blocco che stai guardando indicando la lava e che deve riposizionare un blocco
	 *    4. caso lava
	 *    1. posizionati avanti al blocco che ha la lava vicino 
	 *    2. guarda il blocco 
	 *    3. fai scavare il blocco che stai guardando INDICANDO LA LAVA
	 */
	private void scava() {
		if (exStat.equals(ModStatus.STARTED)) {
			// player position
			BlockPos plPos = PlayerUtils.getNotRoundedPosition();
			// front block position
			BlockPos frontBlPos = plPos.add(Direction.FRONT.getVector());
			// capisco se sono a destra o a sinistra
			boolean sonoASinistra = PlayerUtils.canMove(Direction.RIGHT);
			Vec3i hVec = sonoASinistra ? Direction.RIGHT.getVector() : Direction.LEFT.getVector();
			// lista dei blocchi della parete
			Map<BlockPos, WallPosition> wallMap = new HashMap<>();
			if (sonoASinistra) {
				wallMap.put(frontBlPos, WallPosition.LOWER_LEFT);
				wallMap.put(frontBlPos.add(Direction.UP.getVector()), WallPosition.UPPER_LEFT);
				wallMap.put(frontBlPos.add(hVec), WallPosition.LOWER_RIGHT);
				wallMap.put(frontBlPos.add(Direction.UP.getVector()).add(hVec), WallPosition.UPPER_RIGHT);
			} else {
				wallMap.put(frontBlPos, WallPosition.LOWER_RIGHT);
				wallMap.put(frontBlPos.add(Direction.UP.getVector()), WallPosition.UPPER_RIGHT);
				wallMap.put(frontBlPos.add(hVec), WallPosition.LOWER_LEFT);
				wallMap.put(frontBlPos.add(Direction.UP.getVector()).add(hVec), WallPosition.UPPER_LEFT);
			}
			// punto 1 (controlla fine muro)
			if (Utilities.DigUtils.wallFinished(wallMap)) {
				// il muro è finito, passa avanti
				contTorcie++;
				inizio();
			} else {
				// c'è ancora del muro, scaviamo!
				// punto 2 (controlla lava)
				boolean needWater = Utilities.DigUtils.needWater(wallMap);
				log.info("lava= " + needWater);
				// punto 2.1 (caso lava sopra)
				if (needWater && DigUtils.isLavaOnTop(wallMap)) {
					// punto 2.1.1 (ferma tutto)
					com.printCommandMessage("ATTENZIONE! Trovata lava sopra al muro. Impossibile continuare!");
					return;
				}
				// punto 2.2 (caso no lava), 2.3(caso particolare lava sotto), 2.4 (caso lava)
				WallPosition wPos = Utilities.DigUtils.getWallBlock(wallMap, needWater);
				// punto 2.x.1 (posizionati)
				if (!PlayerUtils.isInPosition(wPos)) {
					this.orcStat = BotStatus.DIGGING;
					if (wPos.isLeft())
						PlayerMovements.goToLeftBlock();
					else
						PlayerMovements.goToRightBlock();
					return;
				}
				// punto 2.x.2 (guarda il blocco)
				PlayerMovements.lookWallPos(wPos, needWater);
				// punto 2.x.3 (scava)
				this.orcStat = BotStatus.DIGGING;
				PlayerMovements.breakLookingBlock(wPos, needWater);
			}
		}
	}

	public synchronized void nextStep() {
		if (exStat.equals(ModStatus.STARTED)) {
			switch (this.orcStat) {
			case MOVING:
				posizionati();
				break;
			case DIGGING:
				scava();
				break;
			case INACTIVE:
				return;
			default:
				log.warn("Switch nextStep - entrato nel case default.");
				break;
			}
		}
	}

}
