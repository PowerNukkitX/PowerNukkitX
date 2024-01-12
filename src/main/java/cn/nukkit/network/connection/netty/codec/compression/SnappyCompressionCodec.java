package cn.nukkit.network.connection.netty.codec.compression;

import cn.nukkit.compression.CompressionProvider;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class SnappyCompressionCodec extends MessageToMessageCodec<ByteBuf, ByteBuf> implements CompressionCodec {
    public static final String NAME = "compression-codec";

    private static final ThreadLocal<short[]> TABLE = ThreadLocal.withInitial(() -> new short[16384]);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf output = ctx.alloc().ioBuffer();
        try {
            byte[] data = new byte[msg.readableBytes()];
            msg.readBytes(data);
            byte[] compress = CompressionProvider.SNAPPY.compress(data,7);
            output.writeBytes(compress);
            out.add(output.retain());
        } finally {
            msg.release();
            output.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf output = ctx.alloc().ioBuffer();
        try {
            byte[] data = new byte[msg.readableBytes()];
            msg.readBytes(data);
            byte[] compress = CompressionProvider.SNAPPY.decompress(data);
            output.writeBytes(compress);
            out.add(output.retain());
        } finally {
            msg.release();
            output.release();
        }
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public void setLevel(int level) {
    }

    @Override
    public PacketCompressionAlgorithm getAlgorithm() {
        return PacketCompressionAlgorithm.SNAPPY;
    }
}
