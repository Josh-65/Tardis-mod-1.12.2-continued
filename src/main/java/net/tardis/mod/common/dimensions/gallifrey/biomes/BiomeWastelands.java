package net.tardis.mod.common.dimensions.gallifrey.biomes;

import java.util.Random;

import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.tardis.mod.Tardis;
import net.tardis.mod.common.blocks.TBlocks;


public class BiomeWastelands extends Biome {

	protected static final IBlockState GRASS = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);
	protected static final IBlockState SANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();
	protected static final IBlockState DIRT = TBlocks.gallifreyan_dirt.getDefaultState();

	protected static final ResourceLocation[] BARNS = {
			new ResourceLocation(Tardis.MODID, "gallifrey/barn_one")
	};


	public BiomeWastelands() {
		super(new BiomeProperties("Wastelands").setBaseHeight(0.0F).setHeightVariation(0.0F).setTemperature(6.0F).setRainDisabled().setWaterColor(0xF78F00));
		this.spawnableCaveCreatureList.clear();
		this.spawnableCreatureList.clear();

		this.spawnableMonsterList.clear();
		this.spawnableWaterCreatureList.clear();

		this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityRabbit.class, 4, 2, 3));

		this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySkeleton.class, 25, 1, 4));


	}

	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		return getModdedBiomeGrassColor(0xAFA469);
	}

	@Override
	public int getFoliageColorAtPos(BlockPos pos) {
		return getModdedBiomeFoliageColor(0xEAEDED);
	}


	@Override
	public void genTerrainBlocks(World world, Random rand, ChunkPrimer primer, int x, int z, double stoneNoiseVal) {
		IBlockState topBlock = TBlocks.gallifreyan_sand.getDefaultState();
		IBlockState fillerBlock = TBlocks.gallifreyan_dirt.getDefaultState();
		IBlockState seaFloorBlock = TBlocks.gallifreyan_stone.getDefaultState();

		boolean hitFloorYet = false;
		int topBlocksToFill = 0;
		int dirtBlocksToFill = 0;
		int seaFloorBlocksToFill = 0;
		int dirtDepth = Math.max(0, (int) (stoneNoiseVal / 3.0D + 3 + rand.nextDouble() * 0.25D));
		int seaFloorDepth = 1 + rand.nextInt(2);

		int localX = x & 15;
		int localZ = z & 15;

		// start at the top and move downwards
		for (int y = 255; y >= 0; --y) {

			IBlockState state = primer.getBlockState(localZ, y, localX);

			// bedrock at the bottom
			if (y <= rand.nextInt(5)) {
				primer.setBlockState(localZ, y, localX, Blocks.BEDROCK.getDefaultState());
				continue;
			}

			if (state.getMaterial() == Material.AIR) {
				// topBlocks and dirtBlocks can occur after any pocket of air
				topBlocksToFill = (topBlock == null ? 0 : 1);
				dirtBlocksToFill = dirtDepth;
				continue;
			} else if (!hitFloorYet && state.getMaterial() == Material.WATER) {
				// seaFloorBlocks can occur after surface water
				seaFloorBlocksToFill = seaFloorDepth;
			}

			if (state.getBlock() == Blocks.STONE) {
				hitFloorYet = true;
				if (topBlocksToFill > 0) {
					if (y >= 62) {
						primer.setBlockState(localZ, y, localX, topBlock);
					} else if (y >= 56 - dirtDepth) {
						primer.setBlockState(localZ, y, localX, fillerBlock);
					} else {
						primer.setBlockState(localZ, y, localX, Blocks.GRAVEL.getDefaultState());
						dirtBlocksToFill = 0;
					}
					topBlocksToFill--;
				} else if (seaFloorBlocksToFill > 0) {
					primer.setBlockState(localZ, y, localX, seaFloorBlock);
					--seaFloorBlocksToFill;
				} else if (dirtBlocksToFill > 0) {
					primer.setBlockState(localZ, y, localX, fillerBlock);
					--dirtBlocksToFill;
				}

			}
		}


	}


	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos) {


		int maxSandStone = 100;
		for (int sandstone = 0; sandstone < maxSandStone; ++sandstone) {
			BlockPos sstonePos = worldIn.getTopSolidOrLiquidBlock(pos.add(rand.nextInt(16), 0, rand.nextInt(16)));
			if (worldIn.getBlockState(sstonePos.down()).getBlock() == TBlocks.gallifreyan_sand) {
				worldIn.setBlockState(sstonePos.down(), SANDSTONE);
			}
		}

		int maxDirt = 25;
		for (int dirt = 0; dirt < maxDirt; ++dirt) {
			BlockPos dirtPos = worldIn.getTopSolidOrLiquidBlock(pos.add(rand.nextInt(16), 0, rand.nextInt(16)));
			if (worldIn.getBlockState(dirtPos.down()).getBlock() == TBlocks.gallifreyan_sand) {
				worldIn.setBlockState(dirtPos.down(), DIRT);
			}
		}

		int maxGrass = 16;
		for (int grass = 0; grass < maxGrass; ++grass) {
			BlockPos grassPos = worldIn.getTopSolidOrLiquidBlock(pos.add(rand.nextInt(14), 0, rand.nextInt(14)));
			if (worldIn.getBlockState(grassPos.down()).getBlock() == TBlocks.gallifreyan_dirt) {
				worldIn.setBlockState(grassPos, GRASS);
			}
		}


		int maxSkull = 1;
		for (int skull = 0; skull < maxSkull; ++skull) {

			int percentageSpawn = rand.nextInt(100);

			if (percentageSpawn == 1) {

				BlockPos skullPos = worldIn.getTopSolidOrLiquidBlock(pos.add(rand.nextInt(16), 0, rand.nextInt(16)));
				worldIn.setBlockState(skullPos, Blocks.SKULL.getDefaultState().withProperty(BlockSkull.FACING, randomEnum(EnumFacing.class, rand)));
			}
		}


		int maxBarns = rand.nextInt(2);
		for (int barns = 0; barns < maxBarns; ++barns) {

			int x = rand.nextInt(15);
			int z = rand.nextInt(15);


			BlockPos barnPos = worldIn.getTopSolidOrLiquidBlock(pos.add(rand.nextInt(14), 0, rand.nextInt(14)));

			int barnChance = rand.nextInt(1000);
			if (barnChance == 1) {

				generateGallifreySheds(worldIn, worldIn.getTopSolidOrLiquidBlock(barnPos), BARNS[rand.nextInt(BARNS.length)]);
			}

		}

	}

	public static void generateGallifreySheds(World world, BlockPos pos, ResourceLocation location) {

		if (!world.isRemote) {

			Template shedTemp = ((WorldServer) world).getStructureTemplateManager().get(world.getMinecraftServer(), location);
			BlockPos shedPos = pos.add(-shedTemp.getSize().getX() / 2, -1, -shedTemp.getSize().getZ() / 2);
			if (world.getBlockState(shedPos).isSideSolid(world, shedPos, EnumFacing.UP))
				shedTemp.addBlocksToWorld(world, shedPos, new PlacementSettings());
		}

	}


	public static <T extends Enum<?>> T randomEnum(Class<T> clazz, Random random) {
		int x = random.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}
}


