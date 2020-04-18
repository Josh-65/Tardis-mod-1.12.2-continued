package net.tardis.mod.common.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.tardis.mod.common.TDamage;
import net.tardis.mod.common.entities.EntityLaserRay;
import net.tardis.mod.common.sounds.TSounds;

import java.util.List;

public class ItemRayGun extends ItemBase {
	
	public static final String AMMO_KEY = "ammo";
	
	public ItemRayGun() {
		this.setMaxStackSize(1);
	}
	
	public static void setAmmo(ItemStack stack, int ammo) {
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger(AMMO_KEY, ammo);
	}
	
	public static int getAmmo(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(AMMO_KEY))
			return stack.getTagCompound().getInteger(AMMO_KEY);
		return 0;
	}
	
	public static ItemStack getAmmoInInventory(NonNullList<ItemStack> stacks, Item item) {
		for (ItemStack stack : stacks) {
			if (stack.getItem() == item) return stack;
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack gun = playerIn.getHeldItem(handIn);
		if (playerIn.isSneaking()) {
			ItemStack ammo = getAmmoInInventory(playerIn.inventory.mainInventory, TItems.power_cell);
			if (!ammo.isEmpty()) {
				setAmmo(gun, getAmmo(gun) + (ammo.getCount() * 5));
				ammo.shrink(ammo.getCount());
			}
		} else {
			playerIn.setActiveHand(handIn);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, gun);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase livingBase, int timeLeft) {
		if (livingBase instanceof EntityPlayer) {
			EntityPlayer playerIn = (EntityPlayer) livingBase;
			if (getAmmo(stack) > 0) {
				if (!worldIn.isRemote) {
					EntityLaserRay entity = new EntityLaserRay(worldIn, playerIn, 5, TDamage.LASER, new Vec3d(0, 1, 0));
					entity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
					worldIn.spawnEntity(entity);
					playerIn.getCooldownTracker().setCooldown(this, 10); //Feel free to remove, just a suggestion
					//Fuck you, I like it
					ItemRayGun.setAmmo(stack, ItemRayGun.getAmmo(stack) - 1);
				}
				worldIn.playSound(null, playerIn.getPosition(), TSounds.dalek_ray, SoundCategory.HOSTILE, 1F, 1F);
			}
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (getAmmo(stack) > 0) {
			tooltip.add(new TextComponentTranslation("raygun.ammo", getAmmo(stack)).getUnformattedText());
		}
		else {
			tooltip.add(new TextComponentTranslation("raygun.ammo.none").getFormattedText());
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
}
