package net.tardis.mod.client.renderers.controls;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.tardis.mod.client.models.console.contols.ModelRandom;
import net.tardis.mod.common.entities.controls.EntityControl;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.util.common.helpers.Helper;

public class RenderRandom extends RenderControl {

	ModelRandom model = new ModelRandom();

	public RenderRandom() {
	}

	@Override
	public void renderControl(EntityControl entity, double x, double y, double z, float entityYaw, float partialTicks, TileEntityTardis tType) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.0625F, z);
		mc.getTextureManager().bindTexture(CONTROL_TEXTURE);
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.rotate(-58, 1, 0, 0);
		Vec3d offset = Helper.convertToPixels(-1.5, -1.5, 0);
		int ticks = entity.ticks;
		float rot = 0F;
		if (Helper.isIntInRange(0, 6, ticks)) rot = 0;
		if (Helper.isIntInRange(5, 11, ticks)) rot = -90;
		if (Helper.isIntInRange(10, 16, ticks)) rot = -180;
		if (Helper.isIntInRange(15, 20, ticks)) rot = -270;
		GlStateManager.rotate(rot, 0, 0, 1);
		GlStateManager.translate(offset.x, offset.y, offset.z);
		model.render(null, 0, 0, 0, 0, 0, 0.0625F);
		GlStateManager.popMatrix();
	}

}
