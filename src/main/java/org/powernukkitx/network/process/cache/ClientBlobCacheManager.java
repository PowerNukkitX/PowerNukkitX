package org.powernukkitx.network.process.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.buffer.ByteBuf;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.powernukkitx.network.RakNetNetworkMetrics;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages Bedrock client blob-cache state per session. It remembers encoded blobs, advertises hashes, tracks
 * acknowledgements, and batches transfers with a concurrency limit.
 *
 * @author Curse
 */
public final class ClientBlobCacheManager {
    private static final Cache<Long, byte[]> BLOBS =
            Caffeine.<Long, byte[]>newBuilder()
                    .maximumWeight(256L * 1024L * 1024L)
                    .weigher((Long id, byte[] data) -> data.length)
                    .expireAfterAccess(Duration.ofMinutes(10))
                    .build();
    private static final Map<BedrockServerSession, SessionState> SESSIONS = new ConcurrentHashMap<>();
    private static final long PRIME64_1 = -7046029288634856825L;
    private static final long PRIME64_2 = -4417276706812531889L;
    private static final long PRIME64_3 = 1609587929392839161L;
    private static final long PRIME64_4 = -8796714831421723037L;
    private static final long PRIME64_5 = 2870177450012600261L;
    private ClientBlobCacheManager() {}

    /**
     * Sets whether the feature is enabled.
     *
     * @param session value for this API
     * @param enabled value for this API
     */
    public static void setEnabled(BedrockServerSession session, boolean enabled) {
        if (enabled) {
            SESSIONS.computeIfAbsent(session, key -> new SessionState());
        } else {
            SESSIONS.remove(session);
        }
    }

    /**
     * Returns whether the feature is enabled.
     *
     * @param session value for this API
     * @return the requested value
     */
    public static boolean isEnabled(BedrockServerSession session) {
        return SESSIONS.containsKey(session);
    }

    /**
     * Removes all data for a session.
     *
     * @param session value for this API
     */
    public static void removeSession(BedrockServerSession session) {
        SESSIONS.remove(session);
    }

    /**
     * Remembers a blob and returns a strong handle that remains valid independently from the evictable global cache.
     */
    public static Blob rememberBlob(ByteBuf buffer) {
        final byte[] data = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), data);
        final long id = xxHash64(data);
        BLOBS.put(id, data);
        return new Blob(id, data);
    }

    /**
     * Remembers a buffer and returns its ID.
     *
     * @param buffer value for this API
     * @return the requested value
     */
    public static long remember(ByteBuf buffer) {
        return rememberBlob(buffer).id;
    }

    /**
     * Remembers a buffer and returns its ID.
     *
     * @param session value for this API
     * @param buffer value for this API
     * @return the requested value
     */
    public static long remember(BedrockServerSession session, ByteBuf buffer) {
        final Blob blob = rememberBlob(buffer);
        final SessionState state = SESSIONS.get(session);
        if (state != null) state.pending.put(blob.id, blob.data);
        return blob.id;
    }

    /**
     * Advertises a blob ID to the session.
     *
     * @param session value for this API
     * @param id value for this API
     */
    public static void advertise(BedrockServerSession session, long id) {
        final SessionState state = SESSIONS.get(session);
        if (state == null) return;

        final byte[] data = BLOBS.getIfPresent(id);
        if (data != null) state.pending.put(id, data);
    }

    /**
     * Returns a stored value.
     *
     * @param session value for this API
     * @param id value for this API
     * @return the requested value
     */
    public static byte[] get(BedrockServerSession session, long id) {
        final SessionState state = SESSIONS.get(session);
        if (state != null) {
            final byte[] pendingBlob = state.pending.get(id);
            if (pendingBlob != null) return pendingBlob;
        }

        return BLOBS.getIfPresent(id);
    }

    /**
     * Marks a blob as acknowledged.
     *
     * @param session value for this API
     * @param id value for this API
     */
    public static void acknowledge(BedrockServerSession session, long id) {
        final SessionState state = SESSIONS.get(session);
        if (state == null) {
            return;
        }

        state.pending.remove(id);
        synchronized (state) {
            state.activeTransfers.removeIf(
                    transfer -> {
                        transfer.waitingIds.remove(id);
                        return transfer.waitingIds.isEmpty();
                    });
        }
    }

    /**
     * Starts a transfer if allowed.
     *
     * @param session value for this API
     * @return the requested value
     */
    public static TransferBuilder tryStartTransfer(BedrockServerSession session) {
        final SessionState state = SESSIONS.get(session);
        if (state == null) return null;

        synchronized (state) {
            final int maximumTransfers = getMaximumConcurrentTransfers(session);
            if (state.activeTransfers.size() > maximumTransfers) return null;
            return new TransferBuilder(session, state);
        }
    }

    /**
     * Starts a transfer.
     *
     * @param session value for this API
     * @return the requested value
     */
    public static TransferBuilder startTransfer(BedrockServerSession session) {
        final SessionState state = SESSIONS.get(session);
        if (state == null) return null;
        return new TransferBuilder(session, state);
    }

    /**
     * Returns the active transfer count.
     *
     * @param session value for this API
     * @return the requested value
     */
    public static int getActiveTransferCount(BedrockServerSession session) {
        final SessionState state = SESSIONS.get(session);
        if (state == null) return 0;

        synchronized (state) {
            return state.activeTransfers.size();
        }
    }

    /**
     * Returns the maximum concurrent transfer count.
     *
     * @param session value for this API
     * @return the requested value
     */
    public static int getMaximumConcurrentTransfers(BedrockServerSession session) {
        final var metrics = session.getPeer().getChannel().config().getOption(RakChannelOption.RAK_METRICS);
        if (!(metrics instanceof RakNetNetworkMetrics networkMetrics)) return 20;

        return switch (networkMetrics.getNetworkLoad()) {
            case UNRESTRICTED -> 200;
            case LOW -> 100;
            case MEDIUM -> 40;
            case HIGH -> 20;
        };
    }

    /**
     * Creates a transfer completion batch.
     *
     * @param transfer value for this API
     * @param expectedCompletions value for this API
     * @return the requested value
     */
    public static TransferBatch createTransferBatch(TransferBuilder transfer, int expectedCompletions) {
        if (transfer == null) return null;
        return new TransferBatch(transfer, expectedCompletions);
    }

    /**
     * Strong handle to a client cache blob retained independently from the evictable global cache.
     */
    public static final class Blob {
        private final long id;
        private final byte[] data;

        private Blob(long id, byte[] data) {
            this.id = id;
            this.data = data;
        }

        /**
         * Returns the blob identifier sent to the client.
         */
        public long id() {
            return id;
        }
    }

    /**
     * Public constant used by this API.
     */
    public static final class TransferBatch {
        private final TransferBuilder transfer;
        private int remaining;
        private boolean finished;
        private TransferBatch(TransferBuilder transfer, int remaining) {
            if (remaining <= 0) {
                throw new IllegalArgumentException("Transfer batch must contain at least one completion");
            }

            this.transfer = transfer;
            this.remaining = remaining;
        }

        /**
         * Returns the associated transfer.
         * @return the requested value
         */
        public TransferBuilder getTransfer() {
            return transfer;
        }

        /**
         * Marks one transfer completion.
         */
        public synchronized void complete() {
            if (finished) return;
            if (--remaining > 0) return;

            finished = true;
            transfer.close();
        }
    }

    /**
     * Public constant used by this API.
     */
    public static final class TransferBuilder implements AutoCloseable {
        private final BedrockServerSession session;
        private final SessionState state;
        private final Map<Long, byte[]> blobs = new HashMap<>();
        private boolean finished;
        private TransferBuilder(BedrockServerSession session, SessionState state) {
            this.session = session;
            this.state = state;
        }

        /**
         * Remembers a buffer and returns its ID.
         *
         * @param buffer value for this API
         * @return the requested value
         */
        public long remember(ByteBuf buffer) {
            final Blob blob = ClientBlobCacheManager.rememberBlob(buffer);
            add(blob);
            return blob.id;
        }

        /**
         * Adds a value.
         *
         * @param id value for this API
         */
        public void add(long id) {
            if (finished) {
                throw new IllegalStateException("Cache transfer is already finished");
            }

            final byte[] data = BLOBS.getIfPresent(id);
            if (data == null) {
                throw new IllegalStateException("Unknown cache blob " + Long.toUnsignedString(id));
            }

            blobs.put(id, data);
        }

        /**
         * Adds a strongly retained blob to this transfer.
         */
        public void add(Blob blob) {
            if (finished) {
                throw new IllegalStateException("Cache transfer is already finished");
            }

            blobs.put(blob.id, blob.data);
        }

        /**
         * Returns whether this object is empty.
         * @return the requested value
         */
        public boolean isEmpty() {
            return blobs.isEmpty();
        }

        /**
         * Commits pending values.
         */
        public void commit() {
            synchronized (state) {
                if (finished) return;

                if (SESSIONS.get(session) == state && !blobs.isEmpty()) {
                    state.pending.putAll(blobs);
                    state.activeTransfers.add(new ActiveTransfer(blobs.keySet()));
                }

                finished = true;
            }
        }

        @Override
        public void close() {
            commit();
        }
    }

    private static final class SessionState {
        private final ConcurrentHashMap<Long, byte[]> pending = new ConcurrentHashMap<>();
        private final List<ActiveTransfer> activeTransfers = new ArrayList<>();
    }

    private static final class ActiveTransfer {
        private final Set<Long> waitingIds;
        private ActiveTransfer(Set<Long> ids) {
            this.waitingIds = new HashSet<>(ids);
        }
    }

    private static long xxHash64(byte[] data) {
        int index = 0;
        final int length = data.length;
        long hash;
        if (length >= 32) {
            long v1 = PRIME64_1 + PRIME64_2;
            long v2 = PRIME64_2;
            long v3 = 0;
            long v4 = -PRIME64_1;
            final int limit = length - 32;
            while (index <= limit) {
                v1 = round(v1, readLongLE(data, index));
                index += 8;
                v2 = round(v2, readLongLE(data, index));
                index += 8;
                v3 = round(v3, readLongLE(data, index));
                index += 8;
                v4 = round(v4, readLongLE(data, index));
                index += 8;
            }

            hash = Long.rotateLeft(v1, 1)
                        + Long.rotateLeft(v2, 7)
                        + Long.rotateLeft(v3, 12)
                        + Long.rotateLeft(v4, 18);
            hash = mergeRound(hash, v1);
            hash = mergeRound(hash, v2);
            hash = mergeRound(hash, v3);
            hash = mergeRound(hash, v4);
        } else {
            hash = PRIME64_5;
        }

        hash += length;
        while (index <= length - 8) {
            final long k1 = round(0, readLongLE(data, index));
            hash ^= k1;
            hash = Long.rotateLeft(hash, 27) * PRIME64_1 + PRIME64_4;
            index += 8;
        }

        if (index <= length - 4) {
            hash ^= (readIntLE(data, index) & 0xffffffffL) * PRIME64_1;
            hash = Long.rotateLeft(hash, 23) * PRIME64_2 + PRIME64_3;
            index += 4;
        }

        while (index < length) {
            hash ^= (data[index] & 0xffL) * PRIME64_5;
            hash = Long.rotateLeft(hash, 11) * PRIME64_1;
            index++;
        }

        hash ^= hash >>> 33;
        hash *= PRIME64_2;
        hash ^= hash >>> 29;
        hash *= PRIME64_3;
        hash ^= hash >>> 32;
        return hash;
    }

    private static long round(long accumulator, long input) {
        accumulator += input * PRIME64_2;
        accumulator = Long.rotateLeft(accumulator, 31);
        accumulator *= PRIME64_1;
        return accumulator;
    }

    private static long mergeRound(long accumulator, long value) {
        accumulator ^= round(0, value);
        accumulator = accumulator * PRIME64_1 + PRIME64_4;
        return accumulator;
    }

    private static long readLongLE(byte[] data, int index) {
        return (data[index] & 0xffL)
                | ((data[index + 1] & 0xffL) << 8)
                | ((data[index + 2] & 0xffL) << 16)
                | ((data[index + 3] & 0xffL) << 24)
                | ((data[index + 4] & 0xffL) << 32)
                | ((data[index + 5] & 0xffL) << 40)
                | ((data[index + 6] & 0xffL) << 48)
                | ((data[index + 7] & 0xffL) << 56);
    }

    private static int readIntLE(byte[] data, int index) {
        return (data[index] & 0xff)
                | ((data[index + 1] & 0xff) << 8)
                | ((data[index + 2] & 0xff) << 16)
                | ((data[index + 3] & 0xff) << 24);
    }
}
