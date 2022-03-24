package cn.nukkit.level.format.leveldb;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.format.leveldb.datas.LDBChunkBiomeMap;
import cn.nukkit.level.format.leveldb.datas.LDBChunkHeightMap;
import cn.nukkit.level.format.leveldb.datas.LDBSubChunkBiomeMap;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.utils.ChunkException;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
@Log4j2
public class LDBChunk extends BaseChunk {

    private final int dimension;

    private byte version;

    private LDBChunkBiomeMap biomeMap = new LDBChunkBiomeMap();
    private LDBChunkHeightMap heightMap = new LDBChunkHeightMap();

    private final Int2IntOpenHashMap extraData = new Int2IntOpenHashMap();

    private LevelProvider levelProvider;

    protected boolean isInit;
    protected BatchPacket chunkPacket;
    protected boolean terrainPopulated;
    protected boolean terrainGenerated;

    public LDBChunk(LevelProvider levelProvider, int dimension, int chunkX, int chunkZ) {
        this.levelProvider = levelProvider;
        this.providerClass = levelProvider.getClass();
        this.dimension = dimension;
        super.setX(chunkX);
        super.setZ(chunkZ);
        // 初始化子区块
        this.sectionLength = getChunkSectionCount();
        this.sections = new ChunkSection[sectionLength];
        System.arraycopy(getChunkSectionCount() == 24 ? EmptyChunkSection.EMPTY24 : EmptyChunkSection.EMPTY,
                0, this.sections, 0, getChunkSectionCount());
    }

    @Override
    public int getChunkSectionCount() {
        return dimension == 0 ? 24 : 16;
    }

    @Override
    public boolean isSectionEmpty(float fY) {
        return this.sections[(int) fY].isEmpty();
    }

    @Override
    public ChunkSection getSection(float fY) {
        return this.sections[(int) fY];
    }

    @Override
    public boolean setSection(float fY, ChunkSection section) {
        if (!section.hasBlocks()) {
            this.sections[(int) fY] = EmptyChunkSection.EMPTY24[(int) fY];
        } else {
            this.sections[(int) fY] = section;
        }
        setChanged();
        return true;
    }

    @Override
    public ChunkSection[] getSections() {
        return sections;
    }

    @Override
    public LevelProvider getProvider() {
        return this.levelProvider;
    }

    @Override
    public void setProvider(LevelProvider provider) {
        this.levelProvider = provider;
        this.providerClass = provider.getClass();
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return getAndSetBlock(x, y, z, 0, block);
    }

    @PowerNukkitOnly
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        var state = getAndSetBlockState(x, y, z, layer, block.getCurrentState());
        try {
            return state.getBlock();
        } catch (InvalidBlockStateException e) {
            return new BlockUnknown(state.getBlockId(), state.getExactIntStorage());
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state) {
        int sectionY = toSectionY(y);
        try {
            setChanged();
            return getOrCreateMutableSection(sectionY).setBlockStateAtLayer(x, y & 0x0f, z, layer, state);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @PowerNukkitOnly
    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        return this.sections[toSectionY(y)].getBlockId(x, y & 0x0f, z, layer);
    }

    @Override
    public int getBlockExtraData(int x, int y, int z) {
        int index = Level.chunkBlockHash(x, y, z);
        if (this.extraData.containsKey(index)) {
            return this.extraData.get(index);
        }
        return 0;
    }

    @Override
    public void setBlockExtraData(int x, int y, int z, int data) {
        if (data == 0) {
            this.extraData.remove(Level.chunkBlockHash(x, y, z));
        } else {
            this.extraData.put(Level.chunkBlockHash(x, y, z), data);
        }
        this.setChanged(true);
    }

    @Override
    public int getHeightMap(int x, int z) {
        return this.heightMap.getHighestBlockAt(x, z);
    }

    @Override
    public void setHeightMap(int x, int z, int value) {
        this.heightMap.setHighestBlockAt(x, z, value);
    }

    public void setHeightMap(LDBChunkHeightMap heightMap) {
        this.heightMap = heightMap;
    }

    @Override
    public int getBiomeId(int x, int z) {
        var subBiomeMap = this.biomeMap.getTopSubChunkBiomeMap();
        if (subBiomeMap == null) {
            return 0;
        } else {
            return subBiomeMap.getBiomeAt(x, 0, z);
        }
    }

    @Override
    public void setBiomeId(int x, int z, byte biomeId) {
        for (var each : this.biomeMap.getSubChunks()) {
            for (int i = (getDimension() == 0 ? 0 : -64); i < (getDimension() == 0 ? 256 : 320); i++) {
                each.setBiomeAt(x, i, z, biomeId);
            }
        }
    }

    @Override
    public boolean isPopulated() {
        return this.terrainPopulated;
    }

    @Override
    public void setPopulated() {
        this.setPopulated(true);
    }

    @Override
    public void setPopulated(boolean value) {
        this.terrainPopulated = value;
    }

    @Override
    public boolean isGenerated() {
        return this.terrainGenerated;
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public void setGenerated(boolean value) {
        this.terrainGenerated = value;
    }

    @Override
    public byte[] getBiomeIdArray() {
        var buffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            LDBSubChunkBiomeMap lastSubChunkBiomeMap = null;
            for (var subChunkBiomeMap : biomeMap.getSubChunks()) {
                if (subChunkBiomeMap.getPalette().getEntries().size() == 0) {
                    throw new ChunkException("biome sub chunk has no biomes present");
                }
                int bitsPerBlock = (int) Math.ceil(Math.log(subChunkBiomeMap.getPalette().getEntries().size()) / Math.log(2));
                int blocksPerWord = 0;
                int wordsPerChunk = 0;
                if (subChunkBiomeMap.equals(lastSubChunkBiomeMap)) {
                    buffer.writeByte(-1);
                    continue;
                }
                if (bitsPerBlock > 0) {
                    blocksPerWord = 32 / bitsPerBlock;
                    wordsPerChunk = (int) Math.ceil(4096d / blocksPerWord);
                }
                buffer.writeByte((bitsPerBlock << 1) | 1);
                int pos = 0;
                for (int i = 0; i < wordsPerChunk; i++) {
                    int word = 0;
                    for (int block = 0; block < blocksPerWord; block++) {
                        if (pos >= 4096) {
                            break;
                        }
                        word |= subChunkBiomeMap.getPalette().getPaletteIndex(subChunkBiomeMap.getBiomeAt(pos >> 8, (pos >> 4) & 15, pos & 15)) << (bitsPerBlock * block);
                        pos++;
                    }
                    buffer.writeIntLE(word);
                }

                if (bitsPerBlock > 0) {
                    buffer.writeIntLE(subChunkBiomeMap.getPalette().getEntries().size());
                }
                for (int i = 0; i < subChunkBiomeMap.getPalette().getEntries().size(); i++) {
                    buffer.writeIntLE(subChunkBiomeMap.getPalette().getEntry(i));
                }
                lastSubChunkBiomeMap = subChunkBiomeMap;
            }
            var data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);
            return data;
        } finally {
            buffer.release();
        }
    }

    @Override
    public byte[] getHeightMapArray() {
        var data = new byte[512];
        var buffer = Unpooled.wrappedBuffer(data);
        buffer.writerIndex(0);
        try {
            for (int height : heightMap.array()) {
                buffer.writeShortLE(height);
            }
            return data;
        } finally {
            buffer.release();
        }
    }

    @Override
    public Map<Integer, Integer> getBlockExtraDataArray() {
        return extraData;
    }

    @Override
    public byte[] toBinary() {
        throw new UnsupportedOperationException("LDB chunk should not be binarized.");
    }

    @Override
    public byte[] toFastBinary() {
        throw new UnsupportedOperationException("LDB chunk should not be binarized.");
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isBlockChangeAllowed(int x, int y, int z) {
        return true;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public List<Block> findBorders(int x, int z) {
        return Collections.emptyList();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isBlockedByBorder(int x, int z) {
        return false;
    }

    private ChunkSection getOrCreateMutableSection(int sectionY) {
        ChunkSection section = sections[sectionY];
        if (section.isEmpty()) {
            createChunkSection(sectionY);
            return sections[sectionY];
        }

        return section;
    }

    private void createChunkSection(int sectionY) {
        try {
            this.setInternalSection(sectionY, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, sectionY));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to create ChunkSection", e);
            throw new ChunkException(e);
        }
    }

    private void setInternalSection(float fY, ChunkSection section) {
        this.sections[(int) fY] = section;
        setChanged();
    }

    public void setChunkPacket(BatchPacket packet) {
        if (packet != null) {
            packet.trim();
        }
        this.chunkPacket = packet;
    }

    public BatchPacket getChunkPacket() {
        var pk = chunkPacket;
        if (pk != null) {
            pk.trim();
        }
        return chunkPacket;
    }

    public boolean compress() {
        BatchPacket pk = chunkPacket;
        if (pk != null) {
            pk.trim();
            return true;
        }
        return false;
    }

    @SuppressWarnings("DuplicatedCode")
    private void removeInvalidTile(int x, int y, int z) {
        var entity = getTile(x, y, z);
        if (entity != null) {
            try {
                if (!entity.closed && entity.isBlockEntityValid()) {
                    return;
                }
            } catch (Exception e) {
                try {
                    log.warn("Block entity validation of {} at {}, {} {} {} failed, removing as invalid.",
                            entity.getClass().getName(),
                            getProvider().getLevel().getName(),
                            entity.x,
                            entity.y,
                            entity.z,
                            e
                    );
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                    log.warn("Block entity validation failed", e);
                }
            }
            removeBlockEntity(entity);
        }
    }

    public void addInitialEntityNbt(CompoundTag nbt) {
        NBTentities.add(nbt);
    }

    public void addInitialBlockEntityNbt(CompoundTag nbt) {
        NBTtiles.add(nbt);
    }

    public byte getVersion() {
        return version;
    }

    public LDBChunkBiomeMap getBiomeMap() {
        return biomeMap;
    }

    public void setBiomeMap(LDBChunkBiomeMap biomeMap) {
        this.biomeMap = biomeMap;
    }

    public LDBChunkHeightMap getHeightMap() {
        return heightMap;
    }

    public LDBChunk setVersion(byte version) {
        this.version = version;
        return this;
    }

    public int getDimension() {
        return dimension;
    }
}
