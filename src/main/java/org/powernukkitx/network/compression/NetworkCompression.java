package org.powernukkitx.network.compression;

import org.powernukkitx.Server;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.BatchCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SimpleCompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SnappyCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.ZlibCompression;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.common.util.Zlib;

/**
 * Builds the outbound compression setup for a session from {@code network-settings}.
 * <p>
 * Both login paths (single- and multi-version) used to hand the session a bare algorithm, which
 * left the connection on the protocol library's default deflate level and compressed every batch
 * no matter how small. Going through here applies the configured level and threshold instead.
 */
public final class NetworkCompression {

    /**
     * First protocol version that prefixes each batch with its compression algorithm. Below it the
     * algorithm is fixed for the whole connection, so a per-batch threshold cannot be used.
     */
    private static final int PREFIXED_COMPRESSION_PROTOCOL = 649;

    private NetworkCompression() {
    }

    /**
     * Tells the client which algorithm and threshold to use, then switches the session over to the
     * same setup. The settings packet has to go out under the previous compression, so the order of
     * the two halves matters.
     */
    public static void configure(BedrockServerSession session) {
        var settings = Server.getInstance().getSettings().networkSettings();
        boolean snappy = settings.snappy();
        PacketCompressionAlgorithm algorithm = snappy
                ? PacketCompressionAlgorithm.SNAPPY
                : PacketCompressionAlgorithm.ZLIB;

        BatchCompression compression = snappy ? new SnappyCompression() : new ZlibCompression(Zlib.RAW);
        compression.setLevel(settings.compressionLevel());

        int threshold = settings.compressionThreshold();
        if (threshold > 1 && session.getPeer().getCodec().getProtocolVersion() < PREFIXED_COMPRESSION_PROTOCOL) {
            threshold = 1;
        }

        NetworkSettingsPacket packet = new NetworkSettingsPacket();
        packet.setCompressionAlgorithm(algorithm);
        packet.setCompressionThreshold(threshold);
        session.sendPacketImmediately(packet);

        session.getPeer().setCompression(threshold > 1
                ? new ThresholdCompressionStrategy(compression, threshold)
                : new SimpleCompressionStrategy(compression));
    }
}
