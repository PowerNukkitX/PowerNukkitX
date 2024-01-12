package cn.nukkit.network.connection.netty.codec.compression;

import cn.nukkit.compression.NativeByteBufZlib;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.ThreadCache;
import cn.nukkit.compression.ZlibChooser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibCompressionCodec extends MessageToMessageCodec<ByteBuf, ByteBuf> implements CompressionCodec {
    public static boolean libDeflateAvailable;
    public static final String NAME = "compression-codec";
    private static final ThreadLocal<Inflater> INFLATER_RAW = ThreadLocal.withInitial(() -> new Inflater(true));
    private static final ThreadLocal<Deflater> DEFLATER_RAW = ThreadLocal.withInitial(() -> new Deflater(7, true));
    private final NativeByteBufZlib zlib;
    private int level = 7;

    public ZlibCompressionCodec(NativeByteBufZlib zlib) {
        this.zlib = zlib;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf outBuf = ctx.alloc().ioBuffer(msg.readableBytes() << 3);
        try {
            zlib.deflate(msg, outBuf, level);
            out.add(outBuf.retain());
        } finally {
            outBuf.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add(zlib.inflate(msg, MAX_DECOMPRESSED_BYTES));
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public PacketCompressionAlgorithm getAlgorithm() {
        return PacketCompressionAlgorithm.ZLIB;
    }
}
