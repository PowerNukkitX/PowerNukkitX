package org.powernukkitx.utils.collection;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public final class LZ4Freezer {
    public static final LZ4Factory factory = LZ4Factory.fastestInstance();
    public static final LZ4Compressor compressor = factory.fastCompressor();
    /**
     * Deep freeze runs on the compute pool for every cold array in a cycle. The high compressor costs
     * roughly 72x the fast one on nibble-array shaped data (158.8 us vs 2.2 us per 2 KiB array,
     * measured with JMH) to gain a few percent on data that is already highly repetitive.
     * <p>
     * Deep freeze therefore uses the same fast compressor as {@link #compressor}. Both produce the
     * LZ4 block format that {@link #decompressor} reads, so arrays frozen either way stay
     * interchangeable.
     */
    public static final LZ4Compressor deepCompressor = compressor;
    public static final LZ4FastDecompressor decompressor = factory.fastDecompressor();
}
