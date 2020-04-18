package net.tardis.mod.common.entities.controls;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tardis.mod.common.sounds.TSounds;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.common.tileentity.consoles.*;
import net.tardis.mod.util.common.helpers.Helper;

public class ControlFastReturn extends EntityControl {

	public ControlFastReturn(TileEntityTardis tardis) {
		super(tardis);
	}

	public ControlFastReturn(World world) {
		super(world);
		this.setSize(0.0625F, 0.0625F);
	}

	@Override
	public Vec3d getOffset(TileEntityTardis tardis) {
		if (tardis.getClass() == TileEntityTardis01.class || tardis.getClass() == TileEntityTardis02.class) {
			return Helper.convertToPixels(10, -2.5, 9.5);
		}
		if (tardis instanceof TileEntityTardis03)
			return Helper.convertToPixels(-6, 1, -15.5);
		if(tardis instanceof TileEntityTardis04)
			return Helper.convertToPixels(-7.5, -1, -8.25);
		if (tardis instanceof TileEntityTardis05) {
			return Helper.convertToPixels(8, 0, -4.5);
		}
		return Helper.convertToPixels(-2.5, 0, 7.3);
	}

	@Override
	public void preformAction(EntityPlayer player) {
		TileEntityTardis tardis = (TileEntityTardis) world.getTileEntity(getConsolePos());
		if (!world.isRemote) {
			tardis.setSpaceTimeCoordnate(tardis.returnLocation);
		}
	}

	@Override
	public void init(TileEntityTardis tardis) {
		if (tardis != null) {
			if (tardis instanceof TileEntityTardis03)
				this.setSize(Helper.precentToPixels(1F), Helper.precentToPixels(1.5F));
		}
	}

	@Override
	public SoundEvent getUseSound() {
		return TSounds.control_fast_return;
	}
}
