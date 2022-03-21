package cn.nukkit.level.format.leveldb;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.format.leveldb.datas.LDBLayer;
import cn.nukkit.level.format.leveldb.palette.Palette;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Zlib;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
@Slf4j
public class LDBChunkSection implements ChunkSection {

    public static final int STREAM_STORAGE_VERSION = 8;
    private static final int BLOCK_ID_MASK          = 0x00FF;
    private static final int BLOCK_ID_EXTRA_MASK    = 0xFF00;
    private static final int BLOCK_ID_FULL          = BLOCK_ID_MASK | BLOCK_ID_EXTRA_MASK;

    private final List<LDBLayer> layers;
    private final int sectionY;

    private LDBLayer layer0 = null;

    protected byte[] blockLight;
    protected byte[] skyLight;
    protected byte[] compressedLight;
    protected boolean hasBlockLight;
    protected boolean hasSkyLight;

    public LDBChunkSection(int sectionY) {
        this.sectionY = sectionY;
        layers = new ArrayList<>();
    }

    LDBChunkSection(int sectionY, List<LDBLayer> layers, LDBLayer layer0, byte[] blockLight, byte[] skyLight, boolean hasBlockLight, boolean hasSkyLight) {
        this.sectionY = sectionY;
        this.layers = layers;
        this.layer0 = layer0;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.hasBlockLight = hasBlockLight;
        this.hasSkyLight = hasSkyLight;
    }

    @Override
    public int getY() {
        return sectionY;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return getLayer(0).getBlockEntryAt(x, y, z).getBlockId();
    }

    @PowerNukkitOnly
    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        return getLayer(layer).getBlockEntryAt(x, y, z).getBlockId();
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        getLayer(0).setBlockEntryAt(x, y, z, BlockState.of(id));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.6.0.0-PNX")
    @Override
    public int getBlockData(int x, int y, int z) {
        return getLayer(0).getBlockEntryAt(x, y, z).getSignedBigDamage();
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return getLayer(layer).getBlockEntryAt(x, y, z).getSignedBigDamage();
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.6.0.0-PNX")
    @Override
    public void setBlockData(int x, int y, int z, int data) {
        getLayer(0).setBlockEntryAt(x, y, z, getBlockState(x, y, z).withData(data));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        getLayer(layer).setBlockEntryAt(x, y, z, getBlockState(x, y, z, layer).withData(data));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.6.0.0-PNX")
    @Override
    public int getFullBlock(int x, int y, int z) {
        return getLayer(0).getBlockEntryAt(x, y, z).getFullId();
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return getLayer(layer).getBlockEntryAt(x, y, z).getFullId();
    }

    @PowerNukkitOnly
    @NotNull
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        var oldBlock = getLayer(layer).getBlockEntryAt(x, y, z).getBlock();
        getLayer(layer).setBlockEntryAt(x, y, z, block.getCurrentState());
        return oldBlock;
    }

    @NotNull
    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        var oldBlock = getLayer(0).getBlockEntryAt(x, y, z).getBlock();
        getLayer(0).setBlockEntryAt(x, y, z, block.getCurrentState());
        return oldBlock;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public BlockState getAndSetBlockState(int x, int y, int z, int layer, @NotNull BlockState state) {
        var oldState = getLayer(layer).getBlockEntryAt(x, y, z);
        getLayer(layer).setBlockEntryAt(x, y, z, state);
        return oldState;
    }

    @PowerNukkitOnly
    @Override
    public void setBlockId(int x, int y, int z, int layer, int id) {
        getLayer(layer).setBlockEntryAt(x, y, z, BlockState.of(id));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.6.0.0-PNX")
    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        Preconditions.checkArgument(fullId < (BLOCK_ID_FULL << Block.DATA_BITS | Block.DATA_MASK), "Invalid full block");
        int blockId = fullId >> Block.DATA_BITS & BLOCK_ID_FULL;
        int data = fullId & Block.DATA_MASK;
        getLayer(0).setBlockEntryAt(x, y, z, BlockState.of(blockId, data));
        return true;
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        Preconditions.checkArgument(fullId < (BLOCK_ID_FULL << Block.DATA_BITS | Block.DATA_MASK), "Invalid full block");
        int blockId = fullId >> Block.DATA_BITS & BLOCK_ID_FULL;
        int data = fullId & Block.DATA_MASK;
        getLayer(layer).setBlockEntryAt(x, y, z, BlockState.of(blockId, data));
        return true;
    }

    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId));
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return setBlockStateAtLayer(x, y, z, 0, BlockState.of(blockId));
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return setBlockStateAtLayer(x, y, z, 0, BlockState.of(blockId, meta));
    }

    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId, meta));
    }

    // TODO: 2022/3/21 实现光照计算

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        if (this.skyLight == null) {
            if (!hasSkyLight) {
                return 0;
            } else if (compressedLight == null) {
                return 15;
            }
            this.skyLight = getSkyLightArray();
        }
        int sl = this.skyLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return sl & 0x0f;
        }
        return sl >> 4;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        if (this.skyLight == null) {
            if (hasSkyLight && compressedLight != null) {
                this.skyLight = getSkyLightArray();
            } else if (level == (hasSkyLight ? 15 : 0)) {
                return;
            } else {
                this.skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(this.skyLight, (byte) 0xFF);
                }
            }
        }
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.skyLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.skyLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.skyLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        if (blockLight == null && !hasBlockLight) return 0;
        this.blockLight = getLightArray();
        int l = blockLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return l & 0x0f;
        }
        return l >> 4;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        if (this.blockLight == null) {
            if (hasBlockLight) {
                this.blockLight = getLightArray();
            } else if (level == 0) {
                return;
            } else {
                this.blockLight = new byte[2048];
            }
        }
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.blockLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.blockLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.blockLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public byte[] getSkyLightArray() {
        if (skyLight != null) {
            return skyLight.clone();
        }

        if (!hasSkyLight) {
            return new byte[EmptyChunkSection.EMPTY_LIGHT_ARR.length];
        }

        if (compressedLight != null && inflate() && skyLight != null) {
            return skyLight.clone();
        }

        return EmptyChunkSection.EMPTY_SKY_LIGHT_ARR.clone();
    }

    @Override
    public byte[] getLightArray() {
        if (blockLight != null) {
            return blockLight.clone();
        }

        if (hasBlockLight && compressedLight != null && inflate() && blockLight != null) {
            return blockLight.clone();
        }

        return new byte[EmptyChunkSection.EMPTY_LIGHT_ARR.length];
    }

    private boolean inflate() {
        try {
            if (compressedLight != null && compressedLight.length != 0) {
                byte[] inflated = Zlib.inflate(compressedLight);
                blockLight = Arrays.copyOfRange(inflated, 0, 2048);
                if (inflated.length > 2048) {
                    skyLight = Arrays.copyOfRange(inflated, 2048, 4096);
                } else {
                    skyLight = new byte[2048];
                    if (hasSkyLight) {
                        Arrays.fill(skyLight, (byte) 0xFF);
                    }
                }
                compressedLight = null;
            } else {
                blockLight = new byte[2048];
                skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(skyLight, (byte) 0xFF);
                }
            }
            return true;
        } catch (IOException e) {
            log.error("Failed to decompress a chunk section", e);
            return false;
        }
    }

    @Since("1.4.0.0-PN")
    @Override
    public void writeTo(BinaryStream stream) {
        stream.putByte((byte) STREAM_STORAGE_VERSION);
        stream.putByte((byte) getLayers().size());

        List<LDBLayer> layers = getLayers();
        for (var layer : layers) {
            layer.writeTo(stream);
        }
    }

    @PowerNukkitOnly
    @Override
    public int getMaximumLayer() {
        return 1;
    }

    @PowerNukkitOnly
    @NotNull
    @Override
    public CompoundTag toNBT() {
        throw new RuntimeException("This method should not be called");
    }

    @NotNull
    @Override
    public LDBChunkSection copy() {
        return new LDBChunkSection(this.sectionY, this.layers, this.layer0, this.blockLight, this.skyLight, this.hasBlockLight, this.hasSkyLight);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setBlockStateAtLayer(int x, int y, int z, int layer, @NotNull BlockState state) {
        var previous = getAndSetBlockState(x, y, z, layer, state);
        return !state.equals(previous);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getBlockChangeStateAbove(int x, int y, int z) {
        return 0; // TODO: 2022/3/21 实现边界方块
    }

    /**
     * Retrieve all of the {@link LDBLayer}s of this subchunk.
     *
     * @return the {@link List} of {@link LDBLayer}s this subchunk holds.
     */
    public List<LDBLayer> getLayers() {
        return Collections.unmodifiableList(this.layers);
    }

    /**
     * Retrieve a specific {@link LDBLayer} in this subchunk.
     * If the layer does not exist, it should create the layer
     *
     * @param index block layer index
     * @return {@link LDBLayer}
     */
    public LDBLayer getLayer(int index) {
        while (index >= this.getLayers().size()) {
            var blockPalette = new Palette<BlockState>();
            blockPalette.addEntry(BlockState.AIR);    // ensure the palette has air

            var blockLayer = new LDBLayer(blockPalette);
            this.addLayer(blockLayer);
        }
        if (index == 0) {
            if (layer0 == null) {
                return layer0 = layers.get(0);
            } else {
                return layer0;
            }
        }
        return this.layers.get(index);
    }

    /**
     * Add a new {@link LDBLayer} to this subchunk.
     *
     * @param layer The {@link LDBLayer} to add
     */
    public void addLayer(LDBLayer layer) {
        this.layers.add(layer);
    }

    public boolean isEmpty() {
        return this.layers.size() == 0;
    }
}
