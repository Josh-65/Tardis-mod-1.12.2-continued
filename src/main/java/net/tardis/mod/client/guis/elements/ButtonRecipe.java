package net.tardis.mod.client.guis.elements;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.tardis.mod.Tardis;
import net.tardis.mod.client.guis.elements.ButtonText.IAction;

public class ButtonRecipe extends GuiButton {

	public static final ResourceLocation TEXTURE = new ResourceLocation(Tardis.MODID, "textures/gui/item_button.png");
	ItemStack stackToRender = ItemStack.EMPTY;
	TransformType type = TransformType.GUI;
	IAction action;

	public ButtonRecipe(int buttonId, int x, int y, ItemStack stack) {
		super(buttonId, x, y, 18, 18, "Item");
		this.stackToRender = stack.copy();
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemIntoGUI(stackToRender, this.x + 1, this.y + 1);
		RenderHelper.disableStandardItemLighting();
	}

	public IBlockState getBlock() {
		if (this.stackToRender.getItem() instanceof ItemBlock) {
			return ((ItemBlock) this.stackToRender.getItem()).getBlock().getDefaultState();
		}
		return Blocks.AIR.getDefaultState();
	}
	
	public ItemStack getStack() {
		return this.stackToRender;
	}
	
	public void addAction(IAction action) {
		this.action = action;
	}
	
	public void doAction() {
		if(this.action != null) {
			this.action.call();
		}
	}
}
