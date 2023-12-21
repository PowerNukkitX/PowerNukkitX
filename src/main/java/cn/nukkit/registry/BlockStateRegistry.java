package cn.nukkit.registry;

import cn.nukkit.block.state.BlockState;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public final class BlockStateRegistry extends BaseRegistry<Integer, BlockState, BlockState> {
    private static final Int2ObjectOpenHashMap<BlockState> REGISTRY = new Int2ObjectOpenHashMap<>();

    @Override
    public void init() {
    }

    @Override
    public BlockState get(Integer key) {
        return REGISTRY.get(key.intValue());
    }

    public BlockState get(int blockHash) {
        BlockState blockState = REGISTRY.get(blockHash);
        if (blockState == null) {
            throw new IllegalArgumentException("Get the Block State from a unknown blockhash: " + blockHash);
        } else return blockState;
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public OK<?> register(Integer key, BlockState value) {
        if (REGISTRY.putIfAbsent(key, value) == null) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("The blockstate has been registered!"));
        }
    }

    public OK<?> register(BlockState value) {
        if (REGISTRY.put(value.blockStateHash(), value) == null) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("The blockstate has been registered!"));
        }
    }
}
