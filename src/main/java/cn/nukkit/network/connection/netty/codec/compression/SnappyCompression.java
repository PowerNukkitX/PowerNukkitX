package cn.nukkit.network.connection.netty.codec.compression;

import cn.nukkit.compression.CompressionProvider;
import cn.nukkit.network.protocol.types.CompressionAlgorithm;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.ToString;

@ToString
public class SnappyCompression implements BatchCompression {

    @Override
    public ByteBuf encode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int readableBytes = msg.readableBytes();
        ByteBuf output = ctx.alloc().ioBuffer(readableBytes);
        try {
            byte[] data = new byte[readableBytes];
            msg.readBytes(data);
            byte[] compress = CompressionProvider.SNAPPY.compress(data, 7);
            output.writeBytes(compress);
            return output.retain();
        } finally {
            output.release();
        }
    }

    @Override
    public ByteBuf decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int readableBytes = msg.readableBytes();
        ByteBuf output = ctx.alloc().ioBuffer(readableBytes);
        try {
            byte[] data = new byte[readableBytes];
            msg.readBytes(data);
            byte[] compress = CompressionProvider.SNAPPY.decompress(data);
            output.writeBytes(compress);
            return output.retain();
        } finally {
            output.release();
        }
    }

    @Override
    public CompressionAlgorithm getAlgorithm() {
        return PacketCompressionAlgorithm.SNAPPY;
    }

    @Override
    public void setLevel(int level) {
    }

    @Override
    public int getLevel() {
        return -1;
    }
}
