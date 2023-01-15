package cn.nukkit.level.format.generic;

import cn.nukkit.api.*;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.ChunkSection3DBiome;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.updater.ChunkUpdater;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.ChunkException;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public abstract class BaseChunk extends BaseFullChunk implements Chunk {
    protected ChunkSection[] sections;
    protected int sectionLength;
    private boolean delayPaletteUpdates;

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    @Override
    public void backwardCompatibilityUpdate(Level level) {
        ChunkUpdater.backwardCompatibilityUpdate(level, this);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public final int toSectionY(int blockPosY) {
        if (sectionLength == 24) {
            return (blockPosY >> 4) + 4;
        }
        return blockPosY >> 4;
    }

    @Override
    public BaseChunk clone() {
        BaseChunk chunk = (BaseChunk) super.clone();
        if (this.biomes != null) chunk.biomes = this.biomes.clone();
        chunk.heightMap = this.getHeightMapArray().clone();
        if (sections != null && sections[0] != null) {
            chunk.sections = new ChunkSection[sections.length];
            chunk.sectionLength = sections.length;
            for (int i = 0; i < sections.length; i++) {
                chunk.sections[i] = sections[i].copy();
            }
        }
        return chunk;
    }

    @Override
    public long getBlockChanges() {
        long total = 0;
        for (var section : sections) {
            total += section.getBlockChanges();
        }
        return total;
    }

    /**
     * @param sectionY
     * @return 指定section的方块更改总数
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public long getSectionBlockChanges(int sectionY) {
        return sections[sectionY].getBlockChanges();
    }

    @PowerNukkitDifference(info = "using BlockEntity.close() instead of removeBlockEntity() to solve the bug of incomplete cleanup", since = "1.6.0.0-PNX")
    private void removeInvalidTile(int x, int y, int z) {
        BlockEntity entity = getTile(x, y, z);
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
            entity.close();
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Stream<Block> scanBlocks(BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        int offsetX = getX() << 4;
        int offsetZ = getZ() << 4;
        return IntStream.rangeClosed(min.getChunkSectionY(isOverWorld()), max.getChunkSectionY(isOverWorld()))
                .filter(sectionY -> sectionY >= 0 && sectionY < sections.length)
                .mapToObj(sectionY -> sections[sectionY])
                .filter(section -> !section.isEmpty()).parallel()
                .map(section -> section.scanBlocks(getProvider(), offsetX, offsetZ, min, max, condition))
                .flatMap(Collection::stream);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(x, y, z, 0);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return this.sections[toSectionY(y)].getFullBlock(x, y & 0x0f, z, layer);
    }

    @PowerNukkitOnly
    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return this.sections[toSectionY(y)].getBlockState(x, y & 0x0f, z, layer);
    }

    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        return this.setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId));
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return setBlockState(x, y, z, BlockState.of(blockId));
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return getAndSetBlock(x, y, z, 0, block);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state) {
        int sectionY = toSectionY(y);
        try {
            setChanged();
            return getOrCreateMutableSection(sectionY).getAndSetBlockState(x, y & 0x0f, z, layer, state);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @PowerNukkitOnly
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        BlockState state = getAndSetBlockState(x, y, z, layer, block.getCurrentState());
        try {
            return state.getBlock();
        } catch (InvalidBlockStateException e) {
            return new BlockUnknown(state.getBlockId(), state.getExactIntStorage());
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        return this.setFullBlockId(x, y, z, 0, fullId);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    @Override
    @PowerNukkitOnly
    public boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        int sectionY = toSectionY(y);
        try {
            setChanged();
            return getOrCreateMutableSection(sectionY).setFullBlockId(x, y & 0x0f, z, layer, fullId);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return this.setBlockAtLayer(x, y, z, 0, blockId, meta);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId, meta));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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

    protected ChunkSection getOrCreateMutableSection(int sectionY) {
        ChunkSection section = sections[sectionY];
        if (section.isEmpty()) {
            createChunkSection(sectionY);
            return sections[sectionY];
        }

        return section;
    }

    protected void createChunkSection(int sectionY) {
        try {
            var oldCs = sections[sectionY];
            var newCs = (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, sectionY);
            if (oldCs instanceof ChunkSection3DBiome chunkSection3DBiome && newCs instanceof ChunkSection3DBiome newChunkSection3DBiome) {
                newChunkSection3DBiome.set3DBiomeDataArray(chunkSection3DBiome.get3DBiomeDataArray());
            }
            this.setInternalSection(sectionY, newCs);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to create ChunkSection", e);
            throw new ChunkException(e);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        setBlockStateAtLayer(x, y, z, 0, BlockState.of(id));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @PowerNukkitOnly
    public void setBlockId(int x, int y, int z, int layer, int id) {
        int sectionY = toSectionY(y);
        try {
            setChanged();
            getOrCreateMutableSection(sectionY).setBlockId(x, y & 0x0f, z, layer, id);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return getBlockId(x, y, z, 0);
    }

    @Override
    @PowerNukkitOnly
    public int getBlockId(int x, int y, int z, int layer) {
        return this.sections[toSectionY(y)].getBlockId(x, y & 0x0f, z, layer);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public int getBlockData(int x, int y, int z) {
        return getBlockData(x, y, z, 0);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @PowerNukkitOnly
    public int getBlockData(int x, int y, int z, int layer) {
        return this.sections[toSectionY(y)].getBlockData(x, y & 0x0f, z, layer);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public void setBlockData(int x, int y, int z, int data) {
        setBlockData(x, y, z, 0, data);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        int sectionY = toSectionY(y);
        try {
            setChanged();
            getOrCreateMutableSection(sectionY).setBlockData(x, y & 0x0f, z, layer, data);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.sections[toSectionY(y)].getBlockSkyLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        int sectionY = toSectionY(y);
        getOrCreateMutableSection(sectionY).setBlockSkyLight(x, y & 0x0f, z, level);
        setChanged();
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.sections[toSectionY(y)].getBlockLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int sectionY = toSectionY(y);
        getOrCreateMutableSection(sectionY).setBlockLight(x, y & 0x0f, z, level);
        setChanged();
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
            this.sections[(int) fY] = EmptyChunkSection.EMPTY[(int) fY];
        } else {
            this.sections[(int) fY] = section;
        }
        setChanged();
        return true;
    }

    protected void setInternalSection(float fY, ChunkSection section) {
        if (isPaletteUpdatesDelayed()) {
            section.delayPaletteUpdates();
        }
        this.sections[(int) fY] = section;
        setChanged();
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * getChunkSectionCount());
        for (int y = 0; y < getChunkSectionCount(); y++) {
            buffer.put(this.sections[y].getSkyLightArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * getChunkSectionCount());
        for (int y = 0; y < getChunkSectionCount(); y++) {
            buffer.put(this.sections[y].getLightArray());
        }
        return buffer.array();
    }

    @Override
    public ChunkSection[] getSections() {
        return sections;
    }

    @Override
    public byte[] getHeightMapArray() {
        return this.heightMap;
    }

    @Override
    public LevelProvider getProvider() {
        return this.provider;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public boolean setBlockStateAt(int x, int y, int z, int layer, BlockState state) {
        return setBlockStateAtLayer(x & 0xF, y, z & 0XF, layer, state);
    }

    @PowerNukkitOnly
    @Override
    public BlockState getBlockStateAt(int x, int y, int z, int layer) {
        return getBlockState(x & 0xF, y, z & 0xF, layer);
    }

    @PowerNukkitOnly
    @Override
    public boolean isBlockChangeAllowed(int x, int y, int z) {
        for (ChunkSection section : sections) {
            if (section.getBlockChangeStateAbove(x, 0, z) == 3) { // Border
                return false;
            }
        }

        if (y <= 0) {
            return sections[0].getBlockChangeStateAbove(x, 0, z) == 0;
        }

        int sectionY = toSectionY(y);
        y = y & 0xF;
        for (; sectionY >= 0; sectionY--) {
            switch (sections[sectionY].getBlockChangeStateAbove(x, y, z)) {
                case 1: // Deny
                case 3: // Border
                    return false;
                case 2: // Allow
                    if (sectionY == toSectionY(y)) {
                        return sections[sectionY].getBlockId(x, y, z, 0) != BlockID.ALLOW;
                    } else {
                        return true;
                    }
                default:
                    y = 0xF;
            }
        }
        return true;
    }

    @Nonnull
    @PowerNukkitOnly
    @Override
    public List<Block> findBorders(int x, int z) {
        List<Block> borders = null;
        for (ChunkSection section : sections) {
            if (section.getBlockChangeStateAbove(x, 0, z) == 3) {
                for (int y = 0; y < 0xF; y++) {
                    BlockState blockState = section.getBlockState(x, y, z, 0);
                    if (blockState.getBlockId() == BlockID.BORDER_BLOCK) {
                        if (borders == null) {
                            borders = new ArrayList<>(3);
                        }
                        borders.add(blockState.getBlock(provider.getLevel(), x, y, z, 0));
                    }
                }
            }
        }
        return borders != null ? borders : Collections.emptyList();
    }

    @PowerNukkitOnly
    @Override
    public boolean isBlockedByBorder(int x, int z) {
        for (ChunkSection section : sections) {
            if (section.getBlockChangeStateAbove(x, 0, z) == 3) {
                return true;
            }
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void delayPaletteUpdates() {
        ChunkSection[] sections = this.sections;
        if (sections != null) {
            for (ChunkSection section : sections) {
                if (section != null) {
                    section.delayPaletteUpdates();
                }
            }
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isPaletteUpdatesDelayed() {
        return delayPaletteUpdates;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPaletteUpdatesDelayed(boolean delayPaletteUpdates) {
        this.delayPaletteUpdates = delayPaletteUpdates;
        if (delayPaletteUpdates) {
            delayPaletteUpdates();
        }
    }
}
