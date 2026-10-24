package org.powernukkitx.network.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.BatchCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.ZlibCompression;
import org.cloudburstmc.protocol.common.util.Zlib;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.zip.Inflater;

class NetworkCompressionTest {

    private static ChannelHandlerContext context(EmbeddedChannel channel) {
        channel.pipeline().addLast("ctx", new ChannelInboundHandlerAdapter());
        return channel.pipeline().context("ctx");
    }

    private static byte[] input(int size, boolean compressible) {
        byte[] data = new byte[size];
        Random random = new Random(size);
        for (int i = 0; i < size; i++) {
            data[i] = (byte) (compressible ? (i / 64) % 7 : random.nextInt());
        }
        return data;
    }

    private static byte[] encode(BatchCompression compression, ChannelHandlerContext ctx, byte[] data) throws Exception {
        ByteBuf in = ctx.alloc().directBuffer(data.length).writeBytes(data);
        ByteBuf out = compression.encode(ctx, in);
        try {
            return ByteBufUtil.getBytes(out);
        } finally {
            out.release();
            in.release();
        }
    }

    private static byte[] inflate(byte[] compressed, int expectedLength) throws Exception {
        Inflater inflater = new Inflater(true);
        inflater.setInput(compressed);
        byte[] out = new byte[expectedLength + 1];
        int length = inflater.inflate(out);
        Assertions.assertTrue(inflater.finished());
        inflater.end();
        byte[] result = new byte[length];
        System.arraycopy(out, 0, result, 0, length);
        return result;
    }

    @Test
    void sizedZlibWritesTheSameBytesAsTheProtocolLibrary() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelHandlerContext ctx = context(channel);
        ZlibCompression reference = new ZlibCompression(Zlib.RAW);
        SizedZlibCompression sized = new SizedZlibCompression(Zlib.RAW);
        for (int level : new int[]{0, 4, 9}) {
            reference.setLevel(level);
            sized.setLevel(level);
            for (int size : new int[]{0, 1, 300, 8191, 8192, 70_000, 400_000}) {
                for (boolean compressible : new boolean[]{true, false}) {
                    byte[] data = input(size, compressible);
                    Assertions.assertArrayEquals(encode(reference, ctx, data), encode(sized, ctx, data),
                        "level " + level + ", " + size + " bytes");
                }
            }
        }
        channel.finishAndReleaseAll();
    }

    @Test
    void deflateBoundCoversIncompressibleInput() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelHandlerContext ctx = context(channel);
        SizedZlibCompression sized = new SizedZlibCompression(Zlib.RAW);
        sized.setLevel(9);
        for (int size : new int[]{1, 1000, 65_536, 1_000_000}) {
            byte[] compressed = encode(sized, ctx, input(size, false));
            Assertions.assertTrue(compressed.length <= SizedZlibCompression.deflateBound(size),
                size + " bytes compressed to " + compressed.length);
        }
        channel.finishAndReleaseAll();
    }

    @Test
    void measuredCompressionReportsTheRatioOncePerWindow() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelHandlerContext ctx = context(channel);
        MeasuredCompression measured = new MeasuredCompression(new SizedZlibCompression(Zlib.RAW));
        measured.setLevel(7);
        MeasuredCompression.sampleRatio();

        byte[] compressible = input(64_000, true);
        byte[] compressed = encode(measured, ctx, compressible);
        Assertions.assertArrayEquals(compressible, inflate(compressed, compressible.length));

        double ratio = MeasuredCompression.sampleRatio();
        Assertions.assertEquals((double) compressed.length / compressible.length, ratio, 1e-9);
        Assertions.assertTrue(ratio < 0.1, "ratio " + ratio);
        Assertions.assertTrue(MeasuredCompression.sampleRatio() < 0, "the window starts over after a sample");
        channel.finishAndReleaseAll();
    }

    @Test
    void libdeflateOutputInflatesToTheInput() throws Exception {
        Assumptions.assumeTrue(LibdeflateZlibCompression.isAvailable(), "libdeflate is not available on this platform");
        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelHandlerContext ctx = context(channel);
        LibdeflateZlibCompression compression = new LibdeflateZlibCompression(Zlib.RAW);
        compression.setLevel(4);
        for (int size : new int[]{0, 1, 300, 70_000, 400_000}) {
            for (boolean compressible : new boolean[]{true, false}) {
                byte[] data = input(size, compressible);
                Assertions.assertArrayEquals(data, inflate(encode(compression, ctx, data), size), size + " bytes");
            }
        }
        channel.finishAndReleaseAll();
    }

    @Test
    void libdeflateAcceptsHeapAndCompositeInput() throws Exception {
        Assumptions.assumeTrue(LibdeflateZlibCompression.isAvailable(), "libdeflate is not available on this platform");
        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelHandlerContext ctx = context(channel);
        LibdeflateZlibCompression compression = new LibdeflateZlibCompression(Zlib.RAW);
        byte[] data = input(5000, true);
        ByteBuf composite = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(data, 0, 2000), Unpooled.wrappedBuffer(data, 2000, 3000));
        ByteBuf out = compression.encode(ctx, composite);
        try {
            Assertions.assertArrayEquals(data, inflate(ByteBufUtil.getBytes(out), data.length));
        } finally {
            out.release();
            composite.release();
            channel.finishAndReleaseAll();
        }
    }
}
