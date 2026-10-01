package org.powernukkitx.network.nethernet;

import io.netty.channel.Channel;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.PacketDirection;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockChannelInitializer;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;

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

    protected NetherNetServerInitializer(int codecVersion, boolean packetTrace) {
        this.codecVersion = codecVersion;
        this.packetTrace = packetTrace;
    }

    @Override
    protected void preInitChannel(Channel channel) {
        channel.attr(PacketDirection.ATTRIBUTE).set(PacketDirection.CLIENT_BOUND);
        channel.config().setOption(RakChannelOption.RAK_PROTOCOL_VERSION, this.codecVersion);

        channel.pipeline().addLast(CompressionCodec.NAME, new CompressionCodec(
            BedrockChannelInitializer.getCompression(PacketCompressionAlgorithm.ZLIB, this.codecVersion, true),
            false));
    }

    @Override
    protected void initPacketCodec(Channel channel) {
        channel.pipeline().addLast(BedrockPacketCodec.NAME, new BedrockPacketCodec_v3(this.packetTrace));
    }
}
