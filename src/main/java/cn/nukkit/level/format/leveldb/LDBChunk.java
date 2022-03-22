package cn.nukkit.level.format.leveldb;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.format.leveldb.datas.LDBChunkBiomeMap;
import cn.nukkit.level.format.leveldb.datas.LDBChunkHeightMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class LDBChunk implements Chunk {

    private final int dimension;
    private int x;
    private int z;
    private long hash;

    private byte version;

    private LDBChunkBiomeMap biomeMap = new LDBChunkBiomeMap();
    private LDBChunkHeightMap heightMap = new LDBChunkHeightMap();

    private final Long2ObjectOpenHashMap<Entity> entities = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<BlockEntity> blockEntities = new Long2ObjectOpenHashMap<>();

    private final ChunkSection[] chunkSections;
    private final int sectionLength;
    private LevelProvider levelProvider;

    public LDBChunk(LevelProvider levelProvider, int dimension, int chunkX, int chunkZ) {
        this.levelProvider = levelProvider;
        this.dimension = dimension;
        this.x = chunkX;
        this.z = chunkZ;
        this.hash = Level.chunkHash(chunkX, chunkZ);
        // 初始化子区块
        this.sectionLength = getChunkSectionCount();
        this.chunkSections = new ChunkSection[sectionLength];
        System.arraycopy(getChunkSectionCount() == 24 ? EmptyChunkSection.EMPTY24 : EmptyChunkSection.EMPTY,
                0, this.chunkSections, 0, getChunkSectionCount());
    }

    public final int toSectionY(int blockPosY) {
        if (sectionLength == 24) {
            return (blockPosY >> 4) + 4;
        }
        return blockPosY >> 4;
    }

    @Override
    public int getChunkSectionCount() {
        return dimension == 0 ? 24 : 16;
    }

    @Override
    public boolean isSectionEmpty(float fY) {
        return this.chunkSections[(int) fY].isEmpty();
    }

    @Override
    public ChunkSection getSection(float fY) {
        return this.chunkSections[(int) fY];
    }

    @Override
    public boolean setSection(float fY, ChunkSection section) {
        if (!section.hasBlocks()) {
            this.chunkSections[(int) fY] = EmptyChunkSection.EMPTY[(int) fY];
        } else {
            this.chunkSections[(int) fY] = section;
        }
        setChanged();
        return true;
    }

    @Override
    public ChunkSection[] getSections() {
        return chunkSections;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void setX(int x) {
        this.x = x;
        this.hash = Level.chunkHash(x, z);
    }

    @Override
    public void setZ(int z) {
        this.z = z;
        this.hash = Level.chunkHash(x, z);
    }

    @Override
    public long getIndex() {
        return hash;
    }

    @Override
    public LevelProvider getProvider() {
        return this.levelProvider;
    }

    @Override
    public void setProvider(LevelProvider provider) {
        this.levelProvider = provider;
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return 0;
    }

    @PowerNukkitOnly
    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return null;
    }

    @PowerNukkitOnly
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        return null;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state) {
        return null;
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return false;
    }

    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state) {
        return false;
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return false;
    }

    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        return false;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return 0;
    }

    @PowerNukkitOnly
    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {

    }

    @PowerNukkitOnly
    @Override
    public void setBlockId(int x, int y, int z, int layer, int id) {

    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return 0;
    }

    @PowerNukkitOnly
    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {

    }

    @PowerNukkitOnly
    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {

    }

    @Override
    public int getBlockExtraData(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockExtraData(int x, int y, int z, int data) {

    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {

    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {

    }

    @Override
    public int getHighestBlockAt(int x, int z) {
        return 0;
    }

    @Override
    public int getHighestBlockAt(int x, int z, boolean cache) {
        return 0;
    }

    @Override
    public int getHeightMap(int x, int z) {
        return 0;
    }

    @Override
    public void setHeightMap(int x, int z, int value) {

    }

    @Override
    public void recalculateHeightMap() {

    }

    @PowerNukkitOnly
    @Override
    public int recalculateHeightMapColumn(int chunkX, int chunkZ) {
        return 0;
    }

    @Override
    public void populateSkyLight() {

    }

    @Override
    public int getBiomeId(int x, int z) {
        return 0;
    }

    @Override
    public void setBiomeId(int x, int z, byte biomeId) {

    }

    @Override
    public boolean isLightPopulated() {
        return false;
    }

    @Override
    public void setLightPopulated() {

    }

    @Override
    public void setLightPopulated(boolean value) {

    }

    @Override
    public boolean isPopulated() {
        return false;
    }

    @Override
    public void setPopulated() {

    }

    @Override
    public void setPopulated(boolean value) {

    }

    @Override
    public boolean isGenerated() {
        return false;
    }

    @Override
    public void setGenerated() {

    }

    @Override
    public void setGenerated(boolean value) {

    }

    @Override
    public void addEntity(Entity entity) {

    }

    @Override
    public void removeEntity(Entity entity) {

    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {

    }

    @Override
    public void removeBlockEntity(BlockEntity blockEntity) {

    }

    @Override
    public Map<Long, Entity> getEntities() {
        return null;
    }

    @Override
    public Map<Long, BlockEntity> getBlockEntities() {
        return null;
    }

    @Override
    public BlockEntity getTile(int x, int y, int z) {
        return null;
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public boolean load() throws IOException {
        return false;
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return false;
    }

    @Override
    public boolean unload() throws Exception {
        return false;
    }

    @Override
    public boolean unload(boolean save) throws Exception {
        return false;
    }

    @Override
    public boolean unload(boolean save, boolean safe) throws Exception {
        return false;
    }

    @Override
    public void initChunk() {

    }

    @Override
    public byte[] getBiomeIdArray() {
        return new byte[0];
    }

    @Override
    public byte[] getHeightMapArray() {
        return new byte[0];
    }

    @Override
    public Map<Integer, Integer> getBlockExtraDataArray() {
        return null;
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        return new byte[0];
    }

    @Override
    public byte[] getBlockLightArray() {
        return new byte[0];
    }

    @Override
    public byte[] toBinary() {
        return new byte[0];
    }

    @Override
    public byte[] toFastBinary() {
        return new byte[0];
    }

    @Override
    public boolean hasChanged() {
        return false;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public void setChanged(boolean changed) {

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
}
