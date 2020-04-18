package net.tardis.mod.common.systems;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tardis.mod.common.dimensions.TDimensions;
import net.tardis.mod.common.items.TItems;
import net.tardis.mod.common.systems.TardisSystems.BaseSystem;
import net.tardis.mod.common.tileentity.TileEntityTardis;

public class SystemThermo extends BaseSystem {

	boolean crashed = false;
	boolean hasRun = false;
	boolean repaired = false;

	public SystemThermo() {}

	@Override
	public void onUpdate(World world, BlockPos consolePos) {
		if (this.crashed && !this.hasRun) {
			TileEntityTardis tardis = (TileEntityTardis) world.getTileEntity(consolePos);
			this.hasRun = true;
			if (tardis != null) {
				tardis.setAbsoluteDesination(consolePos.north(5), TDimensions.TARDIS_ID);
				tardis.startFlight();
				tardis.travel();
				return;
			}
		}
		if (crashed && this.getHealth() > 0) {
			this.crashed = false;
			this.hasRun = false;
			TileEntityTardis tardis = (TileEntityTardis) world.getTileEntity(consolePos);
			tardis.setDesination(tardis.returnLocation.getPos(), tardis.returnLocation.getDimension());
			tardis.startFlight();
			repaired = false;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.crashed = tag.getBoolean("crash");
		this.repaired = tag.getBoolean("repaired");
	}

	@Override
	public NBTTagCompound writetoNBT(NBTTagCompound tag) {
		tag.setBoolean("crash", crashed);
		tag.setBoolean("repaired", repaired);
		return super.writetoNBT(tag);
	}

	@Override
	public void damage() {
		this.setHealth(this.getHealth() - 0.2F);
		if(this.getHealth() <= 0)
			this.crashed = true;
	}

	@Override
	public Item getRepairItem() {
		return TItems.thermo;
	}

	@Override
	public String getNameKey() {
		return "system.tardis.thermo";
	}

	@Override
	public String getUsage() {
		return "Without this system, your TARDIS will not land correctly, resulting in your TARDIS landing inside the control room";
	}

	@Override
	public void wear() {
		this.setHealth(this.getHealth() - 0.001F);
	}

}
