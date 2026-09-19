package org.powernukkitx.level.updater.chunk;

import org.cloudburstmc.nbt.NbtMap;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockConnectable;
import org.powernukkitx.block.BlockFence;
import org.powernukkitx.block.BlockStairs;
import org.powernukkitx.block.BlockWallBase;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelChunkMetaData;
import org.powernukkitx.level.updater.block.BlockStateUpdater_1_26_50;
import org.powernukkitx.registry.Registries;;


/**
 * Performs contextual block-state upgrades using the persisted neighbor-aware chunk version.
 *
 * @author Curse
 */
public final class NeighborAwareChunkUpgrader {

    private enum UpdateType {
        NONE,
        WALL_BLOCK_CONNECTIONS,
        CONNECTION_UPGRADE
    }

    private NeighborAwareChunkUpgrader() {
    }

    /**
     * Attempts pending neighbor-aware upgrades around a newly available chunk.
     */
    public static void tryUpgradeAround(Level level, int chunkX, int chunkZ) {
        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                IChunk chunk = level.getChunkIfLoaded(x, z);
                if (chunk == null || !verifyChunkNeedsNeighborAwareUpgrade(level, chunk)) continue;

                if (isNeighborhoodLoaded(level, x, z)) {
                    doLevelChunkNeighborAwareUpgrade(level, chunk);
                }
            }
        }
    }

    private static boolean verifyChunkNeedsNeighborAwareUpgrade(Level level, IChunk chunk) {
        int version = getUpgradeVersion(chunk);
        if (version >= LevelChunkMetaData.CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION) {
            return false;
        }

        final int offsetX = chunk.getX() << 4;
        final int offsetZ = chunk.getZ() << 4;

        for (ChunkSection section : chunk.getSections()) {
            if (section == null) {
                continue;
            }

            final int offsetY = section.y() << 4;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        var state = section.getBlockState(x, y, z);
                        Block block = Registries.BLOCK.get(state, offsetX + x, offsetY + y, offsetZ + z, level);

                        if (getUpdateType(block, state.getIdentifier(), version) != UpdateType.NONE) {
                            return true;
                        }
                    }
                }
            }
        }

        finalizeUpgrade(chunk);
        return false;
    }

    private static boolean isNeighborhoodLoaded(Level level, int chunkX, int chunkZ) {
        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                if (level.getChunkIfLoaded(x, z) == null) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void doLevelChunkNeighborAwareUpgrade(Level level, IChunk chunk) {
        final int version = getUpgradeVersion(chunk);
        final int offsetX = chunk.getX() << 4;
        final int offsetZ = chunk.getZ() << 4;

        for (ChunkSection section : chunk.getSections()) {
            if (section == null) {
                continue;
            }

            final int offsetY = section.y() << 4;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        var state = section.getBlockState(x, y, z);
                        Block block = Registries.BLOCK.get(state, offsetX + x, offsetY + y, offsetZ + z, level);

                        boolean changed = switch (getUpdateType(block, state.getIdentifier(), version)) {
                            case NONE -> false;
                            case WALL_BLOCK_CONNECTIONS -> block instanceof BlockWallBase wall && wall.autoConfigureState();
                            case CONNECTION_UPGRADE -> {
                                if (block instanceof BlockStairs stairs) {
                                    yield stairs.autoConfigureState();
                                }
                                if (block instanceof BlockConnectable connectable) {
                                    yield connectable.updateConnections();
                                }
                                yield false;
                            }
                        };

                        if (changed) {
                            chunk.setBlockState(x, offsetY + y, z, block.getBlockState());
                        }
                    }
                }
            }
        }

        finalizeUpgrade(chunk);
    }

    private static int getUpgradeVersion(IChunk chunk) {
        LevelChunkMetaData chunkMetaData = chunk.getLevelChunkMetaData();
        if (chunkMetaData.getState() != LevelChunkMetaData.State.RESOLVED) {
            return LevelChunkMetaData.CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION;
        }

        Object value = chunkMetaData.getMetadata().get(LevelChunkMetaData.NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION);
        return value instanceof Number number
                ? number.intValue()
                : LevelChunkMetaData.CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION;
    }

    private static UpdateType getUpdateType(Block block, String identifier, int version) {
        if (version >= LevelChunkMetaData.CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION) {
            return UpdateType.NONE;
        }

        if (version <= LevelChunkMetaData.NEIGHBOR_AWARE_BLOCK_UPGRADE_NONE && block instanceof BlockWallBase) {
            return UpdateType.WALL_BLOCK_CONNECTIONS;
        }

        if (version <= LevelChunkMetaData.WALL_BLOCK_UPGRADE_VERSION && block instanceof BlockFence) {
            return UpdateType.CONNECTION_UPGRADE;
        }

        if (version <= LevelChunkMetaData.FENCE_BLOCK_UPGRADE_VERSION
                && BlockStateUpdater_1_26_50.CORNER_BLOCKS.contains(identifier)) {
            return UpdateType.CONNECTION_UPGRADE;
        }

        if (version <= LevelChunkMetaData.STAIR_BLOCK_UPGRADE_VERSION
                && BlockStateUpdater_1_26_50.THIN_CONNECTION_BLOCKS.contains(identifier)) {
            return UpdateType.CONNECTION_UPGRADE;
        }

        return UpdateType.NONE;
    }

    private static void finalizeUpgrade(IChunk chunk) {
        LevelChunkMetaData chunkMetaData = chunk.getLevelChunkMetaData();

        NbtMap metadata = chunkMetaData.getMetadata().toBuilder()
                .putInt(
                        LevelChunkMetaData.NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION,
                        LevelChunkMetaData.CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION
                )
                .build();

        chunk.setLevelChunkMetaData(
                LevelChunkMetaData.resolved(
                        chunkMetaData.getPersistedHash(),
                        metadata
                )
        );

        chunk.setChanged();
    }
}
