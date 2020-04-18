package net.tardis.mod.common.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tardis.mod.client.EnumClothes;

public class ItemClothing extends ItemArmor {

	private EnumClothes clothing;

	public ItemClothing(EnumClothes enumClothes) {
		super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST);

		this.clothing = enumClothes;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return clothing.getTexture().toString();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
		if (clothing.getModel() != null) {
			return clothing.getModel();
		}
		return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
	}

}
