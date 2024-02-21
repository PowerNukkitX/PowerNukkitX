package cn.nukkit.level.format.leveldb;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.IChunkBuilder;
import cn.nukkit.level.format.UnsafeChunk;
import cn.nukkit.level.format.palette.Palette;
import cn.nukkit.level.util.LevelDBKeyUtil;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Utils;
import com.google.common.base.Predicates;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Allay Project 8/23/2023
 *
 * @author Cool_Loong
 */
@Slf4j
public class LevelDBChunkSerializer {
    public static final LevelDBChunkSerializer INSTANCE = new LevelDBChunkSerializer();

    private LevelDBChunkSerializer() {
    }

    public void serialize(WriteBatch writeBatch, IChunk chunk) {
        chunk.batchProcess(unsafeChunk -> {
            try {
                writeBatch.put(LevelDBKeyUtil.VERSION.getKey(unsafeChunk.getX(), unsafeChunk.getZ(), unsafeChunk.getProvider().getDimensionData()), new byte[]{IChunk.VERSION});
                writeBatch.put(LevelDBKeyUtil.CHUNK_FINALIZED_STATE.getKey(unsafeChunk.getX(), unsafeChunk.getZ(), unsafeChunk.getDimensionData()), Unpooled.buffer(4).writeIntLE(unsafeChunk.getChunkState().ordinal() - 1).array());
                writeBatch.put(LevelDBKeyUtil.PNX_EXTRA_DATA.getKey(unsafeChunk.getX(), unsafeChunk.getZ(), unsafeChunk.getDimensionData()), NBTIO.write(unsafeChunk.getExtraData()));
                serializeBlock(writeBatch, unsafeChunk);
                serializeHeightAndBiome(writeBatch, unsafeChunk);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //Spawning block entities requires call the getSpawnPacket method,
        //which is easy to call Level#getBlock, which can cause a deadlock,
        //so handle it without locking
        serializeTileAndEntity(writeBatch, chunk);
    }

    public void deserialize(DB db, IChunkBuilder builder) throws IOException {
        byte[] versionValue = db.get(LevelDBKeyUtil.VERSION.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        if (versionValue == null) {
            versionValue = db.get(LevelDBKeyUtil.LEGACY_VERSION.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        }
        if (versionValue == null) {
            return;
        }
        byte[] finalized = db.get(LevelDBKeyUtil.CHUNK_FINALIZED_STATE.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        if (finalized == null) {
            builder.state(ChunkState.FINISHED);
        } else {
            builder.state(ChunkState.values()[Unpooled.wrappedBuffer(finalized).readIntLE() + 1]);
        }
        byte[] extraData = db.get(LevelDBKeyUtil.PNX_EXTRA_DATA.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        CompoundTag pnxExtraData = null;
        if (extraData != null) {
            builder.extraData(pnxExtraData = NBTIO.read(extraData));
        }
        deserializeBlock(db, builder, pnxExtraData);
        deserializeHeightAndBiome(db, builder, pnxExtraData);
        deserializeTileAndEntity(db, builder, pnxExtraData);
    }

    //serialize chunk section
    private void serializeBlock(WriteBatch writeBatch, UnsafeChunk chunk) {
        ChunkSection[] sections = chunk.getSections();
        for (var section : sections) {
            if (section == null) {
                continue;
            }
            ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
            try {
                buffer.writeByte(ChunkSection.VERSION);
                buffer.writeByte(ChunkSection.LAYER_COUNT);
                buffer.writeByte(section.y());
                for (int i = 0; i < ChunkSection.LAYER_COUNT; i++) {
                    section.blockLayer()[i].writeToStoragePersistent(buffer, BlockState::getBlockStateTag);
                }
                writeBatch.put(LevelDBKeyUtil.CHUNK_SECTION_PREFIX.getKey(chunk.getX(), chunk.getZ(), section.y(), chunk.getProvider().getDimensionData()), Utils.convertByteBuf2Array(buffer));
            } finally {
                buffer.release();
            }
        }
    }

    //serialize chunk section
    private void deserializeBlock(DB db, IChunkBuilder builder, CompoundTag pnxExtraData) {
        DimensionData dimensionInfo = builder.getDimensionData();
        ChunkSection[] sections = new ChunkSection[dimensionInfo.getChunkSectionCount()];
        var minSectionY = dimensionInfo.getMinSectionY();
        for (int ySection = minSectionY; ySection <= dimensionInfo.getMaxSectionY(); ySection++) {
            byte[] bytes = db.get(LevelDBKeyUtil.CHUNK_SECTION_PREFIX.getKey(builder.getChunkX(), builder.getChunkZ(), ySection, dimensionInfo));
            if (bytes != null) {
                ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
                try {
                    byteBuf.writeBytes(bytes);
                    byte subChunkVersion = byteBuf.readByte();
                    int layers = 2;
                    switch (subChunkVersion) {
                        case 8, 9:
                            layers = byteBuf.readByte();//layers
                            if (subChunkVersion == 9) {
                                byteBuf.readByte();//sectionY not use
                            }
                        case 1:
                            ChunkSection section;
                            if (layers <= 2) {
                                section = new ChunkSection((byte) ySection);
                            } else {
                                @SuppressWarnings("rawtypes") Palette[] palettes = new Palette[layers];
                                Arrays.fill(palettes, new Palette<>(BlockAir.PROPERTIES.getDefaultState()));
                                section = new ChunkSection((byte) ySection, palettes);
                            }
                            for (int layer = 0; layer < layers; layer++) {
                                section.blockLayer()[layer].readFromStoragePersistent(byteBuf, hash -> {
                                    BlockState blockState = Registries.BLOCKSTATE.get(hash);
                                    if (blockState == null) {
                                        log.error("missing block hash: " + hash);
                                        return BlockUnknown.PROPERTIES.getDefaultState();
                                    }
                                    return blockState;
                                });
                            }
                            sections[ySection - minSectionY] = section;
                    }
                } finally {
                    byteBuf.release();
                }
            }
            builder.sections(sections);
        }
    }

    //write biomeAndHeight
    private void serializeHeightAndBiome(WriteBatch writeBatch, UnsafeChunk chunk) {
        //Write biomeAndHeight
        ByteBuf heightAndBiomesBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            for (short height : chunk.getHeightMapArray()) {
                heightAndBiomesBuffer.writeShortLE(height);
            }
            Palette<Integer> biomePalette;
            for (int ySection = chunk.getProvider().getDimensionData().getMinSectionY(); ySection <= chunk.getProvider().getDimensionData().getMaxSectionY(); ySection++) {
                ChunkSection section = chunk.getSection(ySection);
                if (section == null) continue;
                biomePalette = section.biomes();
                biomePalette.writeToStorageRuntime(heightAndBiomesBuffer, Integer::intValue);
            }
            if (heightAndBiomesBuffer.readableBytes() > 0) {
                writeBatch.put(LevelDBKeyUtil.DATA_3D.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData()), Utils.convertByteBuf2Array(heightAndBiomesBuffer));
            }
        } finally {
            heightAndBiomesBuffer.release();
        }
    }

    //read biomeAndHeight
    private void deserializeHeightAndBiome(DB db, IChunkBuilder builder, CompoundTag pnxExtraData) {
        ByteBuf heightAndBiomesBuffer = null;
        try {
            DimensionData dimensionInfo = builder.getDimensionData();
            byte[] bytes = db.get(LevelDBKeyUtil.DATA_3D.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
            if (bytes != null) {
                heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes);
                short[] heights = new short[256];
                for (int i = 0; i < 256; i++) {
                    heights[i] = heightAndBiomesBuffer.readShortLE();
                }
                builder.heightMap(heights);
                Palette<Integer> biomePalette;
                var minSectionY = builder.getDimensionData().getMinSectionY();
                for (int y = minSectionY; y <= builder.getDimensionData().getMaxSectionY(); y++) {
                    ChunkSection section = builder.getSections()[y - minSectionY];
                    if (section == null) continue;
                    biomePalette = section.biomes();
                    biomePalette.readFromStorageRuntime(heightAndBiomesBuffer, Integer::valueOf);
                }
            } else {
                byte[] bytes2D = db.get(LevelDBKeyUtil.DATA_2D.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
                if (bytes2D != null) {
                    heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes2D);
                    short[] heights = new short[256];
                    for (int i = 0; i < 256; i++) {
                        heights[i] = heightAndBiomesBuffer.readShortLE();
                    }
                    builder.heightMap(heights);
                    byte[] biomes = new byte[256];
                    heightAndBiomesBuffer.readBytes(biomes);

                    var minSectionY = builder.getDimensionData().getMinSectionY();
                    for (int y = minSectionY; y <= builder.getDimensionData().getMaxSectionY(); y++) {
                        ChunkSection section = builder.getSections()[y - minSectionY];
                        if (section == null) continue;
                        final Palette<Integer> biomePalette = section.biomes();
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                for (int sy = 0; sy < 16; sy++) {
                                    biomePalette.set(IChunk.index(x, sy, z), (int) biomes[x + 16 * z]);
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            if (heightAndBiomesBuffer != null) {
                heightAndBiomesBuffer.release();
            }
        }
    }

    private void deserializeTileAndEntity(DB db, IChunkBuilder builder, CompoundTag pnxExtraData) {
        DimensionData dimensionInfo = builder.getDimensionData();
        byte[] tileBytes = db.get(LevelDBKeyUtil.BLOCK_ENTITIES.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
        if (tileBytes != null) {
            List<CompoundTag> blockEntityTags = new ArrayList<>();
            try (BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream(tileBytes))) {
                while (stream.available() > 0) {
                    blockEntityTags.add(NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            builder.blockEntities(blockEntityTags);
        }

        byte[] key = LevelDBKeyUtil.ENTITIES.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo);
        byte[] entityBytes = db.get(key);
        if (entityBytes == null) return;
        List<CompoundTag> entityTags = new ArrayList<>();
        try (BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream(entityBytes))) {
            while (stream.available() > 0) {
                entityTags.add(NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (pnxExtraData == null) {
            db.delete(key);
            List<CompoundTag> list = entityTags.stream().map(BDSEntityTranslator::translate).filter(Predicates.notNull()).toList();
            builder.entities(list);
        } else {
            builder.entities(entityTags);
        }
    }

    private void serializeTileAndEntity(WriteBatch writeBatch, IChunk chunk) {
        //Write blockEntities
        Collection<BlockEntity> blockEntities = chunk.getBlockEntities().values();
        ByteBuf tileBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try (var bufStream = new ByteBufOutputStream(tileBuffer)) {
            byte[] key = LevelDBKeyUtil.BLOCK_ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData());
            if (blockEntities.isEmpty()) writeBatch.delete(key);
            else {
                for (BlockEntity blockEntity : blockEntities) {
                    blockEntity.saveNBT();
                    NBTIO.write(blockEntity.namedTag, bufStream, ByteOrder.LITTLE_ENDIAN);
                }
                writeBatch.put(key, Utils.convertByteBuf2Array(tileBuffer));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            tileBuffer.release();
        }

        Collection<Entity> entities = chunk.getEntities().values();
        ByteBuf entityBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try (var bufStream = new ByteBufOutputStream(entityBuffer)) {
            byte[] key = LevelDBKeyUtil.ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData());
            if (entities.isEmpty()) {
                writeBatch.delete(key);
            } else {
                for (Entity e : entities) {
                    if (!(e instanceof Player) && !e.closed && e.canBeSavedWithChunk()) {
                        e.saveNBT();
                        NBTIO.write(e.namedTag, bufStream, ByteOrder.LITTLE_ENDIAN);
                    }
                }
                writeBatch.put(key, Utils.convertByteBuf2Array(entityBuffer));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            entityBuffer.release();
        }
    }
}
