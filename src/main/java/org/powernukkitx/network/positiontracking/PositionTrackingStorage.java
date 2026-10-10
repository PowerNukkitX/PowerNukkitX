package org.powernukkitx.network.positiontracking;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.Server;
import org.powernukkitx.ServerDBStorageFormat;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.OptionalInt;

/**
 * Stores position-tracking records directly in the server-global LevelDB.
 * PNX adds only the level name required to distinguish the same Bedrock dimension across multiple server worlds.
 *
 * @author joserobjr
 * @author Curse
 */
@ParametersAreNonnullByDefault
public class PositionTrackingStorage {
    private static final byte VERSION = 1;
    private static final byte STATUS_ENABLED = 0;
    private static final byte STATUS_DISABLED = 1;
    private static final String POSITION_PREFIX = new String(
            ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX,
            StandardCharsets.UTF_8
    );
    private final DB database;

    /**
     * Creates a position-tracking storage backed by the server-global LevelDB.
     *
     * @param database server-global LevelDB
     */
    public PositionTrackingStorage(DB database) {
        this.database = Preconditions.checkNotNull(database, "database");
    }

    /**
     * Returns an enabled position-tracking record.
     */
    public @Nullable PositionTracking getPosition(int trackingHandler) throws IOException {
        return getPosition(trackingHandler, true);
    }

    /**
     * Returns a position-tracking record.
     */
    public @Nullable PositionTracking getPosition(int trackingHandler, boolean onlyEnabled) throws IOException {
        Preconditions.checkArgument(trackingHandler > 0, "Tracking handler must be positive");
        StoredPosition stored = readPosition(trackingHandler);
        if (stored == null || onlyEnabled && stored.status() != STATUS_ENABLED) {
            return null;
        }
        return stored.position();
    }

    /**
     * Reuses an enabled record for the position or creates a new one.
     */
    public synchronized OptionalInt addOrReusePosition(NamedPosition position) throws IOException {
        OptionalInt handler = findTrackingHandler(position);
        return handler.isPresent() ? handler : addNewPosition(position);
    }

    /**
     * Creates a new enabled position-tracking record.
     */
    public synchronized OptionalInt addNewPosition(NamedPosition position) throws IOException {
        return addNewPosition(position, true);
    }

    /**
     * Creates a new position-tracking record.
     */
    public synchronized OptionalInt addNewPosition(NamedPosition position, boolean enabled) throws IOException {
        int lastId = readLastId();
        Preconditions.checkState(lastId < Integer.MAX_VALUE, "Position tracking ID space is exhausted");
        int trackingHandler = lastId + 1;
        int dimensionId = resolveDimension(position);
        StoredPosition stored = new StoredPosition(new PositionTracking(position), dimensionId, enabled ? STATUS_ENABLED : STATUS_DISABLED);

        try (WriteBatch batch = database.createWriteBatch()) {
            batch.put(positionKey(trackingHandler), writePosition(trackingHandler, stored));
            batch.put(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_LAST_ID_KEY, writeLastId(trackingHandler));
            database.write(batch);
        }
        return OptionalInt.of(trackingHandler);
    }

    /**
     * Finds one enabled tracking handle for a position.
     */
    public @NotNull OptionalInt findTrackingHandler(NamedPosition position) throws IOException {
        IntList handlers = findTrackingHandlers(position, true, 1);
        return handlers.size() == 0 ? OptionalInt.empty() : OptionalInt.of(handlers.getInt(0));
    }

    /**
     * Permanently invalidates a tracking handle.
     */
    public synchronized void invalidateHandler(int trackingHandler) {
        Preconditions.checkArgument(trackingHandler > 0, "Tracking handler must be positive");
        database.delete(positionKey(trackingHandler));
    }

    /**
     * Returns whether a tracking handle is enabled.
     */
    public synchronized boolean isEnabled(int trackingHandler) throws IOException {
        Preconditions.checkArgument(trackingHandler > 0, "Tracking handler must be positive");
        StoredPosition stored = readPosition(trackingHandler);
        return stored != null && stored.status() == STATUS_ENABLED;
    }

    /**
     * Changes the enabled state of a persisted tracking handle.
     */
    public synchronized boolean setEnabled(int trackingHandler, boolean enabled) throws IOException {
        Preconditions.checkArgument(trackingHandler > 0, "Tracking handler must be positive");
        StoredPosition stored = readPosition(trackingHandler);
        if (stored == null) {
            return false;
        }

        byte status = enabled ? STATUS_ENABLED : STATUS_DISABLED;
        if (stored.status() == status) {
            return false;
        }

        database.put(
                positionKey(trackingHandler),
                writePosition(trackingHandler, new StoredPosition(stored.position(), stored.dimensionId(), status))
        );
        return true;
    }

    /**
     * Returns whether an enabled record exists for a tracking handle.
     */
    public synchronized boolean hasPosition(int trackingHandler) throws IOException {
        return hasPosition(trackingHandler, true);
    }

    /**
     * Returns whether a persisted record exists for a tracking handle.
     */
    public synchronized boolean hasPosition(int trackingHandler, boolean onlyEnabled) throws IOException {
        Preconditions.checkArgument(trackingHandler > 0, "Tracking handler must be positive");
        StoredPosition stored = readPosition(trackingHandler);
        return stored != null && (!onlyEnabled || stored.status() == STATUS_ENABLED);
    }

    /**
     * Finds enabled tracking handles for a position.
     */
    public @NotNull IntList findTrackingHandlers(NamedPosition position) throws IOException {
        return findTrackingHandlers(position, true);
    }

    /**
     * Finds tracking handles for a position.
     */
    public @NotNull IntList findTrackingHandlers(NamedPosition position, boolean onlyEnabled) throws IOException {
        return findTrackingHandlers(position, onlyEnabled, Integer.MAX_VALUE);
    }

    /**
     * Finds tracking handles for a position up to the requested limit.
     */
    public synchronized @NotNull IntList findTrackingHandlers(NamedPosition position, boolean onlyEnabled, int limit) throws IOException {
        Preconditions.checkArgument(limit > 0, "Limit must be positive");
        IntList result = new IntArrayList();

        try (DBIterator iterator = database.iterator()) {
            iterator.seek(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX);
            while (iterator.hasNext() && result.size() < limit) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (!isPositionKey(entry.getKey())) {
                    break;
                }

                int trackingHandler = readHandle(entry.getKey());
                StoredPosition stored = readPosition(trackingHandler, entry.getValue());
                if (stored == null || onlyEnabled && stored.status() != STATUS_ENABLED) {
                    continue;
                }
                if (stored.position().matchesNamedPosition(position)) {
                    result.add(trackingHandler);
                }
            }
        }
        return result;
    }

    private int resolveDimension(NamedPosition position) throws IOException {
        if (position instanceof Position levelPosition && levelPosition.getLevel() != null) {
            return levelPosition.getLevel().getDimension();
        }

        Level level = Server.getInstance().getLevelByName(position.getLevelName());
        if (level == null) {
            throw new IOException("Unknown level for position tracking: " + position.getLevelName());
        }
        return level.getDimension();
    }

    private StoredPosition readPosition(int trackingHandler) throws IOException {
        byte[] value = database.get(positionKey(trackingHandler));
        return value == null ? null : readPosition(trackingHandler, value);
    }

    private StoredPosition readPosition(int trackingHandler, byte[] value) throws IOException {
        if (value.length == 0) {
            throw new IOException("Empty position-tracking record for handle " + trackingHandler);
        }

        CompoundTag root = readCompound(value);
        if (!root.containsByte("version") || root.getByte("version") != VERSION) {
            throw new IOException("Unsupported position-tracking version for handle " + trackingHandler);
        }
        if (!root.containsString("id") || !formatId(trackingHandler).equals(root.getString("id"))) {
            throw new IOException("Position-tracking id mismatch for handle " + trackingHandler);
        }
        if (!root.containsInt("dim") || !root.containsByte("status") || !root.containsString("level")) {
            throw new IOException("Incomplete position-tracking record for handle " + trackingHandler);
        }
        if (!root.containsList("pos", Tag.TAG_Int)) {
            throw new IOException("Invalid position list for handle " + trackingHandler);
        }

        ListTag<IntTag> pos = root.getList("pos", IntTag.class);
        if (pos.size() != 3) {
            throw new IOException("Invalid position list size for handle " + trackingHandler + ": " + pos.size());
        }

        byte status = root.getByte("status");
        if (status != STATUS_ENABLED && status != STATUS_DISABLED) {
            throw new IOException("Invalid position-tracking status for handle " + trackingHandler + ": " + status);
        }

        String levelName = root.getString("level");
        if (levelName.isEmpty()) {
            throw new IOException("Position-tracking record has no level for handle " + trackingHandler);
        }

        return new StoredPosition(
                new PositionTracking(levelName, pos.get(0).data, pos.get(1).data, pos.get(2).data),
                root.getInt("dim"),
                status
        );
    }

    private int readLastId() throws IOException {
        byte[] value = database.get(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_LAST_ID_KEY);
        if (value == null) {
            return 0;
        }
        if (value.length == 0) {
            throw new IOException("Empty position-tracking allocator record");
        }

        CompoundTag root = readCompound(value);
        if (!root.containsByte("version") || root.getByte("version") != VERSION || !root.containsString("id")) {
            throw new IOException("Invalid position-tracking allocator record");
        }
        return parseId(root.getString("id"));
    }

    private static byte[] writePosition(int trackingHandler, StoredPosition stored) throws IOException {
        PositionTracking position = stored.position();
        CompoundTag root = new CompoundTag()
                .putInt("dim", stored.dimensionId())
                .putString("id", formatId(trackingHandler))
                .putList("pos", new ListTag<IntTag>(Tag.TAG_Int)
                        .add(new IntTag(position.getFloorX()))
                        .add(new IntTag(position.getFloorY()))
                        .add(new IntTag(position.getFloorZ())))
                .putByte("status", stored.status())
                .putByte("version", VERSION)
                .putString("level", position.getLevelName());
        return writeCompound(root);
    }

    private static byte[] writeLastId(int lastId) throws IOException {
        return writeCompound(new CompoundTag().putString("id", formatId(lastId)).putByte("version", VERSION));
    }

    private static CompoundTag readCompound(byte[] value) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(value);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            Object tag = nbtInputStream.readTag();
            if (!(tag instanceof NbtMap map)) {
                throw new IOException("Position-tracking value is not a compound");
            }
            return CompoundTag.fromNetwork(map);
        }
    }

    private static byte[] writeCompound(CompoundTag root) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(root.toNetwork());
            return outputStream.toByteArray();
        }
    }

    private static byte[] positionKey(int trackingHandler) {
        return (POSITION_PREFIX + formatId(trackingHandler)).getBytes(StandardCharsets.UTF_8);
    }

    private static boolean isPositionKey(byte[] key) {
        byte[] prefix = ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX;
        if (key.length != prefix.length + 10) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (key[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static int readHandle(byte[] key) throws IOException {
        int handle = parseId(new String(
                key,
                ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX.length,
                10,
                StandardCharsets.UTF_8
        ));
        if (handle <= 0) {
            throw new IOException("Invalid position-tracking key handle: " + handle);
        }
        return handle;
    }

    private static int parseId(String value) throws IOException {
        if (value.length() != 10 || !value.startsWith("0x")) {
            throw new IOException("Invalid position-tracking id: " + value);
        }
        try {
            long id = Long.parseLong(value.substring(2), 16);
            if (id < 0 || id > Integer.MAX_VALUE) {
                throw new IOException("Position-tracking id is outside the supported range: " + value);
            }
            return (int) id;
        } catch (NumberFormatException e) {
            throw new IOException("Invalid position-tracking id: " + value, e);
        }
    }

    private static String formatId(int trackingHandler) {
        return String.format("0x%08x", trackingHandler);
    }

    private record StoredPosition(PositionTracking position, int dimensionId, byte status) {
    }
}
