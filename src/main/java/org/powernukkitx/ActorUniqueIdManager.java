package org.powernukkitx;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages persistent actor unique ID allocation for server actors and players. It reserves stable player IDs from
 * stored data, allocates server-global IDs from a dedicated epoch, and validates canonical player NBT before an ID is
 * reused.
 *
 * @author Curse
 */
@Slf4j
final class ActorUniqueIdManager {

    /*
     * PNX server-global actor IDs use a reserved negative high-word range.
     *
     * WorldStartCount actor epochs start at 0xffffffff and decrease.
     * This range keeps PNX global IDs far away from normal world-generated epochs.
     *
     * 0x80000000 is reserved for Player ActorUniqueIDs.
     */
    private static final int PLAYER_EPOCH = Integer.MIN_VALUE;
    private static final int GLOBAL_EPOCH_MIN = 0x80000001;
    private static final int GLOBAL_EPOCH_MAX = 0xbfffffff;
    private static final long MAX_COUNTER = 0xffffffffL;
    private static final byte[] PLAYER_PREFIX = ServerDBStorageFormat.SERVER_DATA_PLAYER_PREFIX;
    private static final byte[] NEXT_EPOCH_KEY = ServerDBStorageFormat.SERVER_DATA_ACTOR_UNIQUE_ID_NEXT_EPOCH_KEY;

    private final Map<Long, UUID> playerUniqueIdOwners = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerUniqueIds = new ConcurrentHashMap<>();
    private final AtomicLong playerCounter = new AtomicLong(1);
    private final AtomicLong actorCounter = new AtomicLong(1);
    private final int worldStartEpoch;

    ActorUniqueIdManager(DB db) {
        loadExistingPlayers(db);
        this.worldStartEpoch = reserveEpoch(db);
    }

    long next() {
        while (true) {
            long value = this.actorCounter.getAndIncrement();

            if (value > MAX_COUNTER) {
                throw new IllegalStateException("Server-global ActorUniqueID counter exhausted for epoch " + this.worldStartEpoch);
            }

            long uniqueId = ((long) this.worldStartEpoch << 32) | (value & MAX_COUNTER);

            if (!isPlayerReserved(uniqueId)) return uniqueId;
        }
    }

    synchronized long assignPlayer(UUID uuid, CompoundTag nbt) {
        Long existing = this.playerUniqueIds.get(uuid);
        long stored = nbt.contains("UniqueID") ? nbt.getLong("UniqueID") : 0;

        if (existing != null) {
            if (stored != 0 && stored != existing) {
                throw new IllegalStateException("Player " + uuid + " has conflicting UniqueID " + stored + ", expected " + existing);
            }

            nbt.putLong("UniqueID", existing);
            return existing;
        }

        if (stored != 0) {
            reservePlayer(uuid, stored);
            return stored;
        }

        long uniqueId = allocatePlayer();
        reservePlayer(uuid, uniqueId);
        nbt.putLong("UniqueID", uniqueId);
        return uniqueId;
    }

    long requirePlayer(UUID uuid) {
        Long uniqueId = this.playerUniqueIds.get(uuid);

        if (uniqueId == null) {
            throw new IllegalStateException("Player " + uuid + " has no registered ActorUniqueID");
        }

        return uniqueId;
    }

    long requirePlayer(UUID uuid, CompoundTag nbt) {
        if (!nbt.contains("UniqueID")) {
            throw new IllegalStateException("Canonical player data " + uuid + " is missing UniqueID");
        }

        long stored = nbt.getLong("UniqueID");

        if (stored == 0) {
            throw new IllegalStateException("Canonical player data " + uuid + " has invalid UniqueID 0");
        }

        Long registered = this.playerUniqueIds.get(uuid);

        if (registered == null) {
            throw new IllegalStateException("Player " + uuid + " has no registered ActorUniqueID");
        }

        if (registered.longValue() != stored) {
            throw new IllegalStateException("Player " + uuid + " has conflicting UniqueID " + stored + ", expected " + registered);
        }

        return stored;
    }

    boolean isPlayerReserved(long uniqueId) {
        return this.playerUniqueIdOwners.containsKey(uniqueId);
    }

    Long getPlayerUniqueId(UUID uuid) {
        return this.playerUniqueIds.get(uuid);
    }

    UUID getPlayerUuid(long uniqueId) {
        return this.playerUniqueIdOwners.get(uniqueId);
    }

    private synchronized long allocatePlayer() {
        while (true) {
            long counter = this.playerCounter.getAndIncrement();

            if (counter > MAX_COUNTER) {
                throw new IllegalStateException("Player ActorUniqueID counter exhausted");
            }

            long uniqueId = ((long) PLAYER_EPOCH << 32) | (counter & MAX_COUNTER);

            if (!this.playerUniqueIdOwners.containsKey(uniqueId)) {
                return uniqueId;
            }
        }
    }

    private void reservePlayer(UUID uuid, long uniqueId) {
        UUID owner = this.playerUniqueIdOwners.putIfAbsent(uniqueId, uuid);

        if (owner != null && !owner.equals(uuid)) {
            throw new IllegalStateException("Player ActorUniqueID " + uniqueId + " is already owned by " + owner + ", cannot assign it to " + uuid);
        }

        Long existing = this.playerUniqueIds.putIfAbsent(uuid, uniqueId);

        if (existing != null && existing != uniqueId) {
            throw new IllegalStateException("Player " + uuid + " already owns ActorUniqueID " + existing + ", cannot assign " + uniqueId);
        }

        if ((int) (uniqueId >> 32) == PLAYER_EPOCH) {
            long counter = uniqueId & MAX_COUNTER;
            this.playerCounter.accumulateAndGet(counter + 1, Math::max);
        }
    }

    private void loadExistingPlayers(DB db) {
        long loaded = 0;

        try (DBIterator iterator = db.iterator()) {
            iterator.seekToFirst();

            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                byte[] key = entry.getKey();
                byte[] value = entry.getValue();

                if (!isPlayerKey(key) || value.length < 2 || value[0] != 0x1f || value[1] != (byte) 0x8b) {
                    continue;
                }

                UUID uuid = readUuid(key, PLAYER_PREFIX.length);

                CompoundTag nbt;

                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(value);
                     NBTInputStream nbtInputStream = NbtUtils.createGZIPReader(inputStream)) {
                    nbt = CompoundTag.fromNetwork((NbtMap) nbtInputStream.readTag());
                } catch (IOException | RuntimeException e) {
                    throw new IllegalStateException("Unable to inspect player data " + uuid + " while loading ActorUniqueIDs", e);
                }

                if (!nbt.contains("UniqueID")) continue;
                long uniqueId = nbt.getLong("UniqueID");
                if (uniqueId == 0) continue;

                reservePlayer(uuid, uniqueId);
                loaded++;
            }
        }

        log.debug("Loaded {} persistent player ActorUniqueIDs", loaded);
    }

    private static boolean isPlayerKey(byte[] key) {
        if (key.length != PLAYER_PREFIX.length + 16) return false;

        for (int i = 0; i < PLAYER_PREFIX.length; i++) {
            if (key[i] != PLAYER_PREFIX[i]) return false;
        }

        return true;
    }

    private static int reserveEpoch(DB db) {
        byte[] stored = db.get(NEXT_EPOCH_KEY);

        if (stored != null && stored.length != Integer.BYTES) {
            throw new IllegalStateException("Invalid persisted server-global ActorUniqueID epoch");
        }

        int epoch = stored == null ? GLOBAL_EPOCH_MAX : ByteBuffer.wrap(stored).getInt();

        if (epoch < GLOBAL_EPOCH_MIN || epoch > GLOBAL_EPOCH_MAX) {
            throw new IllegalStateException("Server-global ActorUniqueID epochs exhausted or invalid: " + epoch);
        }

        db.put(NEXT_EPOCH_KEY, ByteBuffer.allocate(Integer.BYTES).putInt(epoch - 1).array());

        return epoch;
    }

    private static UUID readUuid(byte[] bytes, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, 16);
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
