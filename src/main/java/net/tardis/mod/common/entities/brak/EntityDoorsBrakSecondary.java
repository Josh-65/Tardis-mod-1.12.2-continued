package net.tardis.mod.common.entities.brak;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.tardis.mod.common.entities.IShouldDie;
import net.tardis.mod.common.items.TItems;

public class EntityDoorsBrakSecondary extends Entity implements IShouldDie{

	public static final DataParameter<Boolean> OPEN = EntityDataManager.createKey(EntityDoorsBrakSecondary.class, DataSerializers.BOOLEAN);
	public double health = 50D;
	public int hurtTime = 0;
	
	public EntityDoorsBrakSecondary(World worldIn) {
		super(worldIn);
		this.setSize(2, 4);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(OPEN, false);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.dataManager.set(OPEN, compound.getBoolean("open"));
		this.health = compound.getDouble("health");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setBoolean("open", this.dataManager.get(OPEN));
		compound.setDouble("health", health);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.isOpen() ? null : this.getEntityBoundingBox();
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		this.dataManager.set(OPEN, !this.dataManager.get(OPEN));
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return this.getEntityBoundingBox().offset(this.getPositionVector());
	}

	@Override
	public void onKillCommand() {
		return;
	}

	public boolean isOpen() {
		return this.dataManager.get(OPEN);
	}
	
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if(health <= 0.0) {
			this.setDead();
			if(!world.isRemote)
				InventoryHelper.spawnItemStack(this.world, posX, posY, posZ, new ItemStack(TItems.doors_brak));
		}
		if(hurtTime > 0)
			--hurtTime;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		this.health -= amount;
		this.hurtTime = 10;
		if(source.getTrueSource() instanceof EntityPlayer) {
			if(((EntityPlayer)source.getTrueSource()).capabilities.isCreativeMode)
				this.health = 0;
		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

}
