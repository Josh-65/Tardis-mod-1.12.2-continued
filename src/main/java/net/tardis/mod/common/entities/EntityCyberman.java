package net.tardis.mod.common.entities;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tardis.mod.api.entities.IDontSufficate;
import net.tardis.mod.common.dimensions.TDimensions;
import net.tardis.mod.common.items.TItems;
import net.tardis.mod.common.tileentity.TileEntityDoor;

public class EntityCyberman extends EntityMob implements IDontSufficate {


	public EntityCyberman(World worldIn) {
		super(worldIn);
		this.tasks.addTask(6, new EntityAIMoveToTARDIS(this, 1.0D));
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		ItemStack[] drops = new ItemStack[]{new ItemStack(TItems.circuts, 1 + lootingModifier)};
		for (ItemStack s : drops) {
			if (!world.isRemote) {
				InventoryHelper.spawnItemStack(world, posX, posY, posZ, s);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source != null && source.getTrueSource() instanceof EntityPlayer && !source.isProjectile()){
			EntityPlayer player = ((EntityPlayer) source.getTrueSource());
			if(player.getHeldItemMainhand().getItem() == Items.GOLDEN_SWORD) {
				amount *= 2;
			}
		}
		return super.attackEntityFrom(source, amount);
	}

	public static class EntityAIMoveToTARDIS extends EntityAIBase {

		double speed;
		EntityMob entity;
		Vec3d pos;
		BlockPos doorPos = BlockPos.ORIGIN;

		public EntityAIMoveToTARDIS(EntityMob base, double speed) {
			this.entity = base;
			this.speed = speed;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.getAttackTarget() == null;
		}

		@Override
		public boolean shouldContinueExecuting() {
			if (entity.dimension == TDimensions.TARDIS_ID) {
				return false;
			}
			for (TileEntity te : entity.world.loadedTileEntityList) {
				if (te instanceof TileEntityDoor) {
					Vec3d tePos = new Vec3d(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
					double dist = entity.getPositionVector().distanceTo(tePos);
					if (dist < 30) {
						this.pos = tePos;
						this.doorPos = te.getPos();
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean isInterruptible() {
			return true;
		}

		@Override
		public void startExecuting() {
			super.startExecuting();
		}

		@Override
		public void updateTask() {
			if (pos != null) {
				if (entity.getPositionVector().distanceTo(pos) > 1.5) {
					this.entity.getMoveHelper().setMoveTo(pos.x, pos.y, pos.z, speed);
				} else if (doorPos != null && entity.ticksExisted % 20 == 0) {
					TileEntityDoor door = ((TileEntityDoor) entity.world.getTileEntity(doorPos));
					if (door != null) door.knock();
				}
			}
		}

	}
}
