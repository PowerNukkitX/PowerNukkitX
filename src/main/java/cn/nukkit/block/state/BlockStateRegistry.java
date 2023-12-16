package cn.nukkit.block.state;

import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public final class BlockStateRegistry {
    private static final Int2ObjectOpenHashMap<BlockState> REGISTRY = new Int2ObjectOpenHashMap<>();

    public static void trim() {
        REGISTRY.trim();
    }

    public static BlockState get(int blockHash) {
        return REGISTRY.get(blockHash);
    }

    public static OK<?> register(BlockState blockState) {
        if (REGISTRY.putIfAbsent(blockState.blockStateHash(), blockState) == null) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("The blockstate has been registered!"));
        }
    }
}
