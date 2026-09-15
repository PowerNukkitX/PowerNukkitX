package org.powernukkitx.level.updater.block;

import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.HashUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.experimental.UtilityClass;

/**
 * Resolves block state hashes written before 1.26.50 gave fences and stairs their shape properties.
 * <p>
 * Only the defaults are mapped. A state that had a shape set was not expressible before 1.26.50, so
 * no old hash can point at it.
 * <p>
 *
 * @author xRookieFight
 * @since 12/09/2026
 */
@UtilityClass
public class LegacyBlockStateHashes {

    private static final Set<String> RESHAPED_BLOCKS = new HashSet<>();

    static {
        RESHAPED_BLOCKS.addAll(BlockStateUpdater_1_26_50.CONNECTION_BLOCKS);
        RESHAPED_BLOCKS.addAll(BlockStateUpdater_1_26_50.CORNER_BLOCKS);
    }

    private static final Set<String> ADDED_PROPERTIES = Set.of(
        BlockStateUpdater_1_26_50.CONNECTION_PROPERTIES[0],
        BlockStateUpdater_1_26_50.CONNECTION_PROPERTIES[1],
        BlockStateUpdater_1_26_50.CONNECTION_PROPERTIES[2],
        BlockStateUpdater_1_26_50.CONNECTION_PROPERTIES[3],
        BlockStateUpdater_1_26_50.CORNER_PROPERTY
    );

    private static volatile Int2ObjectMap<BlockState> byLegacyHash;

    /**
     * The state a pre-1.26.50 hash refers to, or {@code null} when it means nothing here.
     */
    public static BlockState get(int legacyHash) {
        return index().get(legacyHash);
    }

    private static Int2ObjectMap<BlockState> index() {
        Int2ObjectMap<BlockState> index = byLegacyHash;
        if (index == null) {
            synchronized (LegacyBlockStateHashes.class) {
                index = byLegacyHash;
                if (index == null) {
                    byLegacyHash = index = build();
                }
            }
        }
        return index;
    }

    private static Int2ObjectMap<BlockState> build() {
        final Int2ObjectMap<BlockState> index = new Int2ObjectOpenHashMap<>();
        for (BlockState state : Registries.BLOCKSTATE.getAllState()) {
            if (!RESHAPED_BLOCKS.contains(state.getIdentifier())) {
                continue;
            }

            final List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> carried = new ArrayList<>();
            boolean allDefaults = true;
            for (var value : state.getBlockPropertyValues()) {
                if (!ADDED_PROPERTIES.contains(value.getPropertyType().getName())) {
                    carried.add(value);
                } else if (!value.getSerializedValue().equals(value.getPropertyType().createDefaultValue().getSerializedValue())) {
                    allDefaults = false;
                    break;
                }
            }
            if (allDefaults) {
                index.putIfAbsent(HashUtils.computeBlockStateHash(state.getIdentifier(), carried), state);
            }
        }
        return index;
    }
}
