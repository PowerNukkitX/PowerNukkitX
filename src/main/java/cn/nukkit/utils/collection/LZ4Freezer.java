package cn.nukkit.utils.collection;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public final class LZ4Freezer {
    public static final LZ4Factory factory = LZ4Factory.fastestInstance();
    public static final LZ4Compressor compressor = factory.fastCompressor();
    public static final LZ4Compressor deepCompressor = factory.highCompressor();
    public static final LZ4FastDecompressor decompressor = factory.fastDecompressor();
}
