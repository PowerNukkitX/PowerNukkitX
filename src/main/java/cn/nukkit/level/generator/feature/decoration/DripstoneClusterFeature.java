package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockDripstoneBlock;
import cn.nukkit.block.BlockPointedDripstone;
import cn.nukkit.block.property.enums.DripstoneThickness;
import cn.nukkit.block.BlockState;
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
import static cn.nukkit.block.BlockID.POINTED_DRIPSTONE;
import static cn.nukkit.block.BlockID.WATER;
import static cn.nukkit.block.property.CommonBlockProperties.DRIPSTONE_THICKNESS;
import static cn.nukkit.block.property.CommonBlockProperties.HANGING;

public class DripstoneClusterFeature extends GenerateFeature {

    public static final String NAME = "minecraft:dripstone_cluster_feature";

    private static final BlockState DRIPSTONE_BLOCK = BlockDripstoneBlock.PROPERTIES.getDefaultState();

    private SimplexF noise;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if(noise == null) noise = new SimplexF(new NukkitRandom(chunk.getLevel().getSeed()), 30f, 1 / 99f, 1 / 15f);
        BlockManager manager = new BlockManager(level);
        for(int x = 0; x < 16; x++) {
            int baseX = ((chunk.getX() << 4) + x);
            for(int z = 0; z < 16; z++) {
                int baseZ = ((chunk.getZ() << 4) + z);
                if(noise.noise2D(baseX * 1.25f, baseZ * 1.25f, true) > 0.1) {
                    boolean hasDripstoneCave = false;
                    for (int y = chunk.getHeightMap(x, z); y > level.getMinHeight(); y--) {
                        if(chunk.getSection(y >> 4).getBiomeId(x, y & 0x0f, z) == BiomeID.DRIPSTONE_CAVES) {
                            hasDripstoneCave = true;
                            break;
                        }
                    }
                    if(!hasDripstoneCave) continue;
                    for(int y : getHighestWorkableBlocks(chunk, x, z)) {
                        int depth = (int) NukkitMath.clamp(NukkitMath.remapFromNormalized(noise.noise3D(baseX, y, baseZ, true), 1, 3), 1, 2);
                        boolean water = false;
                        for(int i = 0; i < depth; i++) {
                            if(i == 0 && random.nextFloat() < 0.002f) {
                                boolean air = false;
                                for (BlockFace face : BlockFace.getHorizontals()) {
                                    BlockVector3 side = new BlockVector3(baseX, y, baseZ).getSide(face);
                                    if (manager.getBlockIfCachedOrLoaded(side.x, side.y, side.z).isAir()) {
                                        air = true;
                                        break;
                                    }
                                }
                                if(!air) {
                                    manager.setBlockStateAt(baseX, y, baseZ, WATER);
                                    water = true;
                                }
                            } else manager.setBlockStateAt(baseX, y-i, baseZ, DRIPSTONE_BLOCK);

                        }
                        if(!wa  ter && random.nextFloat() < 0.1) {
                            placePointedDripstone(manager, baseX, y + 1, baseZ, false, random.nextInt(1, 3));
                        }
                    }
                    for(int y : getLowestWorkableBlocks(chunk, x, z)) {
                        int depth = (int) NukkitMath.clamp(NukkitMath.remapFromNormalized(noise.noise3D(baseX, y, baseZ, true), 1, 3), 1, 2);
                        for(int i = 0; i < depth; i++) {
                            manager.setBlockStateAt(baseX, y+i, baseZ, DRIPSTONE_BLOCK);
                        }
                        if(random.nextFloat() < 0.3) {
                            placePointedDripstone(manager, baseX, y - 1, baseZ, true, random.nextInt(1, 5));
                        }
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
            String b = chunk.getBlockState(x, y, z).getIdentifier();
            if ((b == STONE || b == DEEPSLATE) && chunk.getBlockState(x, y + 1, z) == BlockAir.STATE) {
                blockYs.add(y);
            }
        }
        return blockYs;
    }

    private ArrayList<Integer> getLowestWorkableBlocks(IChunk chunk, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = chunk.getHeightMap(x, z); y > chunk.getLevel().getMinHeight(); --y) {
            String b = chunk.getBlockState(x, y, z).getIdentifier();
            if ((b == STONE || b == DEEPSLATE) && chunk.getBlockState(x, y - 1, z) == BlockAir.STATE) {
                blockYs.add(y);
            }
        }
        return blockYs;
    }

    @Override
    public String name() {
        return NAME;
    }

    private void placePointedDripstone(BlockManager manager, int x, int y, int z, boolean hanging, int maxLength) {
        ArrayList<Integer> plannedY = new ArrayList<>(maxLength);
        int step = hanging ? -1 : 1;
        int mergeY = Integer.MIN_VALUE;

        for (int i = 0; i < maxLength; i++) {
            int currentY = y + (i * step);
            Block currentBlock = manager.getBlockIfCachedOrLoaded(x, currentY, z);
            if (currentBlock.getId().equals(POINTED_DRIPSTONE)) {
                if (currentBlock.getPropertyValue(HANGING) != hanging && !plannedY.isEmpty()) {
                    mergeY = currentY;
                }
                break;
            }
            if (!currentBlock.isAir()) {
                break;
            }
            plannedY.add(currentY);
        }

        if (plannedY.isEmpty()) {
            return;
        }

        int lastIndex = plannedY.size() - 1;
        for (int i = 0; i < plannedY.size(); i++) {
            DripstoneThickness thickness = getThicknessForIndex(i, lastIndex, mergeY != Integer.MIN_VALUE);
            manager.setBlockStateAt(x, plannedY.get(i), z, getDripstoneState(hanging, thickness));
        }

        if (mergeY != Integer.MIN_VALUE) {
            manager.setBlockStateAt(x, mergeY, z, getDripstoneState(!hanging, DripstoneThickness.MERGE));
        }
    }

    private DripstoneThickness getThicknessForIndex(int index, int lastIndex, boolean merged) {
        if (index == lastIndex) {
            return merged ? DripstoneThickness.MERGE : DripstoneThickness.TIP;
        }
        if (index == 0 && lastIndex >= 2) {
            return DripstoneThickness.BASE;
        }
        if (index == lastIndex - 1) {
            return DripstoneThickness.FRUSTUM;
        }
        return DripstoneThickness.MIDDLE;
    }

    private BlockState getDripstoneState(boolean hanging, DripstoneThickness thickness) {
        return BlockPointedDripstone.PROPERTIES.getBlockState(
                HANGING.createValue(hanging),
                DRIPSTONE_THICKNESS.createValue(thickness)
        );
    }
}
