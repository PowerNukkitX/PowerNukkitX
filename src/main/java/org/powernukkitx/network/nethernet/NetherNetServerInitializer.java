package org.powernukkitx.network.nethernet;

import io.netty.channel.Channel;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.PacketDirection;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockChannelInitializer;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.powernukkitx.network.HoldableServerSession;
import org.powernukkitx.network.NetworkConstants;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Builds the Bedrock pipeline on top of a NetherNet data channel.
 * <p>
 * Everything above the framing is shared with RakNet: the data channel payload is the same batch
 * body, minus the {@code 0xFE} frame id, so compression, batching and the packet codec are reused
 * as they are. The frame codec is dropped because NetherNet delivers one batch per message.
 * <p>
 * {@link BedrockChannelInitializer} and {@link org.cloudburstmc.protocol.bedrock.BedrockPeer} both
 * read {@link RakChannelOption#RAK_PROTOCOL_VERSION} to decide which framing rules apply. NetherNet
 * follows the rules that version introduced, so the option is set on the channel rather than
 * forking the initializer.
 *
 * @author xRookieFight
 * @since 13/09/2026
 */
public abstract class NetherNetServerInitializer extends BedrockServerInitializer {

    private final int codecVersion;
    private final boolean packetTrace;
    private final Map<BedrockPeer, BedrockServerSession> sessions = new WeakHashMap<>();

    protected NetherNetServerInitializer(int codecVersion, boolean packetTrace) {
        this.codecVersion = codecVersion;
        this.packetTrace = packetTrace;
    }

    @Override
    protected void preInitChannel(Channel channel) {
        channel.attr(PacketDirection.ATTRIBUTE).set(PacketDirection.CLIENT_BOUND);
        channel.config().setOption(RakChannelOption.RAK_PROTOCOL_VERSION, this.codecVersion);

        // NetherNet delivers one whole batch per message; wrap it for the compression and batch codecs, which only
        // handle BedrockBatchWrapper. Without it the packet codec read the batch's length prefix as a packet id.
        channel.pipeline().addLast(NetherNetFrameCodec.NAME, NetherNetFrameCodec.INSTANCE);
        channel.pipeline().addLast(CompressionCodec.NAME, new CompressionCodec(
            BedrockChannelInitializer.getCompression(PacketCompressionAlgorithm.ZLIB, this.codecVersion, true),
            false));
    }

    /**
     * A NetherNet peer asks for its main session a second time once the channel is active. A fresh session there
     * would take the packets after RequestNetworkSettings in its initial state and refuse the login, so the peer
     * keeps the session it was given.
     */
    @Override
    public BedrockServerSession createSession0(BedrockPeer peer, int subClientId) {
        if (subClientId != 0) {
            return new HoldableServerSession(peer, subClientId);
        }
        synchronized (this.sessions) {
            return this.sessions.computeIfAbsent(peer, key -> new HoldableServerSession(key, subClientId));
        }
    }

    @Override
    protected void initPacketCodec(Channel channel) {
        final BedrockPacketCodec_v3 packetCodec = new BedrockPacketCodec_v3(this.packetTrace);
        channel.pipeline().addLast(BedrockPacketCodec.NAME, packetCodec);
        // The session is set up on the data channel's thread while the first message may already be read on the event
        // loop, so the codec has to be in place before anything arrives or the client's first packet decodes as unknown.
        packetCodec.setCodec(NetworkConstants.CODEC);
    }
}
