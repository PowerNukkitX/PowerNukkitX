package cn.nukkit.level.format.anvil;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.api.UsedByReflection;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.ChunkSection3DBiome;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.palette.BiomePalette;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Zlib;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class Chunk extends BaseChunk {

    protected long inhabitedTime;
    protected boolean terrainPopulated;
    protected boolean terrainGenerated;
    protected boolean isNew384World = false;
    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    protected DimensionData dimensionData = null;

    @Override
    public Chunk clone() {
        return (Chunk) super.clone();
    }

    public Chunk(LevelProvider level) {
        this(level, (CompoundTag) null);
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    public Chunk(LevelProvider level, DimensionData dimensionData) {
        this(level, null, dimensionData);
    }

    public Chunk(Class<? extends LevelProvider> providerClass) {
        this((LevelProvider) null, (CompoundTag) null);
        this.providerClass = providerClass;
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    public Chunk(Class<? extends LevelProvider> providerClass, DimensionData dimensionData) {
        this((LevelProvider) null, null, dimensionData);
        this.providerClass = providerClass;
    }

    public Chunk(Class<? extends LevelProvider> providerClass, CompoundTag nbt) {
        this((LevelProvider) null, nbt);
        this.providerClass = providerClass;
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    public Chunk(Class<? extends LevelProvider> providerClass, CompoundTag nbt, DimensionData dimensionData) {
        this((LevelProvider) null, nbt, dimensionData);
        this.providerClass = providerClass;
    }

    public Chunk(LevelProvider level, CompoundTag nbt) {
        this(level, nbt, null);
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    public Chunk(LevelProvider level, CompoundTag nbt, DimensionData dimensionData) {
        this.provider = level;
        if (level != null) {
            if (level.getLevel() != null) {
                this.dimensionData = level.getLevel().getDimensionData();
            }
            this.providerClass = level.getClass();
        }
        if (dimensionData != null) {
            this.dimensionData = dimensionData;
        }

        this.sections = new cn.nukkit.level.format.ChunkSection[getChunkSectionCount()];
        this.sectionLength = getChunkSectionCount();
        for (int i = 0; i < sectionLength; i++) {
            sections[i] = new EmptyChunkSection(i);
        }
        if (nbt == null) {
            this.biomes = new byte[16 * 16];
            this.heightMap = new byte[256];
            this.NBTentities = new ArrayList<>(0);
            this.NBTtiles = new ArrayList<>(0);
            return;
        }

        if (nbt.contains("BiomeColors")) {
            this.biomes = new byte[16 * 16];
            int[] biomeColors = nbt.getIntArray("BiomeColors");
            if (biomeColors != null && biomeColors.length == 256) {
                BiomePalette palette = new BiomePalette(biomeColors);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        this.biomes[(x << 4) | z] = (byte) (palette.get(x, z) >> 24);
                    }
                }
            }
        } else {
            this.biomes = Arrays.copyOf(nbt.getByteArray("Biomes"), 256);
        }

        for (Tag section : nbt.getList("Sections").getAll()) {
            if (section instanceof CompoundTag compoundTag) {
                int version = compoundTag.getByte("Version");
                int y = compoundTag.getByte("Y");
                if (y < getChunkSectionCount()) {
                    if (version == -1) {
                        var biomeId = compoundTag.getByteArray("Biomes");
                        if (biomeId != null) {
                            sections[y] = new EmptyChunkSection(y, biomeId);
                        } else {
                            sections[y] = new EmptyChunkSection(y);
                        }
                    } else {
                        final ChunkSection chunkSection = new ChunkSection((CompoundTag) section, this.biomes);
                        if (chunkSection.hasBlocks()) {
                            sections[y] = chunkSection;
                            if (chunkSection.invalidCustomBlockWhenLoad) {
                                this.setChanged();
                            }
                        } else {
                            sections[y] = new EmptyChunkSection(y);
                        }
                    }
                }

            }
        }

        Map<Integer, Integer> extraData = new HashMap<>();

        Tag extra = nbt.get("ExtraData");
        if (extra instanceof ByteArrayTag) {
            BinaryStream stream = new BinaryStream(((ByteArrayTag) extra).data);
            for (int i = 0; i < stream.getInt(); i++) {
                int key = stream.getInt();
                extraData.put(key, stream.getShort());
            }
        }

        this.setPosition(nbt.getInt("xPos"), nbt.getInt("zPos"));
        if (sections.length > getChunkSectionCount()) {
            throw new ChunkException("Invalid amount of chunks");
        }

        int[] heightMap = nbt.getIntArray("HeightMap");
        this.heightMap = new byte[256];
        if (heightMap.length != 256) {
            Arrays.fill(this.heightMap, (byte) 255);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                this.heightMap[i] = (byte) heightMap[i];
            }
        }

        if (!extraData.isEmpty()) this.extraData = extraData;

        this.NBTentities = nbt.getList("Entities", CompoundTag.class).getAll();
        this.NBTtiles = nbt.getList("TileEntities", CompoundTag.class).getAll();
        if (this.NBTentities.isEmpty()) this.NBTentities = null;
        if (this.NBTtiles.isEmpty()) this.NBTtiles = null;

        ListTag<CompoundTag> updateEntries = nbt.getList("TileTicks", CompoundTag.class);

        if (updateEntries != null && updateEntries.size() > 0) {
            for (CompoundTag entryNBT : updateEntries.getAll()) {
                Block block = null;

                try {
                    Tag tag = entryNBT.get("i");
                    if (tag instanceof StringTag) {
                        String name = ((StringTag) tag).data;

                        @SuppressWarnings("unchecked")
                        Class<? extends Block> clazz = (Class<? extends Block>) Class.forName("cn.nukkit.block." + name);

                        Constructor constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        block = (Block) constructor.newInstance();
                    }
                } catch (Throwable e) {
                    continue;
                }

                if (block == null) {
                    continue;
                }

                block.x = entryNBT.getInt("x");
                block.y = entryNBT.getInt("y");
                block.z = entryNBT.getInt("z");
                block.layer = 0;

                this.provider.getLevel().scheduleUpdate(block, block, entryNBT.getInt("t"), entryNBT.getInt("p"), false);
            }
        }

        this.inhabitedTime = nbt.getLong("InhabitedTime");
        this.terrainPopulated = nbt.getBoolean("TerrainPopulated");
        this.terrainGenerated = nbt.getBoolean("TerrainGenerated");
        if (nbt.contains("isNew384World")) {
            this.isNew384World = nbt.getBoolean("isNew384World");
        }
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r5")
    @Override
    protected void createChunkSection(int sectionY) {
        setInternalSection(sectionY, new ChunkSection(sectionY));
    }

    @Since("1.19.20-r4")
    @Override
    public int getMaxHeight() {
        if (dimensionData != null) {
            return dimensionData.getMaxHeight() + 1;
        }
        return getChunkSectionCount() == 24 ? 320 : 256;
    }

    @Since("1.19.20-r4")
    @Override
    public int getMinHeight() {
        if (dimensionData != null) {
            return dimensionData.getMinHeight();
        }
        return getChunkSectionCount() == 24 ? -64 : 0;
    }

    @Since("1.19.20-r3")
    @Override
    public boolean isChunkSection3DBiomeSupported() {
        return true;
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
        if (value != this.terrainPopulated) {
            this.terrainPopulated = value;
            setChanged();
        }
    }

    @Override
    public boolean isGenerated() {
        return this.terrainGenerated || this.terrainPopulated;
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public void setGenerated(boolean value) {
        if (this.terrainGenerated != value) {
            this.terrainGenerated = value;
            setChanged();
        }
    }

    @Override
    public int getBiomeId(int x, int z) {
        return getBiomeId(x, 70, z);
    }

    @Override
    public void setBiomeId(int x, int z, byte biomeId) {
        for (var section : sections) {
            if (section instanceof ChunkSection3DBiome chunkSection3DBiome) {
                for (int dy = 0; dy < 16; dy++) {
                    chunkSection3DBiome.setBiomeId(x, dy, z, biomeId);
                }
            }
        }
        this.biomes[(x << 4) | z] = biomeId;
        this.setChanged();
    }

    @Override
    public int getBiomeId(int x, int y, int z) {
        if (sections[toSectionY(y)] instanceof ChunkSection3DBiome chunkSection3DBiome) {
            return chunkSection3DBiome.getBiomeId(x, y & 0xf, z) & 0xFF;
        }
        return super.getBiomeId(x, z);
    }

    @Override
    public void setBiomeId(int x, int y, int z, byte biomeId) {
        if (sections[toSectionY(y)] instanceof ChunkSection3DBiome chunkSection3DBiome) {
            chunkSection3DBiome.setBiomeId(x, y & 0xf, z, biomeId);
            this.setChanged();
            return;
        }
        this.biomes[(x << 4) | z] = biomeId;
        this.setChanged();
    }

    @Override
    public void setBiome(int x, int y, int z, Biome biome) {
        setBiomeId(x, y, z, (byte) biome.getId());
    }

    public CompoundTag getNBT() {
        CompoundTag tag = new CompoundTag();

        tag.put("LightPopulated", new ByteTag("LightPopulated", (byte) (isLightPopulated() ? 1 : 0)));
        tag.put("InhabitedTime", new LongTag("InhabitedTime", this.inhabitedTime));

        tag.put("V", new ByteTag("V", (byte) 1));

        tag.put("TerrainGenerated", new ByteTag("TerrainGenerated", (byte) (isGenerated() ? 1 : 0)));
        tag.put("TerrainPopulated", new ByteTag("TerrainPopulated", (byte) (isPopulated() ? 1 : 0)));
        tag.putBoolean("isNew384World", this.isNew384World);

        return tag;
    }

    public static Chunk fromBinary(byte[] data) {
        return fromBinary(data, null);
    }

    public static Chunk fromBinary(byte[] data, LevelProvider provider) {
        try {
            CompoundTag chunk = NBTIO.read(new ByteArrayInputStream(Zlib.inflate(data)), ByteOrder.BIG_ENDIAN);

            if (!chunk.contains("Level") || !(chunk.get("Level") instanceof CompoundTag)) {
                return null;
            }

            return new Chunk(provider, chunk.getCompound("Level"));
        } catch (Exception e) {
            log.error("An error has occurred while parsing a chunk from {}", provider.getName(), e);
            return null;
        }
    }


    public static Chunk fromFastBinary(byte[] data) {
        return fromFastBinary(data, null);
    }

    public static Chunk fromFastBinary(byte[] data, LevelProvider provider) {
        try {
            CompoundTag chunk = NBTIO.read(new DataInputStream(new ByteArrayInputStream(data)), ByteOrder.BIG_ENDIAN);
            if (!chunk.contains("Level") || !(chunk.get("Level") instanceof CompoundTag)) {
                return null;
            }

            return new Chunk(provider, chunk.getCompound("Level"));
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public byte[] toFastBinary() {
        CompoundTag nbt = this.getNBT().copy();
        nbt.remove("BiomeColors");

        nbt.putInt("xPos", this.getX());
        nbt.putInt("zPos", this.getZ());

        nbt.putByteArray("Biomes", this.getBiomeIdArray());
        int[] heightInts = new int[256];
        byte[] heightBytes = this.getHeightMapArray();
        for (int i = 0; i < heightInts.length; i++) {
            heightInts[i] = heightBytes[i] & 0xFF;
        }

        for (cn.nukkit.level.format.ChunkSection section : this.getSections()) {
            CompoundTag s = section.toNBT();
            nbt.getList("Sections", CompoundTag.class).add(s);
        }

        ArrayList<CompoundTag> entities = new ArrayList<>();
        for (Entity entity : this.getEntities().values()) {
            if (!(entity instanceof Player) && !entity.closed) {
                entity.saveNBT();
                entities.add(entity.namedTag);
            }
        }
        ListTag<CompoundTag> entityListTag = new ListTag<>("Entities");
        entityListTag.setAll(entities);
        nbt.putList(entityListTag);

        ArrayList<CompoundTag> tiles = new ArrayList<>();
        for (BlockEntity blockEntity : this.getBlockEntities().values()) {
            blockEntity.saveNBT();
            tiles.add(blockEntity.namedTag);
        }
        ListTag<CompoundTag> tileListTag = new ListTag<>("TileEntities");
        tileListTag.setAll(tiles);
        nbt.putList(tileListTag);

        Set<BlockUpdateEntry> entries = this.provider.getLevel().getPendingBlockUpdates(this);

        if (entries != null) {
            ListTag<CompoundTag> tileTickTag = new ListTag<>("TileTicks");
            long totalTime = this.provider.getLevel().getCurrentTick();

            for (BlockUpdateEntry entry : entries) {
                CompoundTag entryNBT = new CompoundTag()
                        .putString("i", entry.block.getSaveId())
                        .putInt("x", entry.pos.getFloorX())
                        .putInt("y", entry.pos.getFloorY())
                        .putInt("z", entry.pos.getFloorZ())
                        .putInt("t", (int) (entry.delay - totalTime))
                        .putInt("p", entry.priority);
                tileTickTag.add(entryNBT);
            }

            nbt.putList(tileTickTag);
        }

        BinaryStream extraData = new BinaryStream();
        Map<Integer, Integer> extraDataArray = this.getBlockExtraDataArray();
        extraData.putInt(extraDataArray.size());
        for (Integer key : extraDataArray.keySet()) {
            extraData.putInt(key);
            extraData.putShort(extraDataArray.get(key));
        }

        nbt.putByteArray("ExtraData", extraData.getBuffer());

        CompoundTag chunk = new CompoundTag("");
        chunk.putCompound("Level", nbt);

        try {
            return NBTIO.write(chunk, ByteOrder.BIG_ENDIAN);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    @Override
    public byte[] toBinary() {
        CompoundTag nbt = this.getNBT().copy();
        nbt.remove("BiomeColors");

        nbt.putInt("xPos", this.getX());
        nbt.putInt("zPos", this.getZ());

        ListTag<CompoundTag> sectionList = new ListTag<>("Sections");
        for (cn.nukkit.level.format.ChunkSection section : this.getSections()) {
            sectionList.add(section.toNBT());
        }
        nbt.putList(sectionList);

        nbt.putByteArray("Biomes", this.getBiomeIdArray());
        int[] heightInts = new int[256];
        byte[] heightBytes = this.getHeightMapArray();
        for (int i = 0; i < heightInts.length; i++) {
            heightInts[i] = heightBytes[i] & 0xFF;
        }
        nbt.putIntArray("HeightMap", heightInts);

        ArrayList<CompoundTag> entities = new ArrayList<>();
        for (Entity entity : this.getEntities().values()) {
            if (!(entity instanceof Player) && !entity.closed) {
                entity.saveNBT();
                entities.add(entity.namedTag);
            }
        }
        ListTag<CompoundTag> entityListTag = new ListTag<>("Entities");
        entityListTag.setAll(entities);
        nbt.putList(entityListTag);

        ArrayList<CompoundTag> tiles = new ArrayList<>();
        for (BlockEntity blockEntity : this.getBlockEntities().values()) {
            blockEntity.saveNBT();
            tiles.add(blockEntity.namedTag);
        }
        ListTag<CompoundTag> tileListTag = new ListTag<>("TileEntities");
        tileListTag.setAll(tiles);
        nbt.putList(tileListTag);

        Set<BlockUpdateEntry> entries = null;
        if (this.provider != null) {
            Level level = provider.getLevel();
            if (level != null) {
                entries = level.getPendingBlockUpdates(this);
            }
        }

        if (entries != null) {
            ListTag<CompoundTag> tileTickTag = new ListTag<>("TileTicks");
            long totalTime = this.provider.getLevel().getCurrentTick();

            for (BlockUpdateEntry entry : entries) {
                CompoundTag entryNBT = new CompoundTag()
                        .putString("i", entry.block.getSaveId())
                        .putInt("x", entry.pos.getFloorX())
                        .putInt("y", entry.pos.getFloorY())
                        .putInt("z", entry.pos.getFloorZ())
                        .putInt("t", (int) (entry.delay - totalTime))
                        .putInt("p", entry.priority);
                tileTickTag.add(entryNBT);
            }

            nbt.putList(tileTickTag);
        }

        BinaryStream extraData = new BinaryStream();
        Map<Integer, Integer> extraDataArray = this.getBlockExtraDataArray();
        extraData.putInt(extraDataArray.size());
        for (Integer key : extraDataArray.keySet()) {
            extraData.putInt(key);
            extraData.putShort(extraDataArray.get(key));
        }

        nbt.putByteArray("ExtraData", extraData.getBuffer());

        CompoundTag chunk = new CompoundTag("");
        chunk.putCompound("Level", nbt);

        try {
            return Zlib.deflate(NBTIO.write(chunk, ByteOrder.BIG_ENDIAN), RegionLoader.COMPRESSION_LEVEL);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        cn.nukkit.level.format.ChunkSection section = this.sections[toSectionY(y)];
        if (section instanceof cn.nukkit.level.format.anvil.ChunkSection) {
            cn.nukkit.level.format.anvil.ChunkSection anvilSection = (cn.nukkit.level.format.anvil.ChunkSection) section;
            if (anvilSection.skyLight != null) {
                return section.getBlockSkyLight(x, y & 0x0f, z);
            } else if (!anvilSection.hasSkyLight) {
                return 0;
            } else {
                int height = getHighestBlockAt(x, z);
                if (height < y) {
                    return 15;
                } else if (height == y) {
                    return Block.isTransparent(getBlockId(x, y, z)) ? 15 : 0;
                } else {
                    return section.getBlockSkyLight(x, y & 0x0f, z);
                }
            }
        } else {
            return section.getBlockSkyLight(x, y & 0x0f, z);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        cn.nukkit.level.format.ChunkSection section = this.sections[toSectionY(y)];
        if (section instanceof cn.nukkit.level.format.anvil.ChunkSection) {
            cn.nukkit.level.format.anvil.ChunkSection anvilSection = (cn.nukkit.level.format.anvil.ChunkSection) section;
            if (anvilSection.blockLight != null) {
                return section.getBlockLight(x, y & 0x0f, z);
            } else if (!anvilSection.hasBlockLight) {
                return 0;
            } else {
                return section.getBlockLight(x, y & 0x0f, z);
            }
        } else {
            return section.getBlockLight(x, y & 0x0f, z);
        }
    }

    @Nullable
    @UsedByReflection
    public static Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return getEmptyChunk(chunkX, chunkZ, (LevelProvider) null);
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    @Nullable
    @UsedByReflection
    public static Chunk getEmptyChunk(int chunkX, int chunkZ, DimensionData dimensionData) {
        return getEmptyChunk(chunkX, chunkZ, null, dimensionData);
    }

    @Nullable
    public static Chunk getEmptyChunk(int chunkX, int chunkZ, LevelProvider provider) {
        return getEmptyChunk(chunkX, chunkZ, provider, null);
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    @Nullable
    public static Chunk getEmptyChunk(int chunkX, int chunkZ, LevelProvider provider, DimensionData dimensionData) {
        try {
            Chunk chunk;
            if (provider != null) {
                chunk = new Chunk(provider, null, dimensionData);
            } else {
                chunk = new Chunk(Anvil.class, null, dimensionData);
            }

            chunk.setPosition(chunkX, chunkZ);

            chunk.heightMap = new byte[256];
            chunk.inhabitedTime = 0;
            chunk.terrainGenerated = false;
            chunk.terrainPopulated = false;
            if (provider != null)
                chunk.isNew384World = provider.isOverWorld();
//            chunk.lightPopulated = false;
            return chunk;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean compress() {
        super.compress();
        boolean result = false;
        for (cn.nukkit.level.format.ChunkSection section : getSections()) {
            if (section instanceof ChunkSection) {
                ChunkSection anvilSection = (ChunkSection) section;
                if (!anvilSection.isEmpty()) {
                    result |= anvilSection.compress();
                }
            }
        }
        return result;
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    @Override
    public int getChunkSectionCount() {
        if (dimensionData != null) return dimensionData.getChunkSectionCount();
        return super.getChunkSectionCount();
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    @Override
    public boolean isOverWorld() {
        if (dimensionData != null) {
            return dimensionData.getDimensionId() == Level.DIMENSION_OVERWORLD;
        }
        return super.isOverWorld();
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    @Override
    public boolean isNether() {
        if (dimensionData != null) {
            return dimensionData.getDimensionId() == Level.DIMENSION_NETHER;
        }
        return super.isNether();
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    @Override
    public boolean isTheEnd() {
        if (dimensionData != null) {
            return dimensionData.getDimensionId() == Level.DIMENSION_THE_END;
        }
        return super.isTheEnd();
    }
}
