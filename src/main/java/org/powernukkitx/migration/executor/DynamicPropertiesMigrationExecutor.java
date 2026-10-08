package org.powernukkitx.migration.executor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.DynamicProperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Normalizes persisted Dynamic Properties namespaces to the server-global PNX namespace during storage migration.
 *
 * @author Curse
 */
@Slf4j
public final class DynamicPropertiesMigrationExecutor {
    private static final byte[] DYNAMIC_PROPERTIES_KEY = LevelDBKeyUtil.getGlobalKey(DynamicProperties.ROOT);
    private static final byte[] ACTOR_PREFIX = "actorprefix".getBytes(StandardCharsets.UTF_8);
    private static final byte[] PLAYER_SERVER_PREFIX = LevelDBKeyUtil.PLAYER_SERVER_PREFIX.getBytes(StandardCharsets.UTF_8);
    private static final byte[] OVERWORLD_DATA_KEY = "Overworld".getBytes(StandardCharsets.UTF_8);
    private static final byte[] NETHER_DATA_KEY = "Nether".getBytes(StandardCharsets.UTF_8);
    private static final byte[] THE_END_DATA_KEY = "TheEnd".getBytes(StandardCharsets.UTF_8);

    private DynamicPropertiesMigrationExecutor() {
    }

    /**
     * Rehomes persisted world, actor, block-entity, item and BDS player Dynamic Properties to the supplied namespace.
     */
    public static void normalizeWorld(LevelDBStorage storage, String namespace) throws IOException {
        DB db = storage.getDb();
        int changed = 0;

        try (DBIterator iterator = db.iterator();
             WriteBatch batch = storage.createBatch()) {
            iterator.seekToFirst();

            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                byte[] key = entry.getKey();
                byte[] value = entry.getValue();

                if (value.length == 0) {
                    continue;
                }

                if (Arrays.equals(key, DYNAMIC_PROPERTIES_KEY)) {
                    CompoundTag root = readSingleCompound(value, "world DynamicProperties");

                    if (normalizeRoot(root, namespace)) {
                        batch.put(key, writeLittleEndianCompounds(List.of(root)));
                        changed++;
                    }

                    continue;
                }

                boolean actor = isActorKey(key);
                boolean blockEntities = isBlockEntityKey(key);
                boolean player = startsWith(key, PLAYER_SERVER_PREFIX);
                boolean dimensionGlobal = isDimensionGlobalKey(key);

                if (!actor && !blockEntities && !player && !dimensionGlobal) {
                    continue;
                }

                List<CompoundTag> compounds = readLittleEndianCompounds(value);

                if (!blockEntities && compounds.size() != 1) {
                    throw new IOException("Invalid Dynamic Properties migration record for key " + printableKey(key));
                }

                boolean recordChanged = false;

                for (CompoundTag compound : compounds) {
                    recordChanged |= normalizeNested(compound, namespace);
                }

                if (recordChanged) {
                    batch.put(key, writeLittleEndianCompounds(compounds));
                    changed++;
                }
            }

            if (changed != 0) {
                storage.writeBatch(batch);
            }
        }

        if (changed != 0) {
            log.info("[DynamicProperties Migration] Normalized {} LevelDB records to namespace {}", changed, namespace);
        }
    }

    static void normalizeNbt(CompoundTag root, String namespace) throws IOException {
        normalizeNested(root, namespace);
    }

    private static boolean normalizeNested(Tag tag, String namespace) throws IOException {
        if (tag instanceof CompoundTag compound) {
            boolean changed = false;

            if (compound.contains(DynamicProperties.ROOT)) {
                if (!compound.containsCompound(DynamicProperties.ROOT)) {
                    throw new IOException("DynamicProperties tag is not a compound");
                }

                changed |= normalizeRoot(compound.getCompound(DynamicProperties.ROOT), namespace);
            }

            for (Map.Entry<String, Tag> entry : compound.getTags().entrySet()) {
                if (DynamicProperties.ROOT.equals(entry.getKey())) {
                    continue;
                }

                changed |= normalizeNested(entry.getValue(), namespace);
            }

            return changed;
        }

        if (tag instanceof ListTag<?> list) {
            boolean changed = false;

            for (Tag child : list.getAll()) {
                changed |= normalizeNested(child, namespace);
            }

            return changed;
        }

        return false;
    }

    private static boolean normalizeRoot(CompoundTag root, String namespace) throws IOException {
        if (root.getTags().size() == 0) {
            return false;
        }

        List<String> groups = new ArrayList<>(root.getTags().keySet());

        for (String group : groups) {
            if (!root.containsCompound(group)) {
                throw new IOException("Dynamic Properties group '" + group + "' is not a compound");
            }
        }

        if (groups.size() == 1 && namespace.equals(groups.get(0))) {
            return false;
        }

        CompoundTag target = root.containsCompound(namespace)
                ? root.getCompound(namespace)
                : new CompoundTag();

        boolean changed = false;

        for (String group : groups) {
            if (namespace.equals(group)) {
                continue;
            }

            CompoundTag source = root.getCompound(group);

            for (Map.Entry<String, Tag> property : source.getTags().entrySet()) {
                Tag existing = target.get(property.getKey());

                if (existing != null && !existing.equals(property.getValue())) {
                    throw new IOException(
                            "Conflicting Dynamic Property '" + property.getKey() + "' while merging namespace '" + group + "' into '" + namespace + "'"
                    );
                }

                if (existing == null) {
                    target.put(property.getKey(), property.getValue().copy());
                }
            }

            root.remove(group);
            changed = true;
        }

        if (changed) {
            root.putCompound(namespace, target);
        }

        return changed;
    }

    private static CompoundTag readSingleCompound(byte[] value, String type) throws IOException {
        List<CompoundTag> compounds = readLittleEndianCompounds(value);

        if (compounds.size() != 1) {
            throw new IOException("Invalid " + type + " record");
        }

        return compounds.get(0);
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
                        throw new IOException("Invalid little-endian compound NBT while migrating Dynamic Properties");
                    }

                    result.add(CompoundTag.fromNetwork(map));
                }
            }
        } finally {
            buffer.release();
        }

        return result;
    }

    private static byte[] writeLittleEndianCompounds(List<CompoundTag> compounds) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            for (CompoundTag compound : compounds) {
                nbtOutputStream.writeTag(compound.toNetwork());
            }

            return outputStream.toByteArray();
        }
    }

    private static boolean isActorKey(byte[] key) {
        return key.length == ACTOR_PREFIX.length + Long.BYTES && startsWith(key, ACTOR_PREFIX);
    }

    private static boolean isBlockEntityKey(byte[] key) {
        return (key.length == 9 || key.length == 13) && key[key.length - 1] == '1';
    }

    private static boolean isDimensionGlobalKey(byte[] key) {
        return Arrays.equals(key, OVERWORLD_DATA_KEY)
                || Arrays.equals(key, NETHER_DATA_KEY)
                || Arrays.equals(key, THE_END_DATA_KEY);
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

    private static String printableKey(byte[] key) {
        if (startsWith(key, ACTOR_PREFIX)) {
            return "actorprefix";
        }

        if (startsWith(key, PLAYER_SERVER_PREFIX)) {
            return LevelDBKeyUtil.PLAYER_SERVER_PREFIX;
        }

        if (isDimensionGlobalKey(key)) {
            return new String(key, StandardCharsets.UTF_8);
        }

        return "block entities";
    }
}
