package net.tardis.mod.common.entities.controls;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.tardis.mod.common.IDoor;
import net.tardis.mod.common.tileentity.TileEntityDoor;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.common.tileentity.consoles.*;
import net.tardis.mod.util.common.helpers.Helper;

public class ControlDoorSwitch extends EntityControl {

	private AxisAlignedBB DOOR_BB = new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(8 * 16);

	public ControlDoorSwitch(TileEntityTardis tardis) {
		super(tardis);
	}

	public ControlDoorSwitch(World world) {
		super(world);
		this.setSize(0.0625F, 0.0625F);
	}

	@Override
	public Vec3d getOffset(TileEntityTardis tardis) {
		if (tardis instanceof TileEntityTardis01 || tardis.getClass() == TileEntityTardis02.class) {
			return Helper.convertToPixels(-13.5, -3, -4.5);
		}
		if (tardis instanceof TileEntityTardis03)
			return Helper.convertToPixels(-4, 2, -16);
		if(tardis instanceof TileEntityTardis04)
			return Helper.convertToPixels(12, -2, -6);
		if(tardis instanceof TileEntityTardis05)
			return Helper.convertToPixels(14, -3, 3.5);
		return Helper.convertToPixels(0, -2, 11);
	}

	@Override
	public void preformAction(EntityPlayer player) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(getConsolePos());
			if (te != null && te instanceof TileEntityTardis) {
				TileEntityTardis tardis = (TileEntityTardis) te;
				if (!tardis.isInFlight()) {
					for (Entity entity : world.getEntitiesWithinAABB(Entity.class, DOOR_BB.offset(getPositionVector()))) {
						if (entity instanceof IDoor) {
							((IDoor) entity).setOpen(!((IDoor) entity).isOpen());
						}
						if (entity instanceof ControlDoor) {
							if (!world.isRemote) {
								if (tardis == null) return;
								WorldServer ws = world.getMinecraftServer().getWorld(tardis.dimension);
								if (ws == null) return;
								TileEntity door = ws.getTileEntity(tardis.getLocation().up());
								if (door instanceof TileEntityDoor) {
									((TileEntityDoor) door).setLocked(!((IDoor) entity).isOpen());
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String getControlName() {
		return this.getDisplayName().getFormattedText();
	}

	@Override
	public void init(TileEntityTardis tardis) {
		if (tardis != null) {
			if (tardis instanceof TileEntityTardis03)
				this.setSize(Helper.precentToPixels(2F), Helper.precentToPixels(2F));
			if(tardis instanceof TileEntityTardis04)
				this.setSize(Helper.precentToPixels(2), Helper.precentToPixels(2));
		}
	}

}
