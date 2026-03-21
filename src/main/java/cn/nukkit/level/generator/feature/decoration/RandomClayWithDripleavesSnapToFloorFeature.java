package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.*;
import cn.nukkit.block.property.enums.BigDripleafTilt;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.ArrayList;

import static cn.nukkit.block.BlockID.DEEPSLATE;
import static cn.nukkit.block.BlockID.STONE;
import static cn.nukkit.block.BlockID.WATER;
import static cn.nukkit.block.property.CommonBlockProperties.BIG_DRIPLEAF_HEAD;
import static cn.nukkit.block.property.CommonBlockProperties.BIG_DRIPLEAF_TILT;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.UPPER_BLOCK_BIT;

public class RandomClayWithDripleavesSnapToFloorFeature extends GenerateFeature {

    public static final String NAME = "minecraft:random_clay_with_dripleaves_snap_to_floor_feature";

    private static final BlockState CLAY = BlockClay.PROPERTIES.getDefaultState();
    private static final BlockState STILL_WATER = BlockWater.PROPERTIES.getDefaultState();

    private SimplexF noise;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if(noise == null) noise = new SimplexF(new NukkitRandom(chunk.getLevel().getSeed()), 1f, 2 / 4f, 1 / 15f);
        BlockManager manager = new BlockManager(level);
        for(int x = 0; x < 16; x++) {
            int baseX = ((chunk.getX() << 4) + x);
            for(int z = 0; z < 16; z++) {
                int baseZ = ((chunk.getZ() << 4) + z);
                if(noise.noise2D(baseX * 0.25f, baseZ * 0.25f, true) > 0.3) {
                    for(int y : getHighestWorkableBlocks(chunk, x, z)) {
                        int depth = (int) NukkitMath.clamp(NukkitMath.remapFromNormalized(noise.noise3D(baseX, y, baseZ, true), 3, 5), 3, 4);
                        for(int i = 0; i < depth; i++) {
                            if(chunk.getSection(i >> 4).getBiomeId(x, i & 0x0f, z) == BiomeID.LUSH_CAVES) {
                                manager.setBlockStateAt(baseX, y - i, baseZ, CLAY);
                            }
                        }
                    }
                }
            }
        }
        for(Block block : manager.getBlocks()) {
            if(block.isSolid() && block.up().isAir()) {
                if(noise.noise3D(block.getFloorX(), block.getFloorY(), block.getFloorZ(), true) > 0.1f) {
                    boolean clay = true;
                    for (BlockFace face : BlockFace.getHorizontals()) {
                        BlockVector3 side = new BlockVector3(block.getFloorX(), block.getFloorY(), block.getFloorZ()).getSide(face);
                        if (!manager.isCached(new BlockVector3(side.x, side.y, side.z))) {
                            clay = false;
                            break;
                        }
                    }
                    if(clay) {
                        manager.setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), WATER);
                    }
                }
            }
        }
        for(Block block : manager.getBlocks()) {
            if(block.up().canBeReplaced()) {
                if(random.nextFloat() < 0.01) {
                    MinecraftCardinalDirection direction = MinecraftCardinalDirection.VALUES[random.nextInt(MinecraftCardinalDirection.VALUES.length - 1)];
                    if(random.nextInt(9) < 8) {
                        BlockState HEAD = BlockBigDripleaf.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(direction), BIG_DRIPLEAF_HEAD.createValue(true), BIG_DRIPLEAF_TILT.createValue(BigDripleafTilt.NONE));
                        BlockState STEM = BlockBigDripleaf.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(direction));
                        int size = random.nextInt(block instanceof BlockWater ? 1 : 0, 4);
                        for(int i = 0; i < size; i++) {
                            BlockVector3 vector3 = block.asBlockVector3().up(i);
                            Block block1 = manager.getBlockIfCachedOrLoaded(vector3.asVector3());
                            if(block1 instanceof BlockWater) {
                                manager.setBlockStateAt(vector3.x, vector3.y, vector3.z, 0, STEM);
                                manager.setBlockStateAt(vector3.x, vector3.y, vector3.z, 1, STILL_WATER);
                            } else if(block1.isAir()) {
                                manager.setBlockStateAt(vector3.x, vector3.y, vector3.z, 0, STEM);
                            }
                        }
                        manager.setBlockStateAtIfCacheAbsent(new BlockVector3(block.getFloorX(), block.getFloorY() + size, block.getFloorZ()), HEAD);
                    } else {
                        BlockVector3 vector3 = block.asBlockVector3();
                        BlockState HEAD = BlockSmallDripleafBlock.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(direction), UPPER_BLOCK_BIT.createValue(true));
                        BlockState STEM = BlockSmallDripleafBlock.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(direction));
                        int offset = 1;
                        if(block instanceof BlockWater) {
                            manager.setBlockStateAt(vector3.x, vector3.y, vector3.z, 1, STILL_WATER);
                            offset = 0;
                        }
                        manager.setBlockStateAt(vector3.x, vector3.y + offset, vector3.z, 0, STEM);
                        manager.setBlockStateAt(vector3.x, vector3.y + 1 + offset, vector3.z, 0, HEAD);
                    }
                }
            }
        }
        queueObject(chunk, manager);
    }


    private ArrayList<Integer> getHighestWorkableBlocks(IChunk chunk, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = chunk.getHeightMap(x, z); y > chunk.getLevel().getMinHeight(); --y) {
            if (chunk.getSection(y >> 4).getBiomeId(x, y & 0x0f, z) == BiomeID.LUSH_CAVES) {
                String b = chunk.getBlockState(x, y, z).getIdentifier();
                if ((b == STONE || b == DEEPSLATE || b == BlockID.CLAY) && chunk.getBlockState(x, y + 1, z) == BlockAir.STATE) {
                    blockYs.add(y);
                }
            }
        }
        return blockYs;
    }

    @Override
    public String name() {
        return NAME;
    }
}
