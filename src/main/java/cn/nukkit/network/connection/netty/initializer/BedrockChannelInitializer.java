package cn.nukkit.network.connection.netty.initializer;

import org.cloudburstmc.protocol.bedrock.data.CompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import cn.nukkit.network.connection.BedrockPeer;
import cn.nukkit.network.connection.BedrockSession;
import org.cloudburstmc.protocol.bedrock.netty.codec.FrameIdCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.batch.BedrockBatchDecoder;
import org.cloudburstmc.protocol.bedrock.netty.codec.batch.BedrockBatchEncoder;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.NoopCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SimpleCompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SnappyCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.ZlibCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v1;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v2;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.PacketDirection;
import org.cloudburstmc.protocol.common.util.Zlib;

@Slf4j
public abstract class BedrockChannelInitializer<T extends BedrockSession> extends ChannelInitializer<Channel> {
    public static final int RAKNET_MINECRAFT_ID = 0xFE;
    private static final FrameIdCodec RAKNET_FRAME_CODEC = new FrameIdCodec(RAKNET_MINECRAFT_ID);
    private static final BedrockBatchDecoder BATCH_DECODER = new BedrockBatchDecoder();

    private static final CompressionStrategy ZLIB_RAW_STRATEGY = new SimpleCompressionStrategy(new ZlibCompression(Zlib.RAW));
    private static final CompressionStrategy ZLIB_STRATEGY = new SimpleCompressionStrategy(new ZlibCompression(Zlib.DEFAULT));
    private static final CompressionStrategy SNAPPY_STRATEGY = new SimpleCompressionStrategy(new SnappyCompression());
    private static final CompressionStrategy NOOP_STRATEGY = new SimpleCompressionStrategy(new NoopCompression());

    @Override
    protected final void initChannel(Channel channel) throws Exception {
        // Decode
        // RAKNET_FRAME_CODEC -> CompressionCodec -> BATCH_DECODER ->  BedrockPacketCodec -> BedrockPeer
        // Encode
        // BedrockPeer -> BedrockPacketCodec-> BATCH_ENCODER -> CompressionCodec -> RAKNET_FRAME_CODEC
        this.preInitChannel(channel);

        channel.pipeline()
                .addLast(BedrockBatchDecoder.NAME, BATCH_DECODER)
                .addLast(BedrockBatchEncoder.NAME, new BedrockBatchEncoder());

        this.initPacketCodec(channel);

        channel.pipeline().addLast(BedrockPeer.NAME, this.createPeer(channel));

        this.postInitChannel(channel);
    }

    protected void preInitChannel(Channel channel) {
        channel.attr(PacketDirection.ATTRIBUTE).set(PacketDirection.CLIENT_BOUND);
        channel.pipeline().addLast(FrameIdCodec.NAME, RAKNET_FRAME_CODEC);

        int rakVersion = channel.config().getOption(RakChannelOption.RAK_PROTOCOL_VERSION);

        CompressionStrategy compression = getCompression(PacketCompressionAlgorithm.ZLIB, rakVersion, true);
        // At this point all connections use not prefixed compression
        channel.pipeline().addLast(CompressionCodec.NAME, new CompressionCodec(compression, false));
    }

    public static CompressionStrategy getCompression(CompressionAlgorithm algorithm, int rakVersion, boolean initial) {
        return switch (rakVersion) {
            case 7, 8, 9 -> ZLIB_STRATEGY;
            case 10 -> ZLIB_RAW_STRATEGY;
            case 11 -> initial ? NOOP_STRATEGY : getCompression(algorithm);
            default -> throw new UnsupportedOperationException("Unsupported RakNet protocol version: " + rakVersion);
        };
    }

    private static CompressionStrategy getCompression(CompressionAlgorithm algorithm) {
        return switch (algorithm) {
            case PacketCompressionAlgorithm.ZLIB -> ZLIB_RAW_STRATEGY;
            case PacketCompressionAlgorithm.SNAPPY -> SNAPPY_STRATEGY;
            case PacketCompressionAlgorithm.NONE -> NOOP_STRATEGY;
            default ->
                    throw new UnsupportedOperationException("Unsupported compression algorithm: " + algorithm);
        };
    }

    protected void postInitChannel(Channel channel) throws Exception {
    }

    protected void initPacketCodec(Channel channel) throws Exception {
        int rakVersion = channel.config().getOption(RakChannelOption.RAK_PROTOCOL_VERSION);

        switch (rakVersion) {
            case 11:
            case 10:
            case 9: // Merged & Varint-ified
                channel.pipeline().addLast(BedrockPacketCodec.NAME, new BedrockPacketCodec_v3());
                break;
            case 8: // Split-screen support
                channel.pipeline().addLast(BedrockPacketCodec.NAME, new BedrockPacketCodec_v2());
                break;
            case 7: // Single byte packet ID
                channel.pipeline().addLast(BedrockPacketCodec.NAME, new BedrockPacketCodec_v1());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported RakNet protocol version: " + rakVersion);
        }
    }

    protected BedrockPeer createPeer(Channel channel) {
        return new BedrockPeer(channel, this::createSession);
    }

    protected final T createSession(BedrockPeer peer, int subClientId) {
        T session = this.createSession0(peer, subClientId);
        this.initSession(session);
        return session;
    }

    protected abstract T createSession0(BedrockPeer peer, int subClientId);

    protected abstract void initSession(T session);
}
