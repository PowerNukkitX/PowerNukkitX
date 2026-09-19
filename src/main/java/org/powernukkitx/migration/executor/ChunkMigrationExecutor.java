package org.powernukkitx.migration.executor;

import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelChunkMetaData;
import org.powernukkitx.level.format.leveldb.LevelDBChunkSerializer;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationService;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.ChunkMigrationData;
import org.powernukkitx.migration.leveldb.LevelDBMigrationChunkSerializer;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Orchestrates chunk storage migrations and commits the resulting canonical LevelDB terrain and metadata.
 *
 * @author Curse
 */
public final class ChunkMigrationExecutor {

    private ChunkMigrationExecutor() {
    }

    /**
     * Executes pending migrations for one stored chunk.
     */
    public static CompoundTag migrate(
            DimensionData dimensionData,
            DB db,
            LevelDBStorage storage,
            WriteBatch batch,
            IChunk chunk,
            int generatorType,
            CompoundTag extraData,
            MigrationService migrationService,
            MigrationVersion currentVersion
    ) throws IOException {
        if (migrationService.getStepsAfter(MigrationFormat.CHUNK, currentVersion).size() == 0) {
            return extraData;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        List<ChunkMigrationData.ScheduledTick> scheduledTicks = new ArrayList<>();

        for (LevelDBMigrationChunkSerializer.ScheduledTickInfo info : LevelDBMigrationChunkSerializer.readLegacyScheduledTicks(chunk, extraData)) {
            scheduledTicks.add(
                    new ChunkMigrationData.ScheduledTick(
                            info.x(),
                            info.y(),
                            info.z(),
                            info.delay(),
                            info.blockState()
                    )
            );
        }

        byte[] pendingTicksKey = LevelDBKeyUtil.PENDING_TICKS.getKey(chunkX, chunkZ, dimensionData);
        ChunkMigrationData migrated = migrationService.apply(
                MigrationFormat.CHUNK,
                currentVersion,
                new ChunkMigrationData(
                        chunkX,
                        chunkZ,
                        extraData,
                        scheduledTicks,
                        db.get(pendingTicksKey) != null,
                        null
                )
        );

        normalizeLegacySnowlogging(chunk);
        chunk.recalculateHeightMap();
        LevelDBChunkSerializer.INSTANCE.serializeTerrain(batch, chunk);
        serializeLegacyBorderBlocks(batch, chunk, dimensionData);
        batch.delete(LevelDBKeyUtil.DATA_2D.getKey(chunkX, chunkZ, dimensionData));

        for (int sectionY = dimensionData.getMinSectionY(); sectionY <= dimensionData.getMaxSectionY(); sectionY++) {
            byte[] legacyLightKey = LevelDBKeyUtil.CHUNK_SECTION_PREFIX.getKey(chunkX, chunkZ, sectionY, dimensionData);
            legacyLightKey[legacyLightKey.length - 2] = (byte) '}';
            batch.delete(legacyLightKey);
        }

        batch.put(LevelDBKeyUtil.VERSION.getKey(chunkX, chunkZ, dimensionData), new byte[]{IChunk.VERSION});
        batch.delete(LevelDBKeyUtil.LEGACY_VERSION.getKey(chunkX, chunkZ, dimensionData));

        byte[] finalizationKey = LevelDBKeyUtil.CHUNK_FINALIZED_STATE.getKey(chunkX, chunkZ, dimensionData);
        ChunkFinalizationState finalizationState = LevelDBMigrationChunkSerializer.decodeLegacyFinalizationState(db.get(finalizationKey));
        batch.put(finalizationKey, intToLittleEndian(finalizationState.getStorageValue()));

        if (migrated.pendingTicks() != null) {
            batch.put(pendingTicksKey, writeLittleEndianCompound(migrated.pendingTicks()));
        }

        storage.writeChunkMetaData(
                batch,
                chunk,
                generatorType,
                LevelChunkMetaData.LEGACY_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION
        );
        storage.writeLevelChunkMetaDataDictionary(batch);

        return migrated.extraData();
    }

    private static void normalizeLegacySnowlogging(IChunk chunk) {
        for (ChunkSection section : chunk.getSections()) {
            if (section == null || section.isEmpty()) continue;

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState secondary = section.getBlockState(x, y, z, 1);
                        if (!BlockID.SNOW_LAYER.equals(secondary.getIdentifier())) continue;

                        BlockState primary = section.getBlockState(x, y, z, 0);
                        if (primary.equals(BlockAir.STATE)) {
                            section.setBlockState(x, y, z, secondary, 0);
                            section.setBlockState(x, y, z, BlockAir.STATE, 1);
                            continue;
                        }

                        var precipitationBehavior = primary.toBlock().getPrecipitationBehavior();
                        if (precipitationBehavior != null && precipitationBehavior.isSnowLoggable()) {
                            section.setBlockState(x, y, z, primary, 1);
                            section.setBlockState(x, y, z, secondary, 0);
                        } else {
                            section.setBlockState(x, y, z, BlockAir.STATE, 1);
                        }
                    }
                }
            }
        }
    }

    private static void serializeLegacyBorderBlocks(WriteBatch batch, IChunk chunk, DimensionData dimensionData) {
        byte[] key = LevelDBKeyUtil.BORDER_BLOCKS.getKey(chunk.getX(), chunk.getZ(), dimensionData);
        byte[] data = new byte[256];
        ChunkSection[] sections = chunk.getSections();
        int count = 0;

        outer:
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                if (!hasLegacyBorderBlock(sections, localX, localZ)) continue;
                if (count >= 255) break outer;

                data[++count] = (byte) ((localX << 4) | localZ);
            }
        }

        if (count == 0) {
            batch.delete(key);
            return;
        }

        // Legacy PNX stored Border Blocks only in terrain; BDS persists one X/Z column entry under LevelDB 0x38.
        data[0] = (byte) count;
        batch.put(key, Arrays.copyOf(data, count + 1));
    }

    private static boolean hasLegacyBorderBlock(ChunkSection[] sections, int localX, int localZ) {
        for (ChunkSection section : sections) {
            if (section == null || section.isEmpty()) continue;

            for (int localY = 0; localY < 16; localY++) {
                if (BlockID.BORDER_BLOCK.equals(section.getBlockState(localX, localY, localZ).getIdentifier())) {
                    return true;
                }
            }
        }

        return false;
    }

    private static byte[] intToLittleEndian(int value) {
        return new byte[]{
                (byte) (value & 0xff),
                (byte) ((value >>> 8) & 0xff),
                (byte) ((value >>> 16) & 0xff),
                (byte) ((value >>> 24) & 0xff)
        };
    }

    private static byte[] writeLittleEndianCompound(CompoundTag tag) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (NBTOutputStream nbt = NbtUtils.createWriterLE(output)) {
            nbt.writeTag(tag.toNetwork());
        }
        return output.toByteArray();
    }
}
