package cn.nukkit.level.newformat;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.BlockStateRegistry;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.newformat.palette.Palette;
import cn.nukkit.level.util.LevelDBKeyUtil;
import cn.nukkit.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.util.Arrays;

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
        serializeBlock(writeBatch, chunk);
        writeBatch.put(LevelDBKeyUtil.VERSION.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData()), new byte[]{IChunk.VERSION});
        serializeHeightAndBiome(writeBatch, chunk);
        //todo Entity and Block Entity
    }

    public IChunk deserialize(DB db, IChunkBuilder builder) {
        deserializeBlock(db, builder);
        deserializeHeightAndBiome(db, builder);
        //todo Entity and Block Entity
    }

    //serialize chunk section
    private void serializeBlock(WriteBatch writeBatch, IChunk chunk) {
        for (int ySection = chunk.getProvider().getDimensionData().getMinSectionY(); ySection <= chunk.getProvider().getDimensionData().getMaxSectionY(); ySection++) {
            ChunkSection section = chunk.getSection(ySection);
            if (section == null) {
                continue;
            }
            ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
            try {
                buffer.writeByte(ChunkSection.VERSION);
                buffer.writeByte(ChunkSection.LAYER_COUNT);
                buffer.writeByte(ySection);
                for (int i = 0; i < ChunkSection.LAYER_COUNT; i++) {
                    section.blockLayer()[i].writeToStoragePersistent(buffer, BlockState::getBlockStateTag);
                }
                writeBatch.put(LevelDBKeyUtil.CHUNK_SECTION_PREFIX.getKey(chunk.getX(), chunk.getZ(), ySection, chunk.getProvider().getDimensionData()), Utils.convertByteBuf2Array(buffer));
            } finally {
                buffer.release();
            }
        }
    }

    //serialize chunk section
    private void deserializeBlock(DB db, IChunkBuilder builder) {
        DimensionData dimensionInfo = builder.getDimensionData();
        ChunkSection[] sections = new ChunkSection[dimensionInfo.getChunkSectionCount()];
        var minSectionY = dimensionInfo.getMinSectionY();
        for (int ySection = minSectionY; ySection <= dimensionInfo.getMaxSectionY(); ySection++) {
            byte[] bytes = db.get(LevelDBKeyUtil.CHUNK_SECTION_PREFIX.getKey(builder.getChunkX(), builder.getChunkZ(), ySection, dimensionInfo));
            if (bytes != null) {
                ByteBuf byteBuf = null;
                try {
                    byteBuf = ByteBufAllocator.DEFAULT.ioBuffer(bytes.length);
                    byteBuf.writeBytes(bytes);
                    byte subChunkVersion = byteBuf.readByte();
                    int layers = 2;
                    switch (subChunkVersion) {
                        case 9:
                        case 8:
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
                                    BlockState blockState = BlockStateRegistry.get(hash);
                                    if (blockState == null) {
                                        log.error("missing block hash: " + hash);
                                    }
                                    return blockState;
                                });
                            }
                            sections[ySection - minSectionY] = section;
                            break;
                    }
                } finally {
                    if (byteBuf != null) {
                        byteBuf.release();
                    }
                }
            }
        }
        builder.sections(sections);
    }

    //write biomeAndHeight
    private void serializeHeightAndBiome(WriteBatch writeBatch, IChunk chunk) {
        //Write biomeAndHeight
        ByteBuf heightAndBiomesBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            for (short height : chunk.getHeightMapArray()) {
                heightAndBiomesBuffer.writeShortLE(height);
            }
            Palette<Integer> last = null;
            Palette<Integer> biomePalette;
            for (int ySection = chunk.getProvider().getDimensionData().getMinSectionY(); ySection <= chunk.getProvider().getDimensionData().getMaxSectionY(); ySection++) {
                ChunkSection section = chunk.getSection(ySection);
                if (section == null) continue;
                biomePalette = section.biomes();
                biomePalette.writeToStorageRuntime(heightAndBiomesBuffer, Integer::intValue, last);
                last = biomePalette;
            }
            writeBatch.put(LevelDBKeyUtil.DATA_3D.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData()), Utils.convertByteBuf2Array(heightAndBiomesBuffer));
        } finally {
            heightAndBiomesBuffer.release();
        }
    }

    //read biomeAndHeight
    private void deserializeHeightAndBiome(DB db, IChunkBuilder builder) {
        ByteBuf heightAndBiomesBuffer = null;
        try {
            byte[] bytes = db.get(LevelDBKeyUtil.DATA_3D.getKey(builder.getChunkX(), builder.getChunkZ()));
            if (bytes != null) {
                heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes);
                short[] heights = new short[256];
                for (int i = 0; i < 256; i++) {
                    heights[i] = heightAndBiomesBuffer.readShortLE();
                }
                builder.heightMap(new HeightMap(heights));
                Palette<BiomeType> last = null;
                Palette<BiomeType> biomePalette;
                var minSectionY = builder.getDimensionInfo().minSectionY();
                for (int y = minSectionY; y <= builder.getDimensionInfo().maxSectionY(); y++) {
                    ChunkSection section = builder.getSections()[y - minSectionY];
                    if (section == null) continue;
                    biomePalette = section.biomes();
                    biomePalette.readFromStorageRuntime(heightAndBiomesBuffer, VanillaBiomeId::fromId, last);
                    last = biomePalette;
                }
            }
        } finally {
            if (heightAndBiomesBuffer != null) {
                heightAndBiomesBuffer.release();
            }
        }
    }
}
