package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.bitarray.BitArrayVersion;
import cn.nukkit.level.format.palette.BlockPalette;
import cn.nukkit.level.format.palette.Palette;
import cn.nukkit.level.util.NibbleArray;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.registry.Registries;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import lombok.Value;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.HeightMapDataType;
import org.cloudburstmc.protocol.bedrock.data.SubChunkData;
import org.cloudburstmc.protocol.bedrock.data.SubChunkRequestResult;
import org.cloudburstmc.protocol.common.util.VarInts;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiPredicate;

import static cn.nukkit.level.format.IChunk.index;

/**
 * Allay Project 2023/5/30
 *
 * @author Cool_Loong
 */
@NotThreadSafe
public record ChunkSection(byte y,
                           BlockPalette[] blockLayer,
                           Palette<Integer> biomes,
                           NibbleArray blockLights,
                           NibbleArray skyLights,
                           AtomicLong blockChanges) {
    public static final int SIZE = 16 * 16 * 16;
    public static final int LAYER_COUNT = 2;
    public static final int VERSION = 9;

    public ChunkSection(byte sectionY) {
        this(sectionY,
                new BlockPalette[]{new BlockPalette(BlockAir.STATE, new ReferenceArrayList<>(16), BitArrayVersion.V2),
                        new BlockPalette(BlockAir.STATE, new ReferenceArrayList<>(16), BitArrayVersion.V2)},
                new Palette<>(BiomeID.PLAINS),
                new NibbleArray(SIZE),
                new NibbleArray(SIZE),
                new AtomicLong(0));
    }

    public ChunkSection(byte sectionY, BlockPalette[] blockLayer) {
        this(sectionY, blockLayer,
                new Palette<>(BiomeID.PLAINS),
                new NibbleArray(SIZE),
                new NibbleArray(SIZE),
                new AtomicLong(0));
    }

    public BlockState getBlockState(int x, int y, int z) {
        return getBlockState(x, y, z, 0);
    }

    public BlockState getBlockState(int x, int y, int z, int layer) {
        return blockLayer[layer].get(index(x, y, z));
    }

    public void setBlockState(int x, int y, int z, BlockState blockState, int layer) {
        blockChanges.addAndGet(1);
        blockLayer[layer].set(index(x, y, z), blockState);
    }

    public BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        blockChanges.addAndGet(1);
        BlockState result = blockLayer[layer].get(index(x, y, z));
        blockLayer[layer].set(index(x, y, z), blockstate);
        return result;
    }

    public void setBiomeId(int x, int y, int z, int biomeId) {
        biomes.set(index(x, y, z), biomeId);
    }

    public int getBiomeId(int x, int y, int z) {
        return biomes.get(index(x, y, z));
    }

    public byte getBlockLight(int x, int y, int z) {
        return blockLights.get(index(x, y, z));
    }

    public byte getBlockSkyLight(int x, int y, int z) {
        return skyLights.get(index(x, y, z));
    }

    public void setBlockLight(int x, int y, int z, byte light) {
        blockLights.set(index(x, y, z), light);
    }

    public void setBlockSkyLight(int x, int y, int z, byte light) {
        skyLights.set(index(x, y, z), light);
    }

    public List<Block> scanBlocks(LevelProvider provider, int offsetX, int offsetZ, BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        final List<Block> results = new ArrayList<>();
        final BlockVector3 current = new BlockVector3();
        int offsetY = y << 4;
        int minX = Math.max(0, min.x - offsetX);
        int minY = Math.max(0, min.y - offsetY);
        int minZ = Math.max(0, min.z - offsetZ);
        for (int x = Math.min(max.x - offsetX, 15); x >= minX; x--) {
            current.x = offsetX + x;
            for (int z = Math.min(max.z - offsetZ, 15); z >= minZ; z--) {
                current.z = offsetZ + z;
                for (int y = Math.min(max.y - offsetY, 15); y >= minY; y--) {
                    current.y = offsetY + y;
                    BlockState state = blockLayer[0].get(index(x, y, z));
                    if (condition.test(current, state)) {
                        results.add(Registries.BLOCK.get(state, current.x, current.y, current.z, provider.getLevel()));
                    }
                }
            }
        }
        return results;
    }

    public boolean isEmpty() {
        return blockLayer[0].isEmpty() && blockLayer[0].get(0) == BlockAir.STATE;
    }

    public void setNeedReObfuscate() {
        blockLayer[0].setNeedReObfuscate();
        blockLayer[1].setNeedReObfuscate();
    }

    private void writeToBuf(ByteBuf byteBuf) {
        byteBuf.writeByte(VERSION);
        //block layer count
        byteBuf.writeByte(LAYER_COUNT);
        byteBuf.writeByte(y);

        blockLayer[0].writeToNetwork(byteBuf, BlockState::blockStateHash);
        blockLayer[1].writeToNetwork(byteBuf, BlockState::blockStateHash);
    }

    private void writeObfuscatedToBuf(Level level, ByteBuf byteBuf) {
        byteBuf.writeByte(VERSION);
        //block layer count
        byteBuf.writeByte(LAYER_COUNT);
        byteBuf.writeByte(y);

        blockLayer[0].writeObfuscatedToNetwork(level, blockChanges, byteBuf, BlockState::blockStateHash);
        blockLayer[1].writeObfuscatedToNetwork(level, blockChanges, byteBuf, BlockState::blockStateHash);
    }

    public SubChunkData serialize(IChunk chunk, Vector3i position, Level level) {
        if (this.isEmpty()) {
            final SubChunkData subChunkData = new SubChunkData();
            subChunkData.setPosition(position);
            subChunkData.setData(Unpooled.EMPTY_BUFFER);
            subChunkData.setSubChunkRequestResult(SubChunkRequestResult.SUCCESS_ALL_AIR);
            subChunkData.setHeightMapDataType(HeightMapDataType.NO_DATA);
            subChunkData.setRenderHeightMapDataType(HeightMapDataType.NO_DATA);
            return subChunkData;
        }

        final ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
        if (level != null && level.isAntiXrayEnabled()) {
            this.writeObfuscatedToBuf(level, buffer);
        } else {
            this.writeToBuf(buffer);
        }
        this.serializeBlockActors(chunk, level, buffer);

        final HeightMapData heightMapData = HeightMapData.compute(chunk, this.y);
        final SubChunkData subChunkData = new SubChunkData();
        subChunkData.setPosition(position);
        subChunkData.setData(buffer);
        subChunkData.setSubChunkRequestResult(SubChunkRequestResult.SUCCESS);
        subChunkData.setHeightMapDataType(heightMapData.getType());
        subChunkData.setHeightMapData(heightMapData.getBuffer());
        subChunkData.setRenderHeightMapDataType(subChunkData.getHeightMapDataType());
        subChunkData.setRenderHeightMapData(subChunkData.getHeightMapData());
        return subChunkData;
    }

    private void serializeBlockActors(IChunk chunk, Level level, ByteBuf buffer) {
        final List<NbtMap> tagList = new ObjectArrayList<>();
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (blockEntity instanceof BlockEntitySpawnable blockEntitySpawnable &&
                    blockEntitySpawnable.getChunkSectionY() == this.y) {
                tagList.add(blockEntitySpawnable.getSpawnCompound());
                //Adding NBT to a chunk pack does not show some block entities, and you have to send block entity packets to the player
                level.addChunkPacket(blockEntitySpawnable.getChunkX(), blockEntitySpawnable.getChunkZ(), blockEntitySpawnable.getSpawnPacket());
            }
        }
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
             final NBTOutputStream outputStream = NbtUtils.createNetworkWriter(stream)) {
            if (tagList.isEmpty()) {
                stream.writeByte(0);
                VarInts.writeUnsignedInt(buffer, 0);
            } else {
                for (NbtMap nbtMap : tagList) {
                    outputStream.writeTag(nbtMap);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Value
    private static class HeightMapData {
        ByteBuf buffer;
        HeightMapDataType type;

        private static HeightMapData compute(IChunk chunk, int y) {
            boolean areAllTooLow = true;
            boolean areAllTooHigh = true;
            final ByteBuf heightMapDataBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    final int blockY = chunk.getHeightMap(x, z);
                    final int subChunkY = blockY >> 4;
                    if (subChunkY > y) {
                        areAllTooLow = false;
                        heightMapDataBuffer.writeByte(16);
                    } else if (subChunkY < y) {
                        areAllTooHigh = false;
                        heightMapDataBuffer.writeByte(-1);
                    } else {
                        areAllTooLow = false;
                        areAllTooHigh = false;
                        heightMapDataBuffer.writeByte(blockY - (y << 4));
                    }
                }
            }
            final HeightMapDataType heightMapDataType = areAllTooLow ? HeightMapDataType.TOO_LOW : areAllTooHigh ?
                    HeightMapDataType.TOO_HIGH : HeightMapDataType.HAS_DATA;
            return new HeightMapData(heightMapDataBuffer, heightMapDataType);
        }
    }
}
