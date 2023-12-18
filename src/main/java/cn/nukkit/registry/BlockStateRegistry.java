package cn.nukkit.registry;

import cn.nukkit.block.state.BlockState;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.function.Consumer;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public final class BlockStateRegistry implements IRegistry<Integer, BlockState, BlockState> {
    private static final Int2ObjectOpenHashMap<BlockState> REGISTRY = new Int2ObjectOpenHashMap<>();

    @Override
    public BlockState get(Integer key) {
        return REGISTRY.get(key.intValue());
    }

    public BlockState get(int blockHash) {
        return REGISTRY.get(blockHash);
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

    @Override
    public void populate(Consumer<IRegistry<Integer, BlockState, BlockState>> iRegistryConsumer) {

    }

    public OK<?> register(BlockState value) {
        if (REGISTRY.put(value.blockStateHash(), value) == null) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("The blockstate has been registered!"));
        }
    }
}
