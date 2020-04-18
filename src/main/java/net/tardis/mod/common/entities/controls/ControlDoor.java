package net.tardis.mod.common.entities.controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tardis.mod.client.worldshell.BlockStorage;
import net.tardis.mod.client.worldshell.IContainsWorldShell;
import net.tardis.mod.client.worldshell.WorldBoti;
import net.tardis.mod.client.worldshell.WorldShell;
import net.tardis.mod.common.IDoor;
import net.tardis.mod.common.blocks.BlockTardisTop;
import net.tardis.mod.common.entities.IShouldDie;
import net.tardis.mod.common.items.TItems;
import net.tardis.mod.common.serializers.TDataSerializers;
import net.tardis.mod.common.sounds.TSounds;
import net.tardis.mod.common.tileentity.TileEntityDoor;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.config.TardisConfig;
import net.tardis.mod.network.NetworkHandler;
import net.tardis.mod.network.packets.MessageRequestBOTI;
import net.tardis.mod.util.common.helpers.Helper;
import net.tardis.mod.util.common.helpers.TardisHelper;

public class ControlDoor extends Entity implements IContainsWorldShell, IDoor, IShouldDie {

	public static final DataParameter<Boolean> IS_OPEN = EntityDataManager.createKey(ControlDoor.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Float> FACING = EntityDataManager.createKey(ControlDoor.class, DataSerializers.FLOAT);
	public static final DataParameter<Boolean> UPDATE = EntityDataManager.createKey(ControlDoor.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Vec3d> MOTION = EntityDataManager.createKey(ControlDoor.class, TDataSerializers.VEC3D);
	public boolean updateServerSide = false;
	
	public int antiSpamTicks = 0;
	private WorldShell shell = new WorldShell(BlockPos.ORIGIN);
	private World clientWorld;
	
	public ControlDoor(World world) {
		super(world);
		this.setSize(1F, 2F);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			this.dataManager.set(IS_OPEN, !this.dataManager.get(IS_OPEN));
			boolean open = this.dataManager.get(IS_OPEN);
			world.playSound(null, this.getPosition(), open ? TSounds.door_open : TSounds.door_closed, SoundCategory.AMBIENT, 1F, 1F);
			this.setOtherDoors(open);
			this.setBotiUpdate(true);
		}
		else this.syncWorldShell();
		return true;
	}

	public void setOtherDoors(boolean open) {
		if(!world.isRemote) {
			TileEntityTardis tardis = this.getTardis();
			if(tardis == null) return;
			WorldServer ws = this.world.getMinecraftServer().getWorld(tardis.dimension);
			TileEntityDoor door = (TileEntityDoor)ws.getTileEntity(tardis.getLocation().up());
			if(door != null) {
				door.setLocked(!open);
			}
		}
	}
	
	public float getFacing() {
		if(!world.isRemote) {
			TileEntityTardis tardis = this.getTardis();
			if(tardis != null) {
				if(tardis.getTardisEntity() != null)
					return tardis.getTardisEntity().rotationYaw;
				WorldServer ws = world.getMinecraftServer().getWorld(tardis.dimension);
				IBlockState state = ws.getBlockState(tardis.getLocation().up());
				if(state.getBlock() instanceof BlockTardisTop) {
					return Helper.getAngleFromFacing(state.getValue(BlockTardisTop.FACING));
				}
			}
		}
		return this.dataManager.get(FACING);
	}
	
	public TileEntityTardis getTardis() {
		TileEntity te = world.getTileEntity(TardisHelper.getTardisForPosition(this.getPosition()));
		return te instanceof TileEntityTardis ? (TileEntityTardis)te : null;
	}
	
	AxisAlignedBB BOTI = Block.FULL_BLOCK_AABB.grow(20);
	
	public void updateWorldShell() {
		this.updateServerSide = false;
		if(!world.isRemote) {
			TileEntityTardis tardis = this.getTardis();
			if(tardis == null) return;
			
			WorldServer ws = world.getMinecraftServer().getWorld(tardis.dimension);
			if(ws == null)
				return;
			
			IBlockState state = ws.getBlockState(tardis.getLocation().up());
			if(!(state.getBlock() instanceof BlockTardisTop))
				return;
			
			BlockPos offset = tardis.getLocation().up().offset(state.getValue(BlockTardisTop.FACING), 20);
			AxisAlignedBB bb = BOTI;
			if(!this.shell.getOffset().equals(offset))
				this.shell = new WorldShell(offset);
			this.shell.blockMap = this.getBlockStoreInAABB(bb, offset, ws);
			this.shell.setEntities(this.getShellEntities(ws, bb.offset(offset)));
			this.dataManager.set(FACING, this.getFacing());
			this.shell.setTime(ws.getWorldTime());
		}
	}
	
	public List<NBTTagCompound> getShellEntities(WorldServer other, AxisAlignedBB bb){
		List<NBTTagCompound> entities = new ArrayList<NBTTagCompound>();
		for(Entity ent : other.getEntitiesWithinAABB(Entity.class, bb)) {
			if(EntityList.getKey(ent) != null) {
				NBTTagCompound tag = ent.serializeNBT();
				tag.setString("id", EntityList.getKey(ent).toString());
				entities.add(tag);
			}
		}
		return entities;
	}
	
	public Map<BlockPos, BlockStorage> getBlockStoreInAABB(AxisAlignedBB bb, BlockPos pos, WorldServer ws){
		HashMap<BlockPos, BlockStorage> map = new HashMap<BlockPos, BlockStorage>();
		for(int x = (int)bb.minX; x < bb.maxX; ++x) {
			for(int y = (int)bb.minY; y < bb.maxY; ++y) {
				for(int z = (int)bb.minZ; z < bb.maxZ; ++z) {
					BlockPos bp = new BlockPos(x, y, z).add(pos);
					IBlockState state = ws.getBlockState(bp);
					if(!(state.getBlock() instanceof BlockTardisTop) && state.getMaterial() != Material.AIR){
						int light = MathHelper.clamp(Helper.getLight(ws, bp) + (ws.isDaytime() ? Helper.getDaylight(ws, bp) : 0), 0, 15);
						map.put(bp, new BlockStorage(state, ws.getTileEntity(bp), light));
					}
				}
			}
		}
		return map;
	}
	
	@SideOnly(Side.CLIENT)
	public void setupClientWorld() {
		this.clientWorld = new WorldBoti(this.getDimension(),(WorldClient) world, this.shell);
	}
	
	public int getLightFromNeighborsFor(WorldServer server, EnumSkyBlock type, BlockPos pos)
	{
		if (!server.provider.hasSkyLight() && type == EnumSkyBlock.SKY)
		{
			return 0;
		}
		else
		{
			if (pos.getY() < 0)
			{
				pos = new BlockPos(pos.getX(), 0, pos.getZ());
			}
			
			if (!server.isValid(pos))
			{
				return type.defaultLightValue;
			}
			else if (!server.isBlockLoaded(pos))
			{
				return type.defaultLightValue;
			}
			else if (server.getBlockState(pos).useNeighborBrightness())
			{
				int i1 = server.getLightFor(type, pos.up());
				int i = server.getLightFor(type, pos.east());
				int j = server.getLightFor(type, pos.west());
				int k = server.getLightFor(type, pos.south());
				int l = server.getLightFor(type, pos.north());
				
				if (i > i1)
				{
					i1 = i;
				}
				
				if (j > i1)
				{
					i1 = j;
				}
				
				if (k > i1)
				{
					i1 = k;
				}
				
				if (l > i1)
				{
					i1 = l;
				}
				
				return i1;
			}
			else
			{
				Chunk chunk = server.getChunk(pos);
				return chunk.getLightFor(type, pos);
			}
		}
	}
	
	
	public void syncWorldShell() {
		NetworkHandler.NETWORK.sendToServer(new MessageRequestBOTI(this.getEntityId()));
	}
	
	public void handleEnter() {
		if(!world.isRemote && this.isOpen()) {
			TileEntityTardis tardis = this.getTardis();
			if(tardis == null) return;
			for(Entity entity : world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox())) {
				if(!entity.isSneaking())
					tardis.transferPlayer(entity, true);
			}
		}
	}
	
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		TileEntityTardis tardis = this.getTardis();
		
		if(tardis == null)
			return;
		
		if(this.isOpen() && (this.world.getTotalWorldTime() % 200 == 0) || this.getBotiUpdate())
			this.updateWorldShell();
		if(world.isRemote && this.isOpen()) {
			if(this.shell.getOffset().equals(BlockPos.ORIGIN))
				this.syncWorldShell();
			if(world.getTotalWorldTime() % TardisConfig.BOTI.botiTickRate == 0 || this.getBotiUpdate())
				this.syncWorldShell();
			this.shell.setTime(this.shell.getTime() + 1);
			if(this.clientWorld == null || ((WorldBoti)this.clientWorld).dimension != this.getDimension())
				this.setupClientWorld();
		}
		this.setBotiUpdate(false);
		this.handleEnter();
		
		//Water flowing shit
		if(!world.isRemote && world.getTotalWorldTime() % 20 == 0) {
			if(tardis != null) {
				WorldServer world = this.world.getMinecraftServer().getWorld(tardis.dimension);
				
				EnumFacing face = EnumFacing.fromAngle(this.getFacing() + 180);
				if(this.isOpen() && isWater(world, tardis.getLocation().offset(face))) {
					if(!isWater(this.world, this.getPosition()) && !tardis.isForceFieldEnabled())
						ControlDoor.updateFluid(this.world, this.getPosition(), Blocks.WATER.getDefaultState());
				}
				else if(isWater(this.world, this.getPosition()))
					ControlDoor.updateFluid(this.world, getPosition(), Blocks.AIR.getDefaultState());
				
				if(this.isOpen() && isWater(world, tardis.getLocation().offset(tardis.getFacing()).up())) {
					if(!isWater(this.world, this.getPosition().up()) && !tardis.isForceFieldEnabled())
						ControlDoor.updateFluid(this.world, this.getPosition().up(), Blocks.WATER.getDefaultState());
				}
				else if(isWater(this.world, this.getPosition().up())) {
					ControlDoor.updateFluid(this.world, this.getPosition().up(), Blocks.AIR.getDefaultState());
				}
			}
		}
		
		if(tardis.isInFlight() && this.isOpen()) {
			for(EntityLivingBase base : world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(30))) {
				Vec3d move = this.getPositionVector().subtract(base.getPositionVector()).normalize().scale(0.23);
				base.motionX += move.x;
				base.motionY += move.y;
				base.motionZ += move.z;
			}
		}
		
	}

	private static boolean isWater(World world, BlockPos pos) {
		return world.getBlockState(pos).getMaterial() == Material.WATER;
	}
	
	private static void updateFluid(World world, BlockPos pos, IBlockState state) {
		world.setBlockState(pos, state, 3);
		state.getBlock().neighborChanged(state, world, pos, state.getBlock(), pos);
	}
	
	@Override
	protected void entityInit() {
		this.dataManager.register(IS_OPEN, false);
		this.dataManager.register(FACING, 0F);
		this.dataManager.register(UPDATE, true);
		this.dataManager.register(MOTION, new Vec3d(0, 0, 0));
	}

	@Override
	public boolean isOpen() {
		return this.dataManager.get(IS_OPEN);
	}

	@Override
	public void setOpen(boolean open) {
		this.dataManager.set(IS_OPEN, open);
	}

	@Override
	public WorldShell getWorldShell() {
		return this.shell;
	}

	@Override
	public void setWorldShell(WorldShell worldShell) {
		this.shell = worldShell;
	}

	@Override
	public int getDimension() {
		TileEntityTardis tardis = getTardis();
		return tardis == null ? 0 : tardis.dimension;
	}

	@Override
	public World getRenderWorld() {
		return this.clientWorld;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.dataManager.set(IS_OPEN, compound.getBoolean("open"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setBoolean("open", this.dataManager.get(IS_OPEN));
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source != null && source.getTrueSource() instanceof EntityPlayer) {
			if(!world.isRemote)
				InventoryHelper.spawnItemStack(world, posX, posY, posZ, new ItemStack(TItems.interior_door));
			this.setDead();
		}
		return true;
	}
	
	public void setBotiUpdate(boolean update) {
		this.updateServerSide = update;
	}
	
	public boolean getBotiUpdate() {
		return this.updateServerSide;
	}

	public Vec3d getMotion() {
		return this.dataManager.get(MOTION);
	}
}

	