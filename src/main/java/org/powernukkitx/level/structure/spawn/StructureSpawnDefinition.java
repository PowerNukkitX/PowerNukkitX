package org.powernukkitx.level.structure.spawn;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Runtime spawn overrides associated with one structure type.
 *
 * @author Curse
 */
public final class StructureSpawnDefinition {
    private final Map<SpawnCategory, StructureSpawnOverride> overrides;

    /**
     * Creates an immutable structure spawn definition.
     */
    public StructureSpawnDefinition(Map<SpawnCategory, StructureSpawnOverride> overrides) {
        Preconditions.checkNotNull(overrides, "overrides");

        EnumMap<SpawnCategory, StructureSpawnOverride> copy = new EnumMap<>(SpawnCategory.class);
        for (var entry : overrides.entrySet()) {
            copy.put(
                    Preconditions.checkNotNull(entry.getKey(), "category"),
                    Preconditions.checkNotNull(entry.getValue(), "override")
            );
        }

        this.overrides = Collections.unmodifiableMap(copy);
    }

    /**
     * Returns the override for a spawn category, or null when none exists.
     */
    @Nullable
    public StructureSpawnOverride get(SpawnCategory category) {
        return overrides.get(Preconditions.checkNotNull(category, "category"));
    }

    /**
     * Returns all category overrides.
     */
    public Map<SpawnCategory, StructureSpawnOverride> getOverrides() {
        return overrides;
    }
}
