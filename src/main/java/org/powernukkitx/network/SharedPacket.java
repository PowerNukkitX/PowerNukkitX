package org.powernukkitx.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCounted;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.event.server.PacketSendEvent;

/**
 * A packet sent to several players that is encoded only once.
 * <p>
 * Every session encodes the packets it sends on its own network thread, so a packet broadcast to
 * fifty viewers is otherwise encoded fifty times. The first session to write this packet encodes
 * it, and the others write the same bytes, see {@link SharedPacketEncoder}. The tick that sends
 * the packet does no encoding of its own.
 * <p>
 * Only meant for packets that no longer change once sent and that encode the same for every
 * session, which holds for every session past the spawn sequence.
 */
@ApiStatus.Internal
public final class SharedPacket implements BedrockPacket {
    private final BedrockPacket packet;
    private BedrockCodec codec;
    private int packetId;
    private volatile ByteBuf encoded;
    private volatile boolean failed;

    private SharedPacket(BedrockPacket packet) {
        this.packet = packet;
    }

    /**
     * Wraps {@code packet} for a broadcast when sharing its encoding pays off and nothing can tell
     * the difference: it goes to at least two players, no plugin listens to
     * {@link PacketSendEvent}, and it does not own buffers of its own.
     *
     * @param packet     the packet to broadcast
     * @param recipients how many players it is sent to
     * @return the packet to hand to each player, which may be {@code packet} itself
     */
    public static BedrockPacket forBroadcast(BedrockPacket packet, int recipients) {
        if (recipients < 2 || packet instanceof SharedPacket || packet instanceof ReferenceCounted
            || !PacketSendEvent.getHandlers().isEmpty()) {
            return packet;
        }
        return new SharedPacket(packet);
    }

    public BedrockPacket getPacket() {
        return this.packet;
    }

    /**
     * The packet encoded with its header for {@code wrapper}, encoded by the first caller with its
     * session's codec helper and shared with all later ones. Only wrappers without sub-client ids
     * share, since those are part of the header; they are the only ones a server session writes.
     *
     * @return a duplicate the caller owns, which never frees the shared bytes, or {@code null} when
     * the caller has to encode the packet itself: it could not be encoded, the wrapper carries
     * sub-client ids, or another codec encoded it first
     */
    @Nullable
    ByteBuf encoded(BedrockPacketCodec packetCodec, BedrockPacketWrapper wrapper) {
        if (wrapper.getSenderSubClientId() != 0 || wrapper.getTargetSubClientId() != 0) {
            return null;
        }
        ByteBuf shared = this.encoded;
        if (shared == null) {
            if (this.failed) {
                return null;
            }
            synchronized (this) {
                shared = this.encoded;
                if (shared == null) {
                    shared = this.encode(packetCodec, wrapper);
                    if (shared == null) {
                        this.failed = true;
                        return null;
                    }
                    this.encoded = shared;
                }
            }
        }
        if (this.codec != packetCodec.getCodec()) {
            return null;
        }
        wrapper.setPacketId(this.packetId);
        return shared.duplicate();
    }

    private ByteBuf encode(BedrockPacketCodec packetCodec, BedrockPacketWrapper wrapper) {
        final BedrockCodec bedrockCodec = packetCodec.getCodec();
        final ByteBuf buffer = Unpooled.buffer();
        try {
            final int id = bedrockCodec.getPacketDefinition(this.packet.getClass()).getId();
            wrapper.setPacketId(id);
            packetCodec.encodeHeader(buffer, wrapper);
            bedrockCodec.tryEncode(packetCodec.getHelper(), buffer, this.packet);
            this.codec = bedrockCodec;
            this.packetId = id;
            return Unpooled.unreleasableBuffer(buffer.asReadOnly());
        } catch (Exception e) {
            buffer.release();
            return null;
        }
    }

    @Override
    public PacketSignal handle(BedrockPacketHandler handler) {
        return this.packet.handle(handler);
    }

    @Override
    public BedrockPacketType getPacketType() {
        return this.packet.getPacketType();
    }

    @Override
    public BedrockPacket clone() {
        return new SharedPacket(this.packet.clone());
    }

    @Override
    public String toString() {
        return this.packet.toString();
    }
}
