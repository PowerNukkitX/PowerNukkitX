package cn.nukkit.compression;

import com.nukkitx.natives.util.Natives;
import com.nukkitx.natives.zlib.Deflater;
import com.nukkitx.natives.zlib.Inflater;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.zip.DataFormatException;

public class NativeByteBufZlib {
    public static final NativeByteBufZlib DEFAULT = new NativeByteBufZlib(false);
    public static final NativeByteBufZlib RAW = new NativeByteBufZlib(true);

    private static final int CHUNK = 8192;

    private final FastThreadLocal<Inflater> inflaterLocal;
    private final FastThreadLocal<Deflater> deflaterLocal;

    private NativeByteBufZlib(boolean raw) {
        // Required for Android API versions prior to 26.
        this.inflaterLocal = new FastThreadLocal<Inflater>() {
            @Override
            public Inflater initialValue() {
                return Natives.ZLIB.get().create(raw);
            }
        };
        this.deflaterLocal = new FastThreadLocal<Deflater>() {
            @Override
            protected Deflater initialValue() {
                return Natives.ZLIB.get().create(7, raw);
            }
        };
    }

    public ByteBuf inflate(ByteBuf buffer, int maxSize) throws DataFormatException {
        ByteBuf source = null;
        ByteBuf decompressed = ByteBufAllocator.DEFAULT.ioBuffer();

        try {
            if (!buffer.isDirect() || (buffer instanceof CompositeByteBuf && ((CompositeByteBuf) buffer).numComponents() > 1)) {
                // We don't have a direct buffer. Create one.
                ByteBuf temporary = ByteBufAllocator.DEFAULT.ioBuffer();
                temporary.writeBytes(buffer);
                source = temporary;
            } else {
                source = buffer;
            }

            Inflater inflater = inflaterLocal.get();
            inflater.reset();
            inflater.setInput(source.internalNioBuffer(source.readerIndex(), source.readableBytes()));
            inflater.finished();

            while (!inflater.finished()) {
                decompressed.ensureWritable(CHUNK);
                int index = decompressed.writerIndex();
                int written = inflater.inflate(decompressed.internalNioBuffer(index, CHUNK));
                if (written < 1) {
                    break;
                }
                decompressed.writerIndex(index + written);
                if (maxSize > 0 && decompressed.writerIndex() >= maxSize) {
                    throw new DataFormatException("Inflated data exceeds maximum size");
                }
            }
            return decompressed;
        } catch (DataFormatException e) {
            decompressed.release();
            throw e;
        } finally {
            if (source != null && source != buffer) {
                source.release();
            }
        }
    }

    public void deflate(ByteBuf uncompressed, ByteBuf compressed, int level) throws DataFormatException {
        ByteBuf destination = null;
        ByteBuf source = null;
        try {
            if (!uncompressed.isDirect() || (uncompressed instanceof CompositeByteBuf && ((CompositeByteBuf) uncompressed).numComponents() > 1)) {
                // Source is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                source = ByteBufAllocator.DEFAULT.ioBuffer();
                source.writeBytes(uncompressed);
            } else {
                source = uncompressed;
            }

            if (!compressed.isDirect()) {
                // Destination is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                destination = ByteBufAllocator.DEFAULT.ioBuffer();
            } else {
                destination = compressed;
            }

            Deflater deflater = deflaterLocal.get();
            deflater.reset();
            deflater.setLevel(level);
            deflater.setInput(source.internalNioBuffer(source.readerIndex(), source.readableBytes()));

            while (!deflater.finished()) {
                int index = destination.writerIndex();
                destination.ensureWritable(CHUNK);
                int written = deflater.deflate(destination.internalNioBuffer(index, CHUNK));
                destination.writerIndex(index + written);
            }

            if (destination != compressed) {
                compressed.writeBytes(destination);
            }
        } finally {
            if (source != null && source != uncompressed) {
                source.release();
            }
            if (destination != null && destination != compressed) {
                destination.release();
            }
        }
    }
}