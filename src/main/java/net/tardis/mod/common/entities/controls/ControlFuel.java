package net.tardis.mod.common.entities.controls;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tardis.mod.common.sounds.TSounds;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.common.tileentity.consoles.*;
import net.tardis.mod.util.common.helpers.Helper;
import net.tardis.mod.util.common.helpers.PlayerHelper;

public class ControlFuel extends EntityControl {

	public static SoundEvent controlSound;

	public ControlFuel(TileEntityTardis tardis) {
		super(tardis);
	}

	public ControlFuel(World world) {
		super(world);
		this.setSize(0.125F, 0.125F);
	}

	@Override
	public Vec3d getOffset(TileEntityTardis tardis) {
		if (tardis.getClass() == TileEntityTardis01.class || tardis.getClass() == TileEntityTardis02.class) {
			return Helper.convertToPixels(13.5, -3, -2);
		}
		if (tardis instanceof TileEntityTardis03)
			return Helper.convertToPixels(0, 1, -13.75);
		if(tardis instanceof TileEntityTardis04)
			return Helper.convertToPixels(10.5, -3, 7.5);
		if(tardis instanceof TileEntityTardis05)
			return Helper.convertToPixels(8.5, -2, 10.5);
		return Helper.convertToPixels(-12, -4, 10);
	}

	@Override
	public void preformAction(EntityPlayer player) {
		if (!world.isRemote) {
			TileEntityTardis t = (TileEntityTardis) world.getTileEntity(getConsolePos());
			t.setFueling(!t.isFueling());
			PlayerHelper.sendMessage(player, t.isFueling() ? "tardis.message.refuelling" : "tardis.message.no_longer_refuelling", true);
		}

	}

	@Override
	public void init(TileEntityTardis tardis) {
		if (tardis != null) {
			if (tardis instanceof TileEntityTardis03)
				this.setSize(Helper.precentToPixels(1F), Helper.precentToPixels(1F));
			if (tardis instanceof TileEntityTardis05)
				this.setSize(Helper.precentToPixels(1F), Helper.precentToPixels(1F));
		}
	}

	@Override
	public SoundEvent getUseSound() {
		TileEntityTardis tardis = (TileEntityTardis) world.getTileEntity(this.getConsolePos());
		return tardis.isFueling() ? TSounds.control_refuel_start : TSounds.control_refuel_stop;
	}


}
