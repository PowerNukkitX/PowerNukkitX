package cn.nukkit.network.connection.netty.codec.compression;

import cn.nukkit.Server;
import cn.nukkit.network.connection.netty.BedrockBatchWrapper;
import cn.nukkit.network.protocol.types.CompressionAlgorithm;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class CompressionCodec extends MessageToMessageCodec<BedrockBatchWrapper, BedrockBatchWrapper> {
    public static final String NAME = "compression-codec";

    private final CompressionStrategy strategy;
    private final boolean prefixed;

    public CompressionCodec(CompressionStrategy strategy, boolean prefixed) {
        this.strategy = strategy;
        this.prefixed = prefixed;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) throws Exception {
        if (msg.getCompressed() == null && msg.getUncompressed() == null) {
            throw new IllegalStateException("Batch was not encoded before");
        }

        if (msg.getCompressed() != null && !msg.isModified()) {
            this.onPassedThrough(ctx, msg);
            out.add(msg.retain());
            return;
        }

        BatchCompression compression = this.strategy.getCompression(msg);
        if (!this.prefixed && this.strategy.getDefaultCompression().getAlgorithm() != compression.getAlgorithm()) {
            throw new IllegalStateException("Non-default compression algorithm used without prefixing");
        }

        ByteBuf compressed = compression.encode(ctx, msg.getUncompressed());
        try {
            ByteBuf outBuf;
            if (this.prefixed) {
                // Do not use a composite buffer as encryption does not like it
                outBuf = ctx.alloc().ioBuffer(1 + compressed.readableBytes());
                outBuf.writeByte(this.getCompressionHeader(compression.getAlgorithm()));
                outBuf.writeBytes(compressed);
            } else {
                outBuf = compressed.retain();
            }

            msg.setCompressed(outBuf, compression.getAlgorithm());
        } finally {
            compressed.release();
        }

        this.onCompressed(ctx, msg);
        out.add(msg.retain());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) throws Exception {
        ByteBuf compressed = msg.getCompressed().slice();
        Preconditions.checkArgument(compressed.capacity() <= Server.getInstance().getSettings().networkSettings().maxDecompressSize(), "Compressed data size is too big: %s", compressed.capacity());

        BatchCompression compression;
        if (this.prefixed) {
            CompressionAlgorithm algorithm = this.getCompressionAlgorithm(compressed.readByte());
            compression = this.strategy.getCompression(algorithm);
        } else {
            compression = this.strategy.getDefaultCompression();
        }

        msg.setAlgorithm(compression.getAlgorithm());

        msg.setUncompressed(compression.decode(ctx, compressed.slice()));
        this.onDecompressed(ctx, msg);
        out.add(msg.retain());
    }

    protected void onPassedThrough(ChannelHandlerContext ctx, BedrockBatchWrapper msg) {
    }

    protected void onCompressed(ChannelHandlerContext ctx, BedrockBatchWrapper msg) {
    }

    protected void onDecompressed(ChannelHandlerContext ctx, BedrockBatchWrapper msg) {
    }

    protected final byte getCompressionHeader(CompressionAlgorithm algorithm) {
        if (algorithm.equals(PacketCompressionAlgorithm.NONE)) {
            return (byte) 0xff;
        } else if (algorithm.equals(PacketCompressionAlgorithm.ZLIB)) {
            return 0x00;
        } else if (algorithm.equals(PacketCompressionAlgorithm.SNAPPY)) {
            return 0x01;
        }

        byte header = this.getCompressionHeader0(algorithm);
        if (header == -1) {
            throw new IllegalArgumentException("Unknown compression algorithm " + algorithm);
        }
        return header;
    }

    protected final CompressionAlgorithm getCompressionAlgorithm(byte header) {
        switch (header) {
            case 0x00:
                return PacketCompressionAlgorithm.ZLIB;
            case 0x01:
                return PacketCompressionAlgorithm.SNAPPY;
            case (byte) 0xff:
                return PacketCompressionAlgorithm.NONE;
        }

        CompressionAlgorithm algorithm = this.getCompressionAlgorithm0(header);
        if (algorithm == null) {
            throw new IllegalArgumentException("Unknown compression algorithm " + header);
        }
        return algorithm;
    }

    protected byte getCompressionHeader0(CompressionAlgorithm algorithm) {
        return -1;
    }

    protected CompressionAlgorithm getCompressionAlgorithm0(byte header) {
        return null;
    }

    public CompressionStrategy getStrategy() {
        return this.strategy;
    }

    @Override
    public String toString() {
        return strategy.toString();
    }
}
