package net.tardis.mod.client.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.tardis.mod.Tardis;
import net.tardis.mod.common.items.TItems;
import net.tardis.mod.util.common.helpers.Helper;

import java.awt.*;

public class OverlaySonicShades implements IOverlay {

	private Minecraft mc = Minecraft.getMinecraft();
	private ResourceLocation background = new ResourceLocation(Tardis.MODID, "textures/overlay/shades.png");
	private ModelPlayer biped = new ModelPlayer(1.0F, false);

	public static void drawEntityOnScreen(int posX, int posY, int scale, Entity ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) posX, (float) posY, 50.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	@Override
	public void renderUpdate() {

	}

	@Override
	public void pre(RenderGameOverlayEvent.Pre e, float partialTicks, ScaledResolution resolution) {
		if (Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != TItems.sonic_shades)
			return;
		//e.setCanceled(e.getType() == RenderGameOverlayEvent.ElementType.FOOD || e.getType() == RenderGameOverlayEvent.ElementType.HEALTH);
	}

	@Override
	public void post(RenderGameOverlayEvent.Post e, float partialTicks, ScaledResolution res) {

		if (mc.currentScreen != null) return;
		if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == TItems.sonic_shades) {
			if (Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != TItems.sonic_shades)
				return;

			//Head
			GlStateManager.pushMatrix();
			GlStateManager.translate(res.getScaledWidth() / 2 - 175, 30, 50.0F);
			GlStateManager.scale(25, 25, 25);
			mc.getTextureManager().bindTexture(mc.player.getLocationSkin());
			GlStateManager.enableColorMaterial();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.color(1, 1, 1, 1);
			biped.bipedHeadwear.rotateAngleX = biped.bipedHead.rotateAngleX = 0.7f;
			biped.bipedHeadwear.rotateAngleY = biped.bipedHead.rotateAngleY = (float) (-Math.PI / 4);
			biped.bipedHeadwear.rotateAngleZ = biped.bipedHead.rotateAngleZ = -0.5f;
			biped.bipedHead.render(0.064f);
			biped.bipedHeadwear.render(0.0625F);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.disableTexture2D();
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

			//Entity and info
			if (Minecraft.getMinecraft().objectMouseOver != null) {
				Entity mouseOver = Minecraft.getMinecraft().objectMouseOver.entityHit;
				if (mc.player != null && mouseOver != null) {
					GlStateManager.pushMatrix();
					mc.fontRenderer.drawStringWithShadow("Name: " + mouseOver.getName(), res.getScaledWidth() - 160, res.getScaledHeight() - 70, Color.GREEN.getRGB());
					mc.fontRenderer.drawStringWithShadow("Distance: " + Math.round(mouseOver.getDistance(mc.player)) + " blocks", res.getScaledWidth() - 160, res.getScaledHeight() - 60, Color.GREEN.getRGB());
					mc.fontRenderer.drawStringWithShadow("Age: " + mouseOver.ticksExisted / 20 + " seconds", res.getScaledWidth() - 160, res.getScaledHeight() - 50, Color.GREEN.getRGB());
					mc.fontRenderer.drawStringWithShadow("Position: " + Helper.formatBlockPos(mouseOver.getPosition()), res.getScaledWidth() - 160, res.getScaledHeight() - 40, Color.GREEN.getRGB());
					GlStateManager.popMatrix();
				}
			}

			//Compass
			EnumFacing face = mc.player.getHorizontalFacing();
			String direction = face == EnumFacing.NORTH ? "N" : (face == EnumFacing.EAST ? "E" : (face == EnumFacing.WEST ? "W" : "S"));
			mc.fontRenderer.drawStringWithShadow(direction, res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(direction) / 2, mc.fontRenderer.FONT_HEIGHT / 2, Color.GREEN.getRGB());
			GlStateManager.popMatrix();


			//Rift?
			NBTTagCompound tag = Helper.getStackTag(mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
			if (tag.hasKey("rift") && tag.getBoolean("rift")) {
				String riftString = "Rift Detected!";
				mc.fontRenderer.drawStringWithShadow(riftString, res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(riftString) / 2, res.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT * 5, Color.GREEN.getRGB());
			}
		}
	}
}