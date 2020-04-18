package net.tardis.mod.common.tileentity.decoration;

import net.minecraft.util.math.AxisAlignedBB;
import net.tardis.mod.common.tileentity.TileEntityMultiblockMaster;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.util.common.helpers.TardisHelper;

public class TileEntityToyotaSpin extends TileEntityMultiblockMaster {

	public static final AxisAlignedBB renderBox = new AxisAlignedBB(0, 0, 0, 3, 4, 3).offset(-1, 0, -1);

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return renderBox.offset(getPos());
	}

	public boolean isInFlight() {
		TileEntityTardis tardis = (TileEntityTardis) world.getTileEntity(TardisHelper.getTardisForPosition(this.getPos()));
		return tardis != null && tardis.isInFlight();
	}

}
