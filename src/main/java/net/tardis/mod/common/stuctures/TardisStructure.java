package net.tardis.mod.common.stuctures;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public abstract class TardisStructure {

	public ResourceLocation key;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TardisStructure)) return false;
		return ((TardisStructure) obj).key.equals(this.key);

	}

	public abstract BlockPos getStructureSize();
}
