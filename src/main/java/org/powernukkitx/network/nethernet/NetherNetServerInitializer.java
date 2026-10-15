package org.powernukkitx.network.nethernet;

import io.netty.channel.Channel;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.PacketDirection;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockChannelInitializer;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.network.NetworkConstants;

/**
 * Builds the Bedrock pipeline on top of a NetherNet data channel.
 * <p>
 * Everything above the framing is shared with RakNet: the data channel payload is the same batch
 * body, minus the {@code 0xFE} frame id, so compression, batching and the packet codec are reused
 * as they are. {@link NetherNetFrameCodec} takes the place of the RakNet frame codec, since
 * NetherNet delivers one batch per message.
 * <p>
 * {@link BedrockPeer} reads the RakNet protocol version off the channel config to pick compression,
 * but a NetherNet channel has no such option and silently ignores it. NetherNet follows the rules
 * that version introduced, so the peer is handed the version directly instead.
 *
 * @author xRookieFight
 * @since 13/09/2026
 */
public abstract class NetherNetServerInitializer extends BedrockServerInitializer {

    private final int codecVersion;
    private final boolean packetTrace;
    private final @Nullable GameOutcomeReporter outcomes;

    protected NetherNetServerInitializer(int codecVersion, boolean packetTrace) {
        this(codecVersion, packetTrace, null);
    }

    /**
     * @param outcomes reports game outcomes to the NXS provider, or null when no provider admits
     *                 players through this initializer
     */
    protected NetherNetServerInitializer(int codecVersion, boolean packetTrace, @Nullable GameOutcomeReporter outcomes) {
        this.codecVersion = codecVersion;
        this.packetTrace = packetTrace;
        this.outcomes = outcomes;
    }

    @Override
    protected void preInitChannel(Channel channel) {
        channel.attr(PacketDirection.ATTRIBUTE).set(PacketDirection.CLIENT_BOUND);

        channel.pipeline()
            .addLast(NetherNetFrameCodec.NAME, NetherNetFrameCodec.INSTANCE)
            .addLast(CompressionCodec.NAME, new CompressionCodec(
                BedrockChannelInitializer.getCompression(PacketCompressionAlgorithm.ZLIB, this.codecVersion, true),
                false));
    }

    @Override
    protected void initPacketCodec(Channel channel) {
        channel.pipeline().addLast(BedrockPacketCodec.NAME, new BedrockPacketCodec_v3(this.packetTrace));
    }

    @Override
    protected void postInitChannel(Channel channel) {
        if (this.outcomes != null) {
            channel.pipeline().addBefore(BedrockPeer.NAME, GameOutcomeReporter.HANDLER_NAME,
                this.outcomes.observer(protocol -> protocol == NetworkConstants.CODEC.getProtocolVersion()));
        }
    }

    @Override
    protected BedrockPeer createPeer(Channel channel) {
        final int rakVersion = this.codecVersion;
        return new BedrockPeer(channel, this::createSession) {
            @Override
            public int getRakVersion() {
                return rakVersion;
            }
        };
    }
}
