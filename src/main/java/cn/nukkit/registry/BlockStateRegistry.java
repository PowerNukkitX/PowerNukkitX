package cn.nukkit.registry;

import cn.nukkit.block.BlockState;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public final class BlockStateRegistry implements IRegistry<Integer, BlockState, BlockState> {
    private static final Int2ObjectOpenHashMap<BlockState> REGISTRY = new Int2ObjectOpenHashMap<>();

    @Override
    public void init() {
        //register from Block Registry
    }

    @Override
    public BlockState get(Integer key) {
        return REGISTRY.get(key.intValue());
    }

    public BlockState get(int blockHash) {
        return REGISTRY.get(blockHash);
    }

    @UnmodifiableView
    public Set<BlockState> getAllState() {
        return Set.copyOf(REGISTRY.values());
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void reload() {
        REGISTRY.clear();
    }

    @Override
    public void register(Integer key, BlockState value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key, value) == null) {
        } else {
            throw new RegisterException("The blockstate has been registered!");
        }
    }

    public void register(BlockState value) throws RegisterException {
        BlockState now;
        if ((now = REGISTRY.put(value.blockStateHash(), value)) == null) {
        } else {
            throw new RegisterException("The blockstate " + value + "has been registered,\n current value: " + now);
        }
    }

    @ApiStatus.Internal
    public void registerInternal(BlockState value) {
        try {
            register(value);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
