package org.powernukkitx.network.process;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Encodes a packet that is the same for every player once, and sends its bytes from then on.
 * <p>
 * The login sequence sends several large packets, such as the crafting data and the item
 * palette, whose content only changes when a registry is rebuilt. Encoding them again for every
 * joining player costs the network threads more than anything else during a burst of joins. The
 * cache hands out an {@link UnknownPacket} over the encoded body instead, which the codec writes
 * as it is.
 * <p>
 * An entry is tied to a key that changes whenever the content does, compared by identity. The
 * caller must only use the cached form while nothing can change the packet on its way out, which
 * means while no plugin listens to {@link org.powernukkitx.event.server.PacketSendEvent}.
 */
@Slf4j
@ApiStatus.Internal
public final class EncodedPacketCache {
    /**
     * Prime the running fingerprint is multiplied by before each collection starts, so that the
     * boundary between two collections takes part in the fingerprint: moving an element from the
     * end of one collection to the start of the next changes it.
     */
    private static final long COLLECTION_MULTIPLIER = 1_000_003L;
    /**
     * 2<sup>64</sup> divided by the golden ratio, mixed in at each collection boundary. Its bits are
     * spread evenly, so an empty collection still changes the fingerprint instead of only
     * multiplying it.
     */
    private static final long COLLECTION_SEED = 0x9E3779B97F4A7C15L;
    /** Prime each element's identity hash is combined with, as in {@link java.util.List#hashCode()}. */
    private static final long ELEMENT_MULTIPLIER = 31L;

    private final BedrockCodec codec;
    private Object key;
    private long stamp;
    private Object dependency;
    private int packetId;
    private ByteBuf payload;

    public EncodedPacketCache(BedrockCodec codec) {
        this.codec = codec;
    }

    /**
     * Same as {@link #get(Object, long, Object, Supplier, Consumer)} with a stamp of {@code 0}.
     */
    public BedrockPacket get(Object key, @Nullable Object dependency, Supplier<? extends BedrockPacket> packet,
                             Consumer<BedrockCodecHelper> helper) {
        return this.get(key, 0L, dependency, packet, helper);
    }

    /**
     * Returns the packet for {@code key} in its encoded form, encoding it first when the key
     * changed since the last call.
     *
     * @param key        identifies the content; a different instance means different content
     * @param stamp      identifies the content further where the instance stays the same while
     *                   its content changes, such as an {@link #identityFingerprint(Iterable[])}
     * @param dependency what else the encoded form depends on, such as the item palette the
     *                   packet refers to, compared by identity as well; may be {@code null}
     * @param packet     creates the packet, only called when it has to be encoded
     * @param helper     prepares a codec helper the way a session's helper is at the point the
     *                   packet is sent, such as with the item definitions the packet refers to
     * @return a packet that is written as the cached bytes, owned by the caller, or the packet itself
     * when it could not be encoded
     */
    public synchronized BedrockPacket get(Object key, long stamp, @Nullable Object dependency,
                                          Supplier<? extends BedrockPacket> packet, Consumer<BedrockCodecHelper> helper) {
        if (this.key != key || this.stamp != stamp || this.dependency != dependency || this.payload == null) {
            final BedrockPacket created = packet.get();
            final ByteBuf encoded = this.encode(created, helper);
            if (encoded == null) {
                return created;
            }
            this.key = key;
            this.stamp = stamp;
            this.dependency = dependency;
            this.packetId = this.codec.getPacketDefinition(created.getClass()).getId();
            this.payload = encoded;
        }
        final UnknownPacket unknown = new UnknownPacket();
        unknown.setPacketId(this.packetId);
        unknown.setPayload(this.payload.retainedDuplicate());
        return unknown;
    }

    /**
     * Fingerprints which objects the collections hold and in what order. Any element added,
     * removed, reordered or replaced by another instance changes it; an element changed in place
     * does not, so it only suits collections whose elements are replaced rather than edited.
     *
     * @param collections the collections, in a fixed order
     * @return the fingerprint
     */
    public static long identityFingerprint(Iterable<?>... collections) {
        long hash = 1L;
        for (Iterable<?> collection : collections) {
            hash = hash * COLLECTION_MULTIPLIER + COLLECTION_SEED;
            for (Object element : collection) {
                hash = hash * ELEMENT_MULTIPLIER + System.identityHashCode(element);
            }
        }
        return hash;
    }

    private ByteBuf encode(BedrockPacket packet, Consumer<BedrockCodecHelper> helper) {
        final BedrockCodecHelper codecHelper = this.codec.createHelper();
        helper.accept(codecHelper);
        final ByteBuf buffer = Unpooled.buffer();
        try {
            this.codec.tryEncode(codecHelper, buffer, packet);
            return Unpooled.unreleasableBuffer(buffer.asReadOnly());
        } catch (Exception e) {
            buffer.release();
            log.warn("Could not pre-encode {}, sending it the regular way", packet.getClass().getSimpleName(), e);
            return null;
        }
    }
}
