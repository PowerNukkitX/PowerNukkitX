package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCinnabar;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockSulfur;
import cn.nukkit.block.BlockSulfurSpike;
import cn.nukkit.block.BlockWater;
import cn.nukkit.block.property.enums.DripstoneThickness;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.level.generator.object.BlockManager;

import java.util.ArrayList;

import static cn.nukkit.block.property.CommonBlockProperties.DRIPSTONE_THICKNESS;
import static cn.nukkit.block.property.CommonBlockProperties.HANGING;

public class SulfurSpikeFeature extends GenerateFeature {

    public static final String NAME = "minecraft:sulfur_spike_feature";

    private static final BlockState CINNABAR = BlockCinnabar.PROPERTIES.getDefaultState();
    private static final BlockState SULFUR = BlockSulfur.PROPERTIES.getDefaultState();
    private static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunk.getX(), chunk.getZ()) ^ NAME.hashCode());

        BlockManager manager = new BlockManager(level);
        SimplexNoise gradient = ((NormalObjectHolder) level.getGeneratorObjectHolder()).getFeatureHolder().getSulfurCaveGradient();
        applyNoiseGradientSurface(chunk, manager, gradient);

        int minY = level.getMinHeight();
        int maxY = Math.min(256, level.getMaxHeight() - 1);
        int attempts = random.nextInt(192, 256);
        for (int i = 0; i < attempts; i++) {
            int baseX = (chunk.getX() << 4) + random.nextBoundedInt(15);
            int baseZ = (chunk.getZ() << 4) + random.nextBoundedInt(15);
            int baseY = random.nextInt(minY, maxY);
            int innerCount = random.nextInt(1, 5);

            for (int j = 0; j < innerCount; j++) {
                int x = baseX + clampedNormal(3.0f, 10);
                int y = baseY + clampedNormal(0.6f, 2);
                int z = baseZ + clampedNormal(3.0f, 10);
                if (!isInCurrentChunk(chunk, x, z) || y < minY || y > maxY || !isSulfurCave(chunk, x & 0x0f, y, z & 0x0f)) {
                    continue;
                }
                if (random.nextBoolean()) {
                    tryPlaceFromFloorScan(manager, gradient, x, y, z);
                } else {
                    tryPlaceFromCeilingScan(manager, gradient, x, y, z);
                }
            }
        }

        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }

    private boolean isSulfurCave(IChunk chunk, int x, int y, int z) {
        return chunk.getSection(y >> 4).getBiomeId(x, y & 0x0f, z) == BiomeID.SULFUR_CAVES;
    }

    private boolean isInCurrentChunk(IChunk chunk, int x, int z) {
        return (x >> 4) == chunk.getX() && (z >> 4) == chunk.getZ();
    }

    private void applyNoiseGradientSurface(IChunk chunk, BlockManager manager, SimplexNoise gradient) {
        int minY = chunk.getLevel().getMinHeight();
        for (int localX = 0; localX < 16; localX++) {
            int worldX = (chunk.getX() << 4) + localX;
            for (int localZ = 0; localZ < 16; localZ++) {
                int worldZ = (chunk.getZ() << 4) + localZ;
                for (int y = Math.min(256, chunk.getHeightMap(localX, localZ)); y > minY + 1; y--) {
                    if (!isSulfurCave(chunk, localX, y, localZ)) {
                        continue;
                    }
                    BlockState state = chunk.getBlockState(localX, y, localZ);
                    if (!isNaturalBaseBlock(state) || !isExposed(manager, worldX, y, worldZ)) {
                        continue;
                    }
                    BlockState gradientState = getGradientSurfaceState(gradient.getValue(worldX, y, worldZ));
                    if (gradientState != null) {
                        manager.setBlockStateAt(worldX, y, worldZ, gradientState);
                    }
                }
            }
        }
    }

    private boolean isExposed(BlockManager manager, int x, int y, int z) {
        return isAirOrWater(manager.getBlockIfCachedOrLoaded(x, y + 1, z))
                || isAirOrWater(manager.getBlockIfCachedOrLoaded(x, y - 1, z))
                || isAirOrWater(manager.getBlockIfCachedOrLoaded(x + 1, y, z))
                || isAirOrWater(manager.getBlockIfCachedOrLoaded(x - 1, y, z))
                || isAirOrWater(manager.getBlockIfCachedOrLoaded(x, y, z + 1))
                || isAirOrWater(manager.getBlockIfCachedOrLoaded(x, y, z - 1));
    }

    private boolean isBaseBlock(BlockState state) {
        String id = state.getIdentifier();
        return isNaturalBaseBlock(state)
                || BlockID.SULFUR.equals(id)
                || BlockID.CINNABAR.equals(id);
    }

    private boolean isNaturalBaseBlock(BlockState state) {
        String id = state.getIdentifier();
        return BlockID.STONE.equals(id)
                || BlockID.DEEPSLATE.equals(id)
                || BlockID.TUFF.equals(id);
    }

    private BlockState getGradientSurfaceState(float noise) {
        if ((noise >= -0.4f && noise < -0.1f) || (noise >= 0.4f && noise <= 1.0f)) {
            return CINNABAR;
        }
        if (noise >= 0.0f && noise < 0.4f) {
            return SULFUR;
        }
        return null;
    }

    private boolean isAirOrWater(Block block) {
        return block.isAir() || isWater(block);
    }

    private void tryPlaceFromFloorScan(BlockManager manager, SimplexNoise gradient, int x, int y, int z) {
        for (int i = 0; i <= 12; i++) {
            int currentY = y - i;
            Block currentBlock = manager.getBlockIfCachedOrLoaded(x, currentY, z);
            if (isBaseBlock(currentBlock.getBlockState())) {
                manager.setBlockStateAt(x, currentY, z, getSpikeBaseState(gradient, x, currentY, z));
                placeSulfurSpike(manager, x, currentY + 1, z, false, random.nextInt(1, 5));
                return;
            }
            if (!isAirOrWater(currentBlock)) {
                return;
            }
        }
    }

    private void tryPlaceFromCeilingScan(BlockManager manager, SimplexNoise gradient, int x, int y, int z) {
        for (int i = 0; i <= 12; i++) {
            int currentY = y + i;
            Block currentBlock = manager.getBlockIfCachedOrLoaded(x, currentY, z);
            if (isBaseBlock(currentBlock.getBlockState())) {
                manager.setBlockStateAt(x, currentY, z, getSpikeBaseState(gradient, x, currentY, z));
                placeSulfurSpike(manager, x, currentY - 1, z, true, random.nextInt(1, 5));
                return;
            }
            if (!isAirOrWater(currentBlock)) {
                return;
            }
        }
    }

    private BlockState getSpikeBaseState(SimplexNoise gradient, int x, int y, int z) {
        BlockState gradientState = getGradientSurfaceState(gradient.getValue(x, y, z));
        return gradientState == null ? SULFUR : gradientState;
    }

    private void placeSulfurSpike(BlockManager manager, int x, int y, int z, boolean hanging, int maxLength) {
        ArrayList<Integer> plannedY = new ArrayList<>(maxLength);
        int step = hanging ? -1 : 1;
        int mergeY = Integer.MIN_VALUE;

        for (int i = 0; i < maxLength; i++) {
            int currentY = y + (i * step);
            Block currentBlock = manager.getBlockIfCachedOrLoaded(x, currentY, z);
            if (BlockID.SULFUR_SPIKE.equals(currentBlock.getId())) {
                if (currentBlock.getPropertyValue(HANGING) != hanging && !plannedY.isEmpty()) {
                    mergeY = currentY;
                }
                break;
            }
            if (!isAirOrWater(currentBlock)) {
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
            setSulfurSpikeStateAt(manager, x, plannedY.get(i), z, getSulfurSpikeState(hanging, thickness));
        }

        if (mergeY != Integer.MIN_VALUE) {
            setSulfurSpikeStateAt(manager, x, mergeY, z, getSulfurSpikeState(!hanging, DripstoneThickness.MERGE));
        }
    }

    private void setSulfurSpikeStateAt(BlockManager manager, int x, int y, int z, BlockState state) {
        boolean waterlogged = isWater(manager.getBlockIfCachedOrLoaded(x, y, z))
                || isWater(manager.getLevel().getBlock(x, y, z, 1));
        manager.setBlockStateAt(x, y, z, state);
        if (waterlogged) {
            manager.setBlockStateAt(x, y, z, 1, WATER);
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

    private BlockState getSulfurSpikeState(boolean hanging, DripstoneThickness thickness) {
        return BlockSulfurSpike.PROPERTIES.getBlockState(
                HANGING.createValue(hanging),
                DRIPSTONE_THICKNESS.createValue(thickness)
        );
    }

    private boolean isWater(Block block) {
        return block instanceof BlockWater;
    }

    private int clampedNormal(float deviation, int max) {
        double u1 = Math.max(Double.MIN_VALUE, random.nextDouble());
        double u2 = random.nextDouble();
        int value = Math.round((float) (Math.sqrt(-2.0d * Math.log(u1)) * Math.cos(2.0d * Math.PI * u2) * deviation));
        if (value < -max) {
            return -max;
        }
        return Math.min(value, max);
    }
}
