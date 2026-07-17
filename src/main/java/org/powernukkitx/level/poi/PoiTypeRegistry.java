package org.powernukkitx.level.poi;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.data.profession.Profession;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Maps block identifiers to {@link PoiType}s. Job-site types are derived from the
 * registered {@link Profession}s; call {@link #refresh()} after registering a
 * profession at runtime so its work station becomes a POI.
 */
public final class PoiTypeRegistry {

    public static final PoiType HOME = new PoiType("minecraft:home", Set.of(BlockID.BED),
            state -> !((Boolean) state.getPropertyValue(CommonBlockProperties.HEAD_PIECE_BIT)), 1, null);

    public static final PoiType MEETING = new PoiType("minecraft:meeting", Set.of(BlockID.BELL),
            state -> true, 32, null);

    private static volatile Map<String, PoiType> byBlockId;
    private static volatile Map<String, PoiType> byName;

    private PoiTypeRegistry() {
    }

    public static @Nullable PoiType forState(BlockState state) {
        PoiType type = byBlockId().get(state.getIdentifier());
        return type != null && type.matches(state) ? type : null;
    }

    public static boolean isPoiBlock(String blockId) {
        return byBlockId().containsKey(blockId);
    }

    /**
     * Resolves a persisted type name back to its {@link PoiType}, or null if it no longer exists.
     */
    public static @Nullable PoiType byName(String name) {
        byBlockId();
        return byName.get(name);
    }

    /**
     * Drops the cached indexes so they are rebuilt from the current professions on next access.
     */
    public static void refresh() {
        synchronized (PoiTypeRegistry.class) {
            byBlockId = null;
            byName = null;
        }
    }

    private static Map<String, PoiType> byBlockId() {
        Map<String, PoiType> map = byBlockId;
        if (map == null) {
            synchronized (PoiTypeRegistry.class) {
                map = byBlockId;
                if (map == null) {
                    map = build();
                    byBlockId = map;
                }
            }
        }
        return map;
    }

    private static Map<String, PoiType> build() {
        Map<String, PoiType> map = new HashMap<>();
        for (String id : HOME.blockIds()) {
            map.put(id, HOME);
        }
        for (String id : MEETING.blockIds()) {
            map.put(id, MEETING);
        }
        for (Profession profession : Profession.getProfessions().values()) {
            String blockId = profession.getBlockID();
            map.putIfAbsent(blockId, new PoiType("job_site:" + blockId, Set.of(blockId), state -> true, 1, blockId));
        }
        Map<String, PoiType> names = new HashMap<>();
        for (PoiType type : map.values()) {
            names.put(type.name(), type);
        }
        byName = names;
        return map;
    }
}
