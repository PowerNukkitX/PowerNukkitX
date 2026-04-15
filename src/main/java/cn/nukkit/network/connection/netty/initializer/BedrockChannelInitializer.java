package cn.nukkit.network.connection.netty.initializer;

import cn.nukkit.network.compression.CompressionProvider;
import cn.nukkit.network.connection.netty.codec.compression.CompressionCodec;
import cn.nukkit.network.connection.netty.codec.compression.CompressionStrategy;
import cn.nukkit.network.connection.netty.codec.compression.NoopCompression;
import cn.nukkit.network.connection.netty.codec.compression.SimpleCompressionStrategy;
import cn.nukkit.network.connection.netty.codec.compression.SnappyCompression;
import cn.nukkit.network.connection.netty.codec.compression.ZlibCompression;
import cn.nukkit.network.protocol.types.CompressionAlgorithm;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import lombok.extern.slf4j.Slf4j;
import cn.nukkit.network.connection.BedrockPeer;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.connection.netty.codec.FrameIdCodec;
import cn.nukkit.network.connection.netty.codec.batch.BedrockBatchDecoder;
import cn.nukkit.network.connection.netty.codec.batch.BedrockBatchEncoder;
import cn.nukkit.network.connection.netty.codec.packet.BedrockPacketCodec;
import cn.nukkit.network.connection.netty.codec.packet.BedrockPacketCodec_v3;

@Slf4j
public abstract class BedrockChannelInitializer<T extends BedrockSession> extends ChannelInitializer<Channel> {
    public static final int RAKNET_MINECRAFT_ID = 0xFE;
    private static final FrameIdCodec RAKNET_FRAME_CODEC = new FrameIdCodec(RAKNET_MINECRAFT_ID);
    private static final BedrockBatchDecoder BATCH_DECODER = new BedrockBatchDecoder();

    private static final CompressionStrategy ZLIB_RAW_STRATEGY = new SimpleCompressionStrategy(new ZlibCompression(CompressionProvider.ZLIB_RAW));
    private static final CompressionStrategy ZLIB_STRATEGY = new SimpleCompressionStrategy(new ZlibCompression(CompressionProvider.ZLIB));
    private static final CompressionStrategy SNAPPY_STRATEGY = new SimpleCompressionStrategy(new SnappyCompression());
    private static final CompressionStrategy NOOP_STRATEGY = new SimpleCompressionStrategy(new NoopCompression());

    @Override
    protected final void initChannel(Channel channel) {
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
        channel.pipeline().addLast(FrameIdCodec.NAME, RAKNET_FRAME_CODEC);

        CompressionStrategy compression = getCompression(PacketCompressionAlgorithm.ZLIB, true);
        // At this point all connections use not prefixed compression
        channel.pipeline().addLast(CompressionCodec.NAME, new CompressionCodec(compression, false));
    }

    public static CompressionStrategy getCompression(CompressionAlgorithm algorithm,boolean initial) {
        return initial ? NOOP_STRATEGY : getCompression(algorithm);
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

    protected void postInitChannel(Channel channel) {
    }

    protected void initPacketCodec(Channel channel) {
        channel.pipeline().addLast(BedrockPacketCodec.NAME, new BedrockPacketCodec_v3());
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
