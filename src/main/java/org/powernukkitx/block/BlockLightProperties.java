package org.powernukkitx.block;

import org.powernukkitx.registry.Registries;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * Shared, precomputed lighting/opacity properties keyed by {@link BlockState#blockStateHash()}.
 *
 * <p>The table is built lazily on first use from a snapshot of all registered block states, then never mutated,
 * so reads are lock-free. Block states created after the first use (e.g. unknown/custom states registered later)
 * are not in the table and fall back to a live property read.</p>
 */
public final class BlockLightProperties {

    // Packed layout (int): bits 0-7 light Level, bits 8-15 lightFilter, bit 16 diffusesSkyLight, bit 17 isTransparent.
    private static final int FILTER_SHIFT = 8;
    private static final int DIFFUSES_BIT = 1 << 16;
    private static final int TRANSPARENT_BIT = 1 << 17;
    private static final int BYTE_MASK = 0xFF;

    private static volatile Int2IntOpenHashMap table;
    private static final Object BUILD_LOCK = new Object();

    private BlockLightProperties() {
    }

    private static int packOf(Block block) {
        int packed = (block.getLightLevel() & BYTE_MASK)
                | ((block.getLightFilter() & BYTE_MASK) << FILTER_SHIFT);
        if (block.diffusesSkyLight()) packed |= DIFFUSES_BIT;
        if (block.isTransparent()) packed |= TRANSPARENT_BIT;
        return packed;
    }

    private static Int2IntOpenHashMap table() {
        Int2IntOpenHashMap t = table;
        if (t == null) {
            synchronized (BUILD_LOCK) {
                t = table;
                if (t == null) {
                    var states = Registries.BLOCKSTATE.getAllState();
                    t = new Int2IntOpenHashMap(states.size());
                    t.defaultReturnValue(-1);
                    for (BlockState state : states) {
                        Block block = Registries.BLOCK.get(state);
                        if (block != null) {
                            t.put(state.blockStateHash(), packOf(block));
                        }
                    }
                    table = t;
                }
            }
        }
        return t;
    }

    /**
     * @return the packed lighting properties for {@code state}; decode with {@link #lightLevel(int)},
     * {@link #lightFilter(int)}, {@link #diffusesSkyLight(int)}, {@link #isTransparent(int)}.
     */
    public static int packed(BlockState state) {
        int packed = table().get(state.blockStateHash());
        if (packed != -1) {
            return packed;
        }
        Block block = Registries.BLOCK.get(state);
        return block == null ? TRANSPARENT_BIT : packOf(block);
    }

    public static int lightLevel(int packed) {
        return packed & BYTE_MASK;
    }

    public static int lightFilter(int packed) {
        return (packed >>> FILTER_SHIFT) & BYTE_MASK;
    }

    public static boolean diffusesSkyLight(int packed) {
        return (packed & DIFFUSES_BIT) != 0;
    }

    public static boolean isTransparent(int packed) {
        return (packed & TRANSPARENT_BIT) != 0;
    }
}
