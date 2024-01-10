package cn.nukkit.network.connection.netty.codec.compression;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.ThreadCache;
import cn.nukkit.utils.Zlib;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibCompressionCodec extends MessageToMessageCodec<ByteBuf, ByteBuf> implements CompressionCodec {
    public static boolean libDeflateAvailable;
    public static final String NAME = "compression-codec";
    private static final ThreadLocal<Inflater> INFLATER_RAW = ThreadLocal.withInitial(() -> new Inflater(true));
    private static final ThreadLocal<Deflater> DEFLATER_RAW = ThreadLocal.withInitial(() -> new Deflater(7, true));
    private int level = 7;
    private final boolean isRawZlib;

    public ZlibCompressionCodec(boolean isRawZlib) {
        this.isRawZlib = isRawZlib;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf outBuf = ctx.alloc().ioBuffer(msg.readableBytes() << 3);
        try {
            final byte[] data = new byte[msg.readableBytes()];
            msg.readBytes(data);
            byte[] deflate;
            if (isRawZlib) {
                deflate = deflateRaw(data, level);
            } else {
                deflate = Zlib.deflate(data, level);
            }
            outBuf.writeBytes(deflate);
            out.add(outBuf.retain());
        } finally {
            msg.release();
            outBuf.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf outBuf = ctx.alloc().ioBuffer();
        try {
            final byte[] data = new byte[msg.readableBytes()];
            byte[] inflate;
            if (isRawZlib) {
                inflate = inflateRaw(data);
            } else {
                inflate = Zlib.inflate(data, MAX_DECOMPRESSED_BYTES);
            }
            outBuf.writeBytes(inflate);
            out.add(outBuf.retain());
        } finally {
            msg.release();
            outBuf.release();
        }
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


    public static byte[] inflateRaw(byte[] data) throws IOException, DataFormatException {
        if (libDeflateAvailable) {
            return Zlib.inflate(data, MAX_DECOMPRESSED_BYTES);
        }
        Inflater inflater = INFLATER_RAW.get();
        try {
            inflater.setInput(data);
            inflater.finished();

            FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
            bos.reset();
            byte[] buf = BUFFER.get();
            while (!inflater.finished()) {
                int i = inflater.inflate(buf);
                if (i == 0) {
                    throw new IOException("Could not decompress the data. Needs input: " + inflater.needsInput() + ", Needs Dictionary: " + inflater.needsDictionary());
                }
                bos.write(buf, 0, i);
            }
            return bos.toByteArray();
        } finally {
            inflater.reset();
        }
    }

    public static byte[] deflateRaw(byte[] data, int level) throws IOException {
        if (libDeflateAvailable) {
            return Zlib.deflate(data, level);
        }
        Deflater deflater = DEFLATER_RAW.get();
        try {
            deflater.setLevel(level);
            deflater.setInput(data);
            deflater.finish();
            FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
            bos.reset();
            byte[] buffer = BUFFER.get();
            while (!deflater.finished()) {
                int i = deflater.deflate(buffer);
                bos.write(buffer, 0, i);
            }

            return bos.toByteArray();
        } finally {
            deflater.reset();
        }
    }

    public static byte[] deflateRaw(byte[][] datas, int level) throws IOException {
        if (libDeflateAvailable) {
            return Zlib.deflate(datas, level);
        }
        Deflater deflater = DEFLATER_RAW.get();
        try {
            deflater.setLevel(level);
            FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
            bos.reset();
            byte[] buffer = BUFFER.get();

            for (byte[] data : datas) {
                deflater.setInput(data);
                while (!deflater.needsInput()) {
                    int i = deflater.deflate(buffer);
                    bos.write(buffer, 0, i);
                }
            }
            deflater.finish();
            while (!deflater.finished()) {
                int i = deflater.deflate(buffer);
                bos.write(buffer, 0, i);
            }
            return bos.toByteArray();
        } finally {
            deflater.reset();
        }
    }
}
