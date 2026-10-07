package org.powernukkitx.migration.executor;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.leveldb.LevelDBMigrationChunkSerializer;
import org.powernukkitx.migration.MigrationService;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.steps.LevelDBBlockEntityV3_1_0Migration;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Orchestrates Block Entity storage migrations and post-migration world-level Block Entity reconciliation.
 *
 * @author Curse
 */
public final class BlockEntityMigrationExecutor {
    private static final byte[] NETHER_PORTALS_KEY = "portals".getBytes(StandardCharsets.UTF_8);

    private BlockEntityMigrationExecutor() {
    }

    /**
     * Executes pending Block Entity migrations for one stored chunk.
     */
    public static void migrate(
            DimensionData dimensionData,
            DB db,
            WriteBatch batch,
            IChunk chunk,
            CompoundTag extraData,
            Set<LevelDBBlockEntityV3_1_0Migration.Position> migratedNetherPortalBlocks,
            MigrationService migrationService,
            MigrationVersion currentVersion
    ) throws IOException {
        if (migrationService.getStepsAfter(MigrationFormat.BLOCK_ENTITY, currentVersion).size() == 0) {
            return;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        byte[] key = LevelDBKeyUtil.BLOCK_ENTITIES.getKey(chunkX, chunkZ, dimensionData);
        byte[] data = db.get(key);
        List<CompoundTag> blockEntities = data == null || data.length == 0
                ? new ArrayList<>()
                : LevelDBMigrationChunkSerializer.readLittleEndianCompounds(data);

        LevelDBBlockEntityV3_1_0Migration.LegacyStorageMigrationContext migrationContext =
                new LevelDBBlockEntityV3_1_0Migration.LegacyStorageMigrationContext() {
                    @Override
                    public Integer getDimensionId() {
                        return dimensionData.getDimensionId();
                    }

                    @Override
                    public CompoundTag getPnxExtraData() {
                        return extraData;
                    }

                    @Override
                    public boolean hasPersistedEndGatewayNear(int blockX, int blockZ) throws IOException {
                        return BlockEntityMigrationExecutor.hasPersistedEndGatewayNear(db, dimensionData, blockX, blockZ);
                    }

                    @Override
                    public long nextActorUniqueId() {
                        return migrationService.getContext().getServer().getNewActorUniqueId();
                    }

                    @Override
                    public long resolvePlayerUniqueId(UUID uuid) throws IOException {
                        try {
                            return migrationService.getContext().getServer().resolvePlayerUniqueId(uuid);
                        } catch (IllegalStateException e) {
                            return 0L;
                        }
                    }
                };

        LevelDBBlockEntityV3_1_0Migration.Data migrated = migrationService.apply(
                MigrationFormat.BLOCK_ENTITY,
                currentVersion,
                new LevelDBBlockEntityV3_1_0Migration.Data(
                        blockEntities,
                        chunk,
                        migrationContext,
                        false,
                        new HashSet<>()
                )
        );

        migratedNetherPortalBlocks.addAll(migrated.netherPortalBlocks());

        if (!migrated.changed()) return;

        if (migrated.blockEntities().size() == 0) {
            batch.delete(key);
            return;
        }

        batch.put(key, LevelDBMigrationChunkSerializer.writeLittleEndianCompounds(migrated.blockEntities()));
    }

    private static boolean hasPersistedEndGatewayNear(DB db, DimensionData dimensionData, int blockX, int blockZ) throws IOException {
        int centerChunkX = blockX >> 4;
        int centerChunkZ = blockZ >> 4;

        for (int chunkX = centerChunkX - 1; chunkX <= centerChunkX + 1; chunkX++) {
            for (int chunkZ = centerChunkZ - 1; chunkZ <= centerChunkZ + 1; chunkZ++) {
                byte[] data = db.get(LevelDBKeyUtil.BLOCK_ENTITIES.getKey(chunkX, chunkZ, dimensionData));
                if (data == null || data.length == 0) continue;

                for (CompoundTag blockEntity : LevelDBMigrationChunkSerializer.readLittleEndianCompounds(data)) {
                    if ("EndGateway".equals(blockEntity.getString("id"))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Reconciles migrated Nether Portal Block Entities into the canonical world portal registry.
     */
    public static void reconcileNetherPortals(
            LevelDBStorage storage,
            DimensionData dimensionData,
            Set<LevelDBBlockEntityV3_1_0Migration.Position> portalBlocks
    ) throws IOException {
        if (portalBlocks.size() == 0) return;

        DB db = storage.getDb();
        int dimensionId = dimensionData.getDimensionId();

        if (dimensionId != DimensionEnum.OVERWORLD.getDimensionData().getDimensionId()
                && dimensionId != DimensionEnum.NETHER.getDimensionData().getDimensionId()) return;

        byte[] existing = db.get(NETHER_PORTALS_KEY);
        CompoundTag root;

        if (existing == null) {
            root = new CompoundTag();
        } else {
            List<CompoundTag> roots = LevelDBMigrationChunkSerializer.readLittleEndianCompounds(existing);
            if (roots.size() != 1) {
                throw new IOException("Invalid BDS portals record");
            }
            root = roots.getFirst();
        }

        CompoundTag data = root.containsCompound("data") ? root.getCompound("data") : new CompoundTag();
        ListTag<CompoundTag> records = data.containsList("PortalRecords")
                ? data.getList("PortalRecords", CompoundTag.class)
                : new ListTag<>(Tag.TAG_Compound);

        for (CompoundTag record : records.getAll()) {
            if (record.getInt("DimId") == dimensionId) return;
        }

        List<CompoundTag> generated = createNetherPortalRecords(portalBlocks, dimensionId);
        generated.sort(Comparator.comparingInt((CompoundTag tag) -> tag.getInt("TpX"))
                .thenComparingInt(tag -> tag.getInt("TpY"))
                .thenComparingInt(tag -> tag.getInt("TpZ")));

        for (CompoundTag record : generated) {
            records.add(record);
        }

        data.putList("PortalRecords", records);
        root.putCompound("data", data);

        try (WriteBatch batch = storage.createBatch()) {
            batch.put(NETHER_PORTALS_KEY, LevelDBMigrationChunkSerializer.writeLittleEndianCompound(root));
            storage.writeBatch(batch);
        }
    }

    private static List<CompoundTag> createNetherPortalRecords(
            Set<LevelDBBlockEntityV3_1_0Migration.Position> portalBlocks,
            int dimensionId
    ) {
        Set<LevelDBBlockEntityV3_1_0Migration.Position> remaining = new HashSet<>(portalBlocks);
        List<CompoundTag> records = new ArrayList<>();

        while (remaining.size() != 0) {
            LevelDBBlockEntityV3_1_0Migration.Position start = remaining.iterator().next();
            List<LevelDBBlockEntityV3_1_0Migration.Position> component = new ArrayList<>();
            List<LevelDBBlockEntityV3_1_0Migration.Position> queue = new ArrayList<>();
            remaining.remove(start);
            queue.add(start);

            for (int i = 0; i < queue.size(); i++) {
                LevelDBBlockEntityV3_1_0Migration.Position position = queue.get(i);
                component.add(position);
                addNetherPortalNeighbor(remaining, queue, position.x() - 1, position.y(), position.z());
                addNetherPortalNeighbor(remaining, queue, position.x() + 1, position.y(), position.z());
                addNetherPortalNeighbor(remaining, queue, position.x(), position.y() - 1, position.z());
                addNetherPortalNeighbor(remaining, queue, position.x(), position.y() + 1, position.z());
                addNetherPortalNeighbor(remaining, queue, position.x(), position.y(), position.z() - 1);
                addNetherPortalNeighbor(remaining, queue, position.x(), position.y(), position.z() + 1);
            }

            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;

            for (LevelDBBlockEntityV3_1_0Migration.Position position : component) {
                minX = Math.min(minX, position.x());
                minY = Math.min(minY, position.y());
                minZ = Math.min(minZ, position.z());
                maxX = Math.max(maxX, position.x());
                maxZ = Math.max(maxZ, position.z());
            }

            boolean xAxis = maxX - minX > maxZ - minZ;
            int span = xAxis ? maxX - minX + 1 : maxZ - minZ + 1;

            records.add(new CompoundTag()
                    .putInt("DimId", dimensionId)
                    .putByte("Span", (byte) span)
                    .putInt("TpX", minX)
                    .putInt("TpY", minY)
                    .putInt("TpZ", minZ)
                    .putByte("Xa", (byte) (xAxis ? 1 : 0))
                    .putByte("Za", (byte) (xAxis ? 0 : 1)));
        }

        return records;
    }

    private static void addNetherPortalNeighbor(
            Set<LevelDBBlockEntityV3_1_0Migration.Position> remaining,
            List<LevelDBBlockEntityV3_1_0Migration.Position> queue,
            int x,
            int y,
            int z
    ) {
        LevelDBBlockEntityV3_1_0Migration.Position neighbor =
                new LevelDBBlockEntityV3_1_0Migration.Position(x, y, z);

        if (remaining.remove(neighbor)) {
            queue.add(neighbor);
        }
    }
}
