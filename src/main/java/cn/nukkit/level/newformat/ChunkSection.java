package cn.nukkit.level.newformat;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ParametersAreNonnullByDefault
public interface ChunkSection {
    int SIZE = 16 * 16 * 16;

    int getY();

    @NotNull
    default BlockState getBlockState(int x, int y, int z) {
        return getBlockState(x, y, z, 0);
    }

    @NotNull
    default BlockState getBlockState(int x, int y, int z, int layer) {
        return null;
    }

    default BlockState getAndSetBlockState(int x, int y, int z, BlockState state) {
        return getAndSetBlockState(x, y, z, state, 0);
    }

    BlockState getAndSetBlockState(int x, int y, int z, BlockState state, int layer);

    boolean setBlockState(int x, int y, int z, BlockState state, int layer);

    default boolean setBlockState(int x, int y, int z, BlockState state) {
        return setBlockState(x, y, z, state, 0);
    }

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    byte[] getSkyLightArray();

    byte[] getLightArray();

    boolean isEmpty();

    void writeTo(BinaryStream stream);

    /**
     * 以混淆方式将子区块写入二进制流，通常用于反矿透
     *
     * @param stream 二进制流
     * @param level  子区块所在世界，包含混淆所用数据
     */
    default void writeObfuscatedTo(BinaryStream stream, Level level) {
        writeTo(stream);
    }

    default void setNeedReObfuscate() {
    }

    /**
     * @return 此section的方块变更数
     */
    long getBlockChanges();

    /**
     * 增加方块变更数
     */
    void addBlockChange();

    int getMaximumLayer();

    @NotNull
    CompoundTag toNBT();

    @NotNull
    ChunkSection copy();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default List<Block> scanBlocks(LevelProvider provider, int offsetX, int offsetZ, BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        /*int offsetY = getY() << 4;
        List<Block> results = new ArrayList<>();

        BlockVector3 current = new BlockVector3();

        int minX = Math.max(0, min.x - offsetX);
        int minY = Math.max(0, min.y - offsetY);
        int minZ = Math.max(0, min.z - offsetZ);

        for (int x = Math.min(max.x - offsetX, 15); x >= minX; x--) {
            current.x = offsetX + x;
            for (int z = Math.min(max.z - offsetZ, 15); z >= minZ; z--) {
                current.z = offsetZ + z;
                for (int y = Math.min(max.y - offsetY, 15); y >= minY; y--) {
                    current.y = offsetY + y;
                    BlockState state = getBlockState(x, y, z);
                    if (condition.test(current, state)) {
                        results.add(state.getBlockRepairing(provider.getLevel(), current, 0));
                    }
                }
            }
        }*/

        return null;
    }
}
