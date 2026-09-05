package org.powernukkitx.level.format.anvil;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * @author iYozem
 */
@Slf4j
public final class RegionReader implements AutoCloseable {

    private static final int SECTOR_BYTES = 4096;
    private static final int ENTRIES = 1024; // 32 * 32

    private static final int COMPRESSION_GZIP = 1;
    private static final int COMPRESSION_ZLIB = 2;
    private static final int COMPRESSION_NONE = 3;

    private final RandomAccessFile file;
    private final int[] locations = new int[ENTRIES];

    public RegionReader(java.io.File path) throws IOException {
        this.file = new RandomAccessFile(path, "r");
        if (this.file.length() < SECTOR_BYTES) {
            return;
        }
        byte[] header = new byte[SECTOR_BYTES];
        this.file.seek(0);
        this.file.readFully(header);
        for (int i = 0; i < ENTRIES; i++) {
            int b = i * 4;
            this.locations[i] = ((header[b] & 0xFF) << 16) | ((header[b + 1] & 0xFF) << 8) | (header[b + 2] & 0xFF);
        }
    }

    public boolean hasChunk(int localX, int localZ) {
        return this.locations[index(localX, localZ)] != 0;
    }

    public @Nullable NbtMap readChunk(int localX, int localZ) throws IOException {
        int sectorOffset = this.locations[index(localX, localZ)];
        if (sectorOffset == 0) {
            return null;
        }

        this.file.seek((long) sectorOffset * SECTOR_BYTES);
        int length = this.file.readInt();
        if (length <= 0) {
            return null;
        }
        int compressionType = this.file.readUnsignedByte();
        byte[] payload = new byte[length - 1];
        this.file.readFully(payload);

        try (NBTInputStream in = openNbt(compressionType, payload)) {
            return (NbtMap) in.readTag();
        }
    }

    private static NBTInputStream openNbt(int compressionType, byte[] payload) throws IOException {
        ByteArrayInputStream raw = new ByteArrayInputStream(payload);
        return switch (compressionType) {
            case COMPRESSION_GZIP -> NbtUtils.createReader(new GZIPInputStream(raw));
            case COMPRESSION_ZLIB -> NbtUtils.createReader(new InflaterInputStream(raw));
            case COMPRESSION_NONE -> NbtUtils.createReader(new BufferedInputStream(raw));
            default -> throw new IOException("Unsupported Anvil chunk compression type: " + compressionType);
        };
    }

    private static int index(int localX, int localZ) {
        return (localX & 31) + (localZ & 31) * 32;
    }

    @Override
    public void close() throws IOException {
        this.file.close();
    }
}
