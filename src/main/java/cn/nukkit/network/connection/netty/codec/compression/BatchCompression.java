package cn.nukkit.network.connection.netty.codec.compression;

import cn.nukkit.network.protocol.types.CompressionAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface BatchCompression {
    ByteBuf encode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception;
    ByteBuf decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception;

    CompressionAlgorithm getAlgorithm();

    void setLevel(int level);

    int getLevel();
}
