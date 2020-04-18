package net.tardis.mod.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tardis.mod.common.tileentity.TileEntityDoor;

public class MessageDemat implements IMessage {

	public boolean isDemat;
	public BlockPos pos;

	public MessageDemat() {
	}

	public MessageDemat(BlockPos pos1, boolean b) {
		this.pos = pos1;
		this.isDemat = b;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
		this.isDemat = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeBoolean(isDemat);
	}

	public static class Handler implements IMessageHandler<MessageDemat, IMessage> {

		public Handler() {
		}

		@Override
		public IMessage onMessage(MessageDemat mes, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TileEntity te = Minecraft.getMinecraft().world.getTileEntity(mes.pos);
				if (te != null && te instanceof TileEntityDoor) {
					if (mes.isDemat) {
						((TileEntityDoor) te).setDemat();
					} else {
						((TileEntityDoor) te).setRemat();
					}
				}
			});
			return null;
		}
	}
}
