package cn.nukkit.utils.collection;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public final class LZ4Freezer {
    public static final LZ4Factory $1 = LZ4Factory.fastestInstance();
    public static final LZ4Compressor $2 = factory.fastCompressor();
    public static final LZ4Compressor $3 = factory.highCompressor();
    public static final LZ4FastDecompressor $4 = factory.fastDecompressor();
}
