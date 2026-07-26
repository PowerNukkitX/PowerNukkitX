package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockSulfurSpike;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.block.property.enums.DripstoneThickness;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.tags.BlockTags;

import java.util.ArrayList;

import static org.powernukkitx.block.property.CommonBlockProperties.DRIPSTONE_THICKNESS;
import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;

public class SulfurSpikeClusterFeature extends GenerateFeature {

    public static final String NAME = "minecraft:sulfur_spike_cluster_feature";

    private static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunk.getX(), chunk.getZ()) ^ NAME.hashCode());

        BlockManager manager = new BlockManager(level);
        int minY = level.getMinHeight();
        int maxY = Math.min(256, level.getMaxHeight() - 1);
        int attempts = random.nextInt(48, 96);

        for (int i = 0; i < attempts; i++) {
            int centerX = (chunk.getX() << 4) + random.nextBoundedInt(15);
            int centerZ = (chunk.getZ() << 4) + random.nextBoundedInt(15);
            int centerY = random.nextInt(minY, maxY);
            if (!isSulfurCave(level, centerX, centerY, centerZ)) {
                continue;
            }

            int radius = random.nextInt(2, 8);
            float density = 0.3f + random.nextFloat() * 0.4f;
            int maxSpikeHeight = random.nextInt(1, 4);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if ((dx * dx) + (dz * dz) > radius * radius || random.nextFloat() > density) {
                        continue;
                    }
                    int x = centerX + dx;
                    int z = centerZ + dz;
                    if (!isInCurrentChunk(chunk, x, z)) {
                        continue;
                    }
                    if (random.nextBoolean()) {
                        tryPlaceClusterSpike(manager, x, centerY, z, false, maxSpikeHeight);
                    } else {
                        tryPlaceClusterSpike(manager, x, centerY, z, true, maxSpikeHeight);
                    }
                }
            }
        }

        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }

    private boolean isSulfurCave(Level level, int x, int y, int z) {
        return level.getBiomeId(x, y, z) == BiomeID.SULFUR_CAVES;
    }

    private boolean isInCurrentChunk(IChunk chunk, int x, int z) {
        return (x >> 4) == chunk.getX() && (z >> 4) == chunk.getZ();
    }

    private void tryPlaceClusterSpike(BlockManager manager, int x, int y, int z, boolean hanging, int maxSpikeHeight) {
        int step = hanging ? 1 : -1;
        for (int i = 0; i <= 12; i++) {
            int supportY = y + (i * step);
            Block support = manager.getBlockIfCachedOrLoaded(x, supportY, z);
            if (isBaseBlock(support.getBlockState())) {
                placeSulfurSpike(manager, x, hanging ? supportY - 1 : supportY + 1, z, hanging, nextRangeSafe(1, maxSpikeHeight));
                return;
            }
            if (!isAirOrWater(support)) {
                return;
            }
        }
    }

    private boolean isBaseBlock(BlockState state) {
        String id = state.getIdentifier();
        return BlockID.STONE.equals(id)
                || BlockID.DEEPSLATE.equals(id)
                || BlockID.TUFF.equals(id)
                || BlockID.SULFUR.equals(id)
                || BlockID.CINNABAR.equals(id);
    }

    private boolean isAirOrWater(Block block) {
        return block.isAir() || block.hasTag(BlockTags.WATER) || BlockID.SULFUR_SPIKE.equals(block.getId());
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
        boolean waterlogged = manager.getBlockIfCachedOrLoaded(x, y, z).hasTag(BlockTags.WATER)
                || manager.getLevel().getBlock(x, y, z, 1).hasTag(BlockTags.WATER);
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

    private int nextRangeSafe(int min, int max) {
        if (max <= min) {
            return min;
        }
        return random.nextInt(min, max);
    }
}
