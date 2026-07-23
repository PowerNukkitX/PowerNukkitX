package org.powernukkitx.block;

import org.powernukkitx.registry.Registries;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

/**
 * Shared, precomputed lighting/opacity properties keyed by {@link BlockState#blockStateHash()}.
 *
 * <p>The table is built once from a snapshot of all registered block states, then never mutated, so reads are
 * lock-free. It should be built eagerly during server startup via {@link #build()} (after block registration,
 * before any level ticks) so its cost never lands on a live tick; if that never happens it falls back to building
 * lazily on first use. Block states created after the build (e.g. unknown/custom states registered later) are not
 * in the table and fall back to a live property read.</p>
 */
public final class BlockLightProperties {

    // Packed layout (int): bits 0-7 lightLevel, bits 8-15 lightFilter, bit 16 diffusesSkyLight, bit 17 isTransparent.
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

    /**
     * Eagerly builds the property table. Intended to be called once during server startup, after all blocks are
     * registered and before any level starts ticking, so the (palette-wide) build cost never stalls a live tick.
     * Idempotent: subsequent calls, and any concurrent lazy {@link #table()} access, are no-ops.
     */
    public static void build() {
        table();
    }

    private static Int2IntOpenHashMap table() {
        Int2IntOpenHashMap t = table;
        if (t == null) {
            synchronized (BUILD_LOCK) {
                t = table;
                if (t == null) {
                    t = buildTable();
                    table = t;
                }
            }
        }
        return t;
    }

    private static Int2IntOpenHashMap buildTable() {
        var states = Registries.BLOCKSTATE.getAllState();
        Int2IntOpenHashMap t = new Int2IntOpenHashMap(states.size());
        t.defaultReturnValue(-1);
        Object2ObjectOpenHashMap<String, Block> prototypes = new Object2ObjectOpenHashMap<>();
        for (BlockState state : states) {
            Block block = prototypes.get(state.getIdentifier());
            if (block == null) {
                block = Registries.BLOCK.get(state);
                if (block == null) {
                    continue;
                }
                prototypes.put(state.getIdentifier(), block);
            }
            block.blockstate = state;
            t.put(state.blockStateHash(), packOf(block));
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
