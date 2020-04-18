package net.tardis.mod.common.screwdriver;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tardis.mod.common.dimensions.TDimensions;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.util.common.helpers.Helper;
import net.tardis.mod.util.common.helpers.TardisHelper;

public class InteractionSignal implements IScrew {

	@Override
	public EnumActionResult performAction(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			if (TardisHelper.hasTardis(player.getGameProfile().getId())) {
				TileEntityTardis tardis = Helper.getTardis(world.getMinecraftServer().getWorld(TDimensions.TARDIS_ID).getTileEntity(TardisHelper.getTardis(player.getGameProfile().getId())));
				tardis.setDesination(player.getPosition(), player.dimension);
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public EnumActionResult blockInteraction(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return EnumActionResult.PASS;
	}

	@Override
	public boolean entityInteraction(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		return false;
	}

	@Override
	public String getName() {
		return "screw.tardis.signal";
	}

	@Override
	public int getCoolDownAmount() {
		return 20;
	}

	@Override
	public boolean causesCoolDown() {
		return true;
	}

	@Override
	public int energyRequired() {
		return 5;
	}
	
	@Override
	public String getInfo() {
		return "screwdriver.info.signal";
	}
	
}
