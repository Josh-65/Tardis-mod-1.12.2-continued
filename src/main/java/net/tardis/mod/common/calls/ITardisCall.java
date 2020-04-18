package net.tardis.mod.common.calls;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Spectre
 */
public interface ITardisCall {

	/**
	 * Perform a function when a player answers a call
	 *
	 * @param world      World in which player anwsers a call (Usually the TARDIS Dimension)
	 * @param player     The Player that answers the call
	 * @param consolePos Position of the console this call was sent to
	 */
	void onAnswered(World world, EntityPlayer player, BlockPos consolePos);

	/**
	 * If it doesn't use the phone, it defaults to the scanner.
	 *
	 * @return
	 */
	boolean usePhone();

}
