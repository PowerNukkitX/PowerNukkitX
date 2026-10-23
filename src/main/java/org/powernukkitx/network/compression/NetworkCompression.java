package org.powernukkitx.network.compression;

import org.powernukkitx.Server;
import org.powernukkitx.math.NukkitMath;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.BatchCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SimpleCompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SnappyCompression;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.common.util.Zlib;

/**
 * Builds the outbound compression setup for a session from {@code network-settings}.
 * <p>
 * The login handler used to hand the session a bare algorithm, which left the connection on the
 * protocol library's default deflate level and compressed every batch no matter how small. Going
 * through here applies the configured level and threshold instead.
 */
public final class NetworkCompression {

    /**
     * First protocol version that prefixes each batch with its compression algorithm. Below it the
     * algorithm is fixed for the whole connection, so a per-batch threshold cannot be used.
     */
    private static final int PREFIXED_COMPRESSION_PROTOCOL = 649;

    /**
     * The {@code zlibProvider} value documented as the hardware-accelerated algorithm.
     */
    private static final int HARDWARE_ACCELERATED_PROVIDER = 3;

    private NetworkCompression() {
    }

    /**
     * The zlib compression {@code network-settings.zlibProvider} selects: libdeflate for
     * {@value #HARDWARE_ACCELERATED_PROVIDER} when it is available on this platform, zlib otherwise.
     */
    static BatchCompression newZlibCompression() {
        final int provider = Server.getInstance().getSettings().networkSettings().zlibProvider();
        return provider == HARDWARE_ACCELERATED_PROVIDER && LibdeflateZlibCompression.isAvailable()
                ? new LibdeflateZlibCompression(Zlib.RAW)
                : new SizedZlibCompression(Zlib.RAW);
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

        BatchCompression compression = snappy ? new SnappyCompression() : newZlibCompression();
        compression.setLevel(NukkitMath.clamp(settings.compressionLevel(), 0, 9));

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
