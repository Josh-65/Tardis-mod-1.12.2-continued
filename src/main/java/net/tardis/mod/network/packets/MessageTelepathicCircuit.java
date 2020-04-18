package net.tardis.mod.network.packets;

import java.util.ArrayList;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tardis.mod.common.dimensions.TDimensions;
import net.tardis.mod.common.items.TItems;
import net.tardis.mod.common.sounds.TSounds;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.util.common.helpers.Helper;
import net.tardis.mod.util.common.helpers.PlayerHelper;

public class MessageTelepathicCircuit implements IMessage {
	
	public BlockPos pos = BlockPos.ORIGIN;
	public String name = "";
	
	public MessageTelepathicCircuit() {
	
	}
	
	public MessageTelepathicCircuit(BlockPos tardisPos, String name) {
		pos = tardisPos.toImmutable();
		if (name != null) {
			this.name = name;
		}
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		pos = BlockPos.fromLong(buf.readLong());
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeLong(pos.toLong());
	}
	
	public static class Handler implements IMessageHandler<MessageTelepathicCircuit, IMessage> {
		
		@Override
		public IMessage onMessage(MessageTelepathicCircuit message, MessageContext ctx) {
			ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
				
				boolean managed = false;
				
				MinecraftServer server = ctx.getServerHandler().player.getServer();
				WorldServer ws = DimensionManager.getWorld(TDimensions.TARDIS_ID);
				EntityPlayer player = server.getPlayerList().getPlayerByUsername(message.name.trim().toLowerCase());
				TileEntity te = ws.getTileEntity(message.pos);
				
				//==================== LANDING ON PEOPLE ====================
				if (te != null && te instanceof TileEntityTardis) {
					TileEntityTardis tardis = (TileEntityTardis) ws.getTileEntity(message.pos);
					WorldServer locationWorld = DimensionManager.getWorld(tardis.getTargetDim());
					if (player != null && !player.inventory.hasItemStack(new ItemStack(TItems.biodampener))) {
						tardis.setDesination(player.getPosition(), player.dimension);
						tardis.startFlight();
						managed = true;
					} else {
						
						//==================== BIOMES ====================
						Biome b = Helper.findBiomeByName(message.name.toLowerCase().trim());
						if (b != null) {
							ArrayList<Biome> biomes = new ArrayList<>();
							biomes.add(b);
							BlockPos biomePos = locationWorld.getBiomeProvider().findBiomePosition(tardis.getLocation().getX(), tardis.getLocation().getZ(), 1000, biomes, new Random());
							if (biomePos != null && !biomePos.equals(BlockPos.ORIGIN)) {
								biomePos.add(0, locationWorld.getSeaLevel(), 0);
								tardis.setDesination(biomePos, tardis.getTargetDim());
								tardis.startFlight();
								managed = true;
							}
							else {
								PlayerHelper.sendMessage(ctx.getServerHandler().player, new TextComponentString("Biome not found in range!"), true);
								managed = true;
							}
						} else {
							//==================== STRUCTURES ====================
							Random rand = ctx.getServerHandler().player.world.rand;
							BlockPos structurePos = locationWorld.findNearestStructure(message.name.trim(), tardis.getLocation(), true);
							if (structurePos != null && !BlockPos.ORIGIN.equals(structurePos)) {
								tardis.setDesination(structurePos.add(rand.nextInt(6) - 3, 0, rand.nextInt(6) - 3), tardis.getTargetDim());
								tardis.startFlight();
								managed = true;
							}
						}
					}
					
					//==================== FAILURE ====================
					if (!managed) {
						PlayerHelper.sendMessage(ctx.getServerHandler().player, new TextComponentTranslation("tardis.message.tc_notfound", TextFormatting.GOLD + TextFormatting.BOLD.toString() + message.name.trim(), TextFormatting.GOLD + TextFormatting.BOLD.toString() + Helper.formatDimensionName(DimensionManager.createProviderFor(tardis.dimension).getDimensionType().getName())), true);
						tardis.getWorld().playSound(null, tardis.getPos(), TSounds.engine_stutter, SoundCategory.BLOCKS, 1, 1);
					}
					
				}
				
			});
			return null;
		}
		
	}
}
