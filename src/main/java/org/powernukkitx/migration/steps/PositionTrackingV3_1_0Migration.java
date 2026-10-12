package org.powernukkitx.migration.steps;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.ServerDBStorageFormat;
import org.powernukkitx.blockentity.BlockEntityLodestone;
import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.PositionTrackingMigrationData;
import org.powernukkitx.migration.leveldb.LevelDBMigrationVersionStore;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Converts legacy PNX and native BDS position-tracking data to the 3.1.0 server-global representation. BDS imports reserve a global
 * handle range and rewrite every persisted Lodestone and lodestone-compass reference to that range before the source records are cleared.
 *
 * @author Curse
 */
public final class PositionTrackingV3_1_0Migration implements MigrationStep<PositionTrackingMigrationData> {
    private static final byte STATUS_ENABLED = 0;
    private static final byte STATUS_DISABLED = 1;
    private static final byte[] ACTOR_PREFIX = "actorprefix".getBytes(StandardCharsets.UTF_8);
    private static final byte[] PLAYER_SERVER_PREFIX = "player_server_".getBytes(StandardCharsets.UTF_8);
    private static final byte[] LEGACY_CONSOLE_PLAYER_PREFIX = "legacy_console_player_".getBytes(StandardCharsets.UTF_8);
    private static final byte[] LOCAL_PLAYER_KEY = "~local_player".getBytes(StandardCharsets.UTF_8);

    @Override
    public MigrationFormat format() {
        return MigrationFormat.POSITION_TRACKING;
    }

    @Override
    public MigrationVersion targetVersion() {
        return MigrationVersion.V3_1_0;
    }

    @Override
    public PositionTrackingMigrationData migrate(MigrationContext context, PositionTrackingMigrationData value) throws IOException {
        boolean remapSource = value.source() != PositionTrackingMigrationData.Source.CANONICAL;
        int offset = remapSource
                ? (value.reservedOffset() >= 0 ? value.reservedOffset() : value.targetLastId())
                : 0;

        int migratedLastId = remapSource
                ? Math.max(value.targetLastId(), addHandle(value.sourceLastId(), offset))
                : Math.max(value.targetLastId(), value.sourceLastId());
        List<PositionTrackingMigrationData.CanonicalEntry> migratedEntries = new ArrayList<>(value.entries().size());

        for (PositionTrackingMigrationData.Entry entry : value.entries()) {
            if (entry.handle() <= 0 || entry.handle() > value.sourceLastId()) {
                throw new IOException("Position-tracking handle is outside the source allocator range: " + entry.handle());
            }
            if (entry.levelName() == null || entry.levelName().isEmpty()) {
                throw new IOException("Position-tracking handle " + entry.handle() + " has no PNX level name");
            }
            if (entry.status() != STATUS_ENABLED && entry.status() != STATUS_DISABLED) {
                throw new IOException("Position-tracking handle " + entry.handle() + " has invalid status " + entry.status());
            }

            int handle = remapSource ? addHandle(entry.handle(), offset) : entry.handle();
            migratedEntries.add(new PositionTrackingMigrationData.CanonicalEntry(
                    handle,
                    entry.dimensionId(),
                    entry.levelName(),
                    exactBlockCoordinate(entry.x(), entry.handle(), "x"),
                    exactBlockCoordinate(entry.y(), entry.handle(), "y"),
                    exactBlockCoordinate(entry.z(), entry.handle(), "z"),
                    entry.status()
            ));
        }

        migratedEntries.sort(Comparator.comparingInt(PositionTrackingMigrationData.CanonicalEntry::handle));
        return new PositionTrackingMigrationData(
                value.source(),
                value.entries(),
                value.sourceLastId(),
                value.targetLastId(),
                value.reservedOffset(),
                migratedEntries,
                offset,
                migratedLastId
        );
    }

    /**
     * Rewrites lodestone-compass references stored inside one legacy PNX player or actor compound.
     */
    public static void remapLegacyPnxItems(CompoundTag root, int sourceLastId, int offset) throws IOException {
        remapItemReferences(root, sourceLastId, offset);
    }

    /**
     * Rewrites lodestone and nested item references before a legacy PNX block entity becomes canonical.
     */
    public static void remapLegacyPnxBlockEntity(CompoundTag blockEntity, int sourceLastId, int offset) throws IOException {
        remapItemReferences(blockEntity, sourceLastId, offset);

        if (!"Lodestone".equals(blockEntity.getString("id"))) {
            return;
        }

        int trackingHandle = readLodestoneHandle(blockEntity);
        if (trackingHandle <= 0) {
            return;
        }

        int migratedHandle = remapHandle(trackingHandle, sourceLastId, offset);

        if (blockEntity.contains("trackingHandle")) {
            blockEntity.putInt("trackingHandle", migratedHandle);
        }
        if (blockEntity.contains("trackingHandler")) {
            blockEntity.putInt("trackingHandler", migratedHandle);
        }
    }

    /**
     * Rewrites BDS references to a reserved global handle range and clears the original BDS position-tracking values atomically.
     */
    public static void remapBdsWorld(DB sourceDB, int sourceLastId, int offset) throws IOException {
        try (WriteBatch batch = sourceDB.createWriteBatch()) {
            try (DBIterator iterator = sourceDB.iterator()) {
                iterator.seekToFirst();
                while (iterator.hasNext()) {
                    Map.Entry<byte[], byte[]> entry = iterator.next();
                    byte[] key = entry.getKey();
                    byte[] value = entry.getValue();

                    if (isPositionKey(key)) {
                        batch.put(key, new byte[0]);
                        continue;
                    }
                    if (isBlockEntityKey(key) && value.length != 0) {
                        remapBlockEntities(sourceDB, batch, key, value, sourceLastId, offset);
                        continue;
                    }
                    if (isActorKey(key) && value.length != 0) {
                        remapCompoundRecord(batch, key, value, sourceLastId, offset, "actorprefix");
                        continue;
                    }
                    if (isBdsPlayerDataKey(key) && value.length != 0) {
                        remapCompoundRecord(batch, key, value, sourceLastId, offset, "BDS player");
                    }
                }
            }

            batch.put(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_LAST_ID_KEY, new byte[0]);
            sourceDB.write(batch);
        }
    }

    private static void remapBlockEntities(
            DB sourceDB,
            WriteBatch batch,
            byte[] key,
            byte[] value,
            int sourceLastId,
            int offset
    ) throws IOException {
        List<CompoundTag> blockEntities = readLittleEndianCompounds(value);
        boolean changed = false;
        boolean extraChanged = false;
        byte[] extraKey = key.clone();
        extraKey[extraKey.length - 1] = '|';
        CompoundTag extraData = null;

        for (CompoundTag blockEntity : blockEntities) {
            changed |= remapItemReferences(blockEntity, sourceLastId, offset);
            if (!"Lodestone".equals(blockEntity.getString("id"))) {
                continue;
            }

            int trackingHandle = readLodestoneHandle(blockEntity);
            if (trackingHandle > 0) {
                if (extraData == null) {
                    extraData = readBigEndianCompound(sourceDB.get(extraKey));
                    if (extraData == null) {
                        extraData = new CompoundTag();
                    }
                }
                int migratedHandle = remapHandle(trackingHandle, sourceLastId, offset);
                BlockEntityLodestone.setPnxTrackingHandle(
                        extraData,
                        blockEntity.getInt("x"),
                        blockEntity.getInt("y"),
                        blockEntity.getInt("z"),
                        migratedHandle
                );
                extraChanged = true;
            }

            if (blockEntity.contains("trackingHandle") || blockEntity.contains("trackingHandler")) {
                blockEntity.remove("trackingHandle", "trackingHandler");
                changed = true;
            }
        }

        if (changed) {
            batch.put(key, writeLittleEndianCompounds(blockEntities));
        }
        if (extraChanged) {
            LevelDBMigrationVersionStore.writeRuntimeVersions(extraData);
            batch.put(extraKey, writeBigEndianCompound(extraData));
        }
    }

    private static void remapCompoundRecord(
            WriteBatch batch,
            byte[] key,
            byte[] value,
            int sourceLastId,
            int offset,
            String type
    ) throws IOException {
        List<CompoundTag> compounds = readLittleEndianCompounds(value);
        if (compounds.size() != 1) {
            throw new IOException("Invalid " + type + " position-tracking migration record");
        }
        CompoundTag compound = compounds.get(0);
        if (remapItemReferences(compound, sourceLastId, offset)) {
            batch.put(key, writeLittleEndianCompounds(compounds));
        }
    }

    private static boolean remapItemReferences(CompoundTag tag, int sourceLastId, int offset) throws IOException {
        boolean changed = false;
        String itemName = tag.containsString("Name") ? tag.getString("Name") : tag.getString("name");

        if ("minecraft:lodestone_compass".equals(itemName) && tag.containsCompound("tag")) {
            CompoundTag itemNbt = tag.getCompound("tag");
            if (itemNbt.contains("trackingHandle") && !itemNbt.containsNumber("trackingHandle")) {
                throw new IOException("Lodestone compass has invalid trackingHandle");
            }
            if (itemNbt.containsNumber("trackingHandle")) {
                int trackingHandle = itemNbt.getInt("trackingHandle");
                int migratedHandle = remapHandle(trackingHandle, sourceLastId, offset);
                if (trackingHandle != migratedHandle) {
                    itemNbt.putInt("trackingHandle", migratedHandle);
                    changed = true;
                }
            }
        }

        for (Tag child : tag.getAllTags()) {
            changed |= remapNestedItemReferences(child, sourceLastId, offset);
        }
        return changed;
    }

    private static boolean remapNestedItemReferences(Tag tag, int sourceLastId, int offset) throws IOException {
        if (tag instanceof CompoundTag compound) {
            return remapItemReferences(compound, sourceLastId, offset);
        }
        if (tag instanceof ListTag<?> list) {
            boolean changed = false;
            for (Tag child : list.getAll()) {
                changed |= remapNestedItemReferences(child, sourceLastId, offset);
            }
            return changed;
        }
        return false;
    }

    private static int readLodestoneHandle(CompoundTag tag) throws IOException {
        boolean hasHandle = tag.contains("trackingHandle");
        boolean hasHandler = tag.contains("trackingHandler");

        if (hasHandle && !tag.containsNumber("trackingHandle")) {
            throw new IOException("Lodestone has invalid trackingHandle");
        }
        if (hasHandler && !tag.containsNumber("trackingHandler")) {
            throw new IOException("Lodestone has invalid trackingHandler");
        }

        int handle = hasHandle ? tag.getInt("trackingHandle") : 0;
        int handler = hasHandler ? tag.getInt("trackingHandler") : 0;
        if (handle < 0 || handler < 0 || handle > 0 && handler > 0 && handle != handler) {
            throw new IOException("Lodestone has invalid or conflicting position-tracking handles");
        }
        return handle > 0 ? handle : handler;
    }

    private static int remapHandle(int trackingHandle, int sourceLastId, int offset) throws IOException {
        if (trackingHandle == 0) {
            return 0;
        }
        if (trackingHandle < 0 || trackingHandle > sourceLastId) {
            throw new IOException("Position-tracking reference is outside the source allocator range: " + trackingHandle);
        }
        return addHandle(trackingHandle, offset);
    }

    private static int addHandle(int handle, int offset) throws IOException {
        long result = (long) handle + offset;
        if (result < 0 || result > Integer.MAX_VALUE) {
            throw new IOException("Position-tracking handle range exceeds the supported integer range");
        }
        return (int) result;
    }

    private static int exactBlockCoordinate(double value, int handle, String axis) throws IOException {
        if (!Double.isFinite(value) || value != Math.rint(value) || value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IOException("Position-tracking handle " + handle + " has invalid " + axis + " coordinate " + value);
        }
        return (int) value;
    }

    private static boolean isPositionKey(byte[] key) {
        byte[] prefix = ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX;
        return key.length == prefix.length + 10 && startsWith(key, prefix);
    }

    private static boolean isBlockEntityKey(byte[] key) {
        return (key.length == 9 || key.length == 13) && key[key.length - 1] == '1';
    }

    private static boolean isActorKey(byte[] key) {
        return key.length == ACTOR_PREFIX.length + Long.BYTES && startsWith(key, ACTOR_PREFIX);
    }

    private static boolean isBdsPlayerDataKey(byte[] key) {
        return Arrays.equals(key, LOCAL_PLAYER_KEY)
                || startsWith(key, PLAYER_SERVER_PREFIX)
                || startsWith(key, LEGACY_CONSOLE_PLAYER_PREFIX);
    }

    private static boolean startsWith(byte[] value, byte[] prefix) {
        if (value.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (value[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static List<CompoundTag> readLittleEndianCompounds(byte[] data) throws IOException {
        List<CompoundTag> result = new ArrayList<>();
        ByteBuf buffer = Unpooled.wrappedBuffer(data);
        try {
            while (buffer.isReadable()) {
                try (ByteBufInputStream inputStream = new ByteBufInputStream(buffer);
                     NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
                    Object tag = nbtInputStream.readTag();
                    if (!(tag instanceof NbtMap map)) {
                        throw new IOException("Invalid little-endian compound NBT while migrating position tracking");
                    }
                    result.add(CompoundTag.fromNetwork(map));
                }
            }
        } finally {
            buffer.release();
        }
        return result;
    }

    private static byte[] writeLittleEndianCompounds(List<CompoundTag> tags) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            for (CompoundTag tag : tags) {
                nbtOutputStream.writeTag(tag.toNetwork());
            }
            return outputStream.toByteArray();
        }
    }

    private static CompoundTag readBigEndianCompound(byte[] data) throws IOException {
        if (data == null) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             NBTInputStream nbtInputStream = NbtUtils.createReader(inputStream)) {
            Object tag = nbtInputStream.readTag();
            if (!(tag instanceof NbtMap map)) {
                throw new IOException("Invalid PNX extra-data while migrating position tracking");
            }
            return CompoundTag.fromNetwork(map);
        }
    }

    private static byte[] writeBigEndianCompound(CompoundTag tag) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriter(outputStream)) {
            nbtOutputStream.writeTag(tag.toNetwork());
            return outputStream.toByteArray();
        }
    }
}
