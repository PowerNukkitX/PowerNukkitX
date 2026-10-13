package org.powernukkitx.level.format.leveldb;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.powernukkitx.level.structure.AabbVolumes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Codec for the AABBVolumes {@code 0x77} chunk value.
 *
 * @author Curse
 */
final class LevelDBAabbVolumesCodec {
    private static final int VERSION = 1;

    private static final int STRUCTURE_TYPE_MIN_SIZE = Integer.BYTES + Short.BYTES;
    private static final int BOUNDING_BOX_SIZE = Integer.BYTES * 7;
    private static final int DYNAMIC_SPAWN_AREA_SIZE = Integer.BYTES * 3;
    private static final int STATIC_SPAWN_AREA_SIZE = Integer.BYTES * 4;

    private LevelDBAabbVolumesCodec() {
    }

    static byte[] encode(AabbVolumes volumes) {
        Preconditions.checkNotNull(volumes, "volumes");

        ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer();
        try {
            buffer.writeIntLE(VERSION);

            List<AabbVolumes.StructureType> structureTypes = volumes.getStructureTypes();
            buffer.writeIntLE(structureTypes.size());
            for (int i = structureTypes.size() - 1; i >= 0; i--) {
                AabbVolumes.StructureType structureType = structureTypes.get(i);
                byte[] typeBytes = structureType.type().getBytes(StandardCharsets.UTF_8);
                Preconditions.checkArgument(
                        typeBytes.length <= 0xffff,
                        "Structure type UTF-8 length exceeds uint16: %s",
                        typeBytes.length
                );

                buffer.writeIntLE(structureType.id());
                buffer.writeShortLE(typeBytes.length);
                buffer.writeBytes(typeBytes);
            }

            List<AabbVolumes.BoundingBox> boundingBoxes = volumes.getBoundingBoxes();
            buffer.writeIntLE(boundingBoxes.size());
            for (int i = boundingBoxes.size() - 1; i >= 0; i--) {
                AabbVolumes.BoundingBox boundingBox = boundingBoxes.get(i);
                buffer.writeIntLE(boundingBox.id());
                buffer.writeIntLE(boundingBox.minX());
                buffer.writeIntLE(boundingBox.minY());
                buffer.writeIntLE(boundingBox.minZ());
                buffer.writeIntLE(boundingBox.maxX());
                buffer.writeIntLE(boundingBox.maxY());
                buffer.writeIntLE(boundingBox.maxZ());
            }

            List<AabbVolumes.DynamicSpawnArea> dynamicSpawnAreas = volumes.getDynamicSpawnAreas();
            buffer.writeIntLE(dynamicSpawnAreas.size());
            for (int i = dynamicSpawnAreas.size() - 1; i >= 0; i--) {
                AabbVolumes.DynamicSpawnArea spawnArea = dynamicSpawnAreas.get(i);
                buffer.writeIntLE(spawnArea.boundingBoxId());
                buffer.writeIntLE(spawnArea.structureId());
                buffer.writeIntLE(spawnArea.fullBoundingBox() ? 1 : 0);
            }

            List<AabbVolumes.StaticSpawnArea> staticSpawnAreas = volumes.getStaticSpawnAreas();
            buffer.writeIntLE(staticSpawnAreas.size());
            for (int i = staticSpawnAreas.size() - 1; i >= 0; i--) {
                AabbVolumes.StaticSpawnArea spawnArea = staticSpawnAreas.get(i);
                buffer.writeIntLE(spawnArea.boundingBoxId());
                buffer.writeIntLE(spawnArea.structureId());
                buffer.writeIntLE(spawnArea.offset());
                buffer.writeIntLE(spawnArea.fullBoundingBox() ? 1 : 0);
            }

            return ByteBufUtil.getBytes(buffer);
        } finally {
            buffer.release();
        }
    }

    static AabbVolumes decode(byte[] bytes) throws IOException {
        Preconditions.checkNotNull(bytes, "bytes");

        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        try {
            requireReadable(buffer, Integer.BYTES, "version");

            long version = buffer.readUnsignedIntLE();
            if (version != VERSION) {
                throw new IOException("Unsupported AABBVolumes version: " + version);
            }

            int structureTypeCount = readCount(buffer, STRUCTURE_TYPE_MIN_SIZE, "structure type count");
            List<AabbVolumes.StructureType> structureTypes = new ArrayList<>(structureTypeCount);
            for (int i = 0; i < structureTypeCount; i++) {
                requireReadable(buffer, Integer.BYTES + Short.BYTES, "structure type");

                int id = buffer.readIntLE();
                int length = buffer.readUnsignedShortLE();

                requireReadable(buffer, length, "structure type string");

                byte[] typeBytes = new byte[length];
                buffer.readBytes(typeBytes);
                structureTypes.add(new AabbVolumes.StructureType(id, new String(typeBytes, StandardCharsets.UTF_8)));
            }

            int boundingBoxCount = readCount(buffer, BOUNDING_BOX_SIZE, "bounding box count");
            List<AabbVolumes.BoundingBox> boundingBoxes = new ArrayList<>(boundingBoxCount);
            for (int i = 0; i < boundingBoxCount; i++) {
                requireReadable(buffer, BOUNDING_BOX_SIZE, "bounding box");

                boundingBoxes.add(
                        new AabbVolumes.BoundingBox(
                                buffer.readIntLE(),
                                buffer.readIntLE(),
                                buffer.readIntLE(),
                                buffer.readIntLE(),
                                buffer.readIntLE(),
                                buffer.readIntLE(),
                                buffer.readIntLE()
                        )
                );
            }

            int dynamicSpawnAreaCount = readCount(buffer, DYNAMIC_SPAWN_AREA_SIZE, "dynamic spawn area count");
            List<AabbVolumes.DynamicSpawnArea> dynamicSpawnAreas = new ArrayList<>(dynamicSpawnAreaCount);
            for (int i = 0; i < dynamicSpawnAreaCount; i++) {
                requireReadable(buffer, DYNAMIC_SPAWN_AREA_SIZE, "dynamic spawn area");

                dynamicSpawnAreas.add(
                        new AabbVolumes.DynamicSpawnArea(
                                buffer.readIntLE(),
                                buffer.readIntLE(),
                                buffer.readIntLE() != 0
                        )
                );
            }

            int staticSpawnAreaCount = readCount(buffer, STATIC_SPAWN_AREA_SIZE, "static spawn area count");
            List<AabbVolumes.StaticSpawnArea> staticSpawnAreas = new ArrayList<>(staticSpawnAreaCount);
            for (int i = 0; i < staticSpawnAreaCount; i++) {
                requireReadable(buffer, STATIC_SPAWN_AREA_SIZE, "static spawn area");

                staticSpawnAreas.add(
                        new AabbVolumes.StaticSpawnArea(
                                buffer.readIntLE(),
                                buffer.readIntLE(),
                                buffer.readIntLE(),
                                buffer.readIntLE() != 0
                        )
                );
            }

            return new AabbVolumes(structureTypes, boundingBoxes, dynamicSpawnAreas, staticSpawnAreas);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("Truncated AABBVolumes value", e);
        } finally {
            buffer.release();
        }
    }

    private static int readCount(ByteBuf buffer, int minimumRecordSize, String name) throws IOException {
        requireReadable(buffer, Integer.BYTES, name);

        long count = buffer.readUnsignedIntLE();
        if (count > Integer.MAX_VALUE || count * minimumRecordSize > buffer.readableBytes()) {
            throw new IOException("Invalid AABBVolumes " + name + ": " + count);
        }

        return (int) count;
    }

    private static void requireReadable(ByteBuf buffer, int length, String name) throws IOException {
        if (buffer.readableBytes() < length) {
            throw new IOException("Truncated AABBVolumes " + name);
        }
    }
}
