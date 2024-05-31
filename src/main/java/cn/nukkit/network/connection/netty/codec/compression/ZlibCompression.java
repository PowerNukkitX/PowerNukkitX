package cn.nukkit.network.connection.netty.codec.compression;

import cn.nukkit.compression.CompressionProvider;
import cn.nukkit.network.protocol.types.CompressionAlgorithm;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ZlibCompression implements BatchCompression {
    private final CompressionProvider zlib;

    @Getter @Setter
    private int $1 = 7;
    /**
     * @deprecated 
     */
    

    public ZlibCompression(CompressionProvider zlib) {
        this.zlib = zlib;
    }

    @Override
    public ByteBuf encode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        ByteBuf $2 = ctx.alloc().ioBuffer(msg.readableBytes());
        try {
            byte[] bytes = Utils.convertByteBuf2Array(msg);
            byte[] compress = zlib.compress(bytes, level);
            outBuf.writeBytes(compress);
            return outBuf.retain();
        } finally {
            outBuf.release();
        }
    }

    @Override
    public ByteBuf decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        ByteBuf $3 = ctx.alloc().ioBuffer(msg.readableBytes() << 3);
        try {
            byte[] bytes = Utils.convertByteBuf2Array(msg);
            byte[] decompress = zlib.decompress(bytes);
            outBuf.writeBytes(decompress);
            return outBuf.retain();
        } finally {
            outBuf.release();
        }
    }

    @Override
    public CompressionAlgorithm getAlgorithm() {
        return PacketCompressionAlgorithm.ZLIB;
    }
}
