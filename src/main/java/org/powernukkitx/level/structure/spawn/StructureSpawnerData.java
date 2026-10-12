package org.powernukkitx.level.structure.spawn;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

/**
 * SpawnerData used by structure-specific weighted spawn lists.
 *
 * @author Curse
 */
public record StructureSpawnerData(
        String entityId,
        String initializationEvent,
        int probabilityWeight,
        int minCount,
        int maxCount,
        SpawnOverrideState surface,
        SpawnOverrideState underground,
        SpawnOverrideState underwater,
        @Nullable Population population,
        @Nullable Brightness brightness
) {
    public StructureSpawnerData {
        Preconditions.checkNotNull(entityId, "entityId");
        Preconditions.checkNotNull(initializationEvent, "initializationEvent");
        Preconditions.checkNotNull(surface, "surface");
        Preconditions.checkNotNull(underground, "underground");
        Preconditions.checkNotNull(underwater, "underwater");
        Preconditions.checkArgument(minCount >= Short.MIN_VALUE && minCount <= Short.MAX_VALUE, "minCount exceeds short range");
        Preconditions.checkArgument(maxCount >= Short.MIN_VALUE && maxCount <= Short.MAX_VALUE, "maxCount exceeds short range");
    }

    /**
     * Creates SpawnerData with default override states.
     */
    public static StructureSpawnerData of(String entityId, int probabilityWeight, int minCount, int maxCount) {
        return new StructureSpawnerData(
                entityId,
                "",
                probabilityWeight,
                minCount,
                maxCount,
                SpawnOverrideState.UNSET,
                SpawnOverrideState.UNSET,
                SpawnOverrideState.UNSET,
                null,
                null
        );
    }

    /**
     * Optional structure-spawn population limits.
     */
    public record Population(int surface, int underground) {
        public Population {
            Preconditions.checkArgument(surface >= Short.MIN_VALUE && surface <= Short.MAX_VALUE, "surface exceeds short range");
            Preconditions.checkArgument(
                    underground >= Short.MIN_VALUE && underground <= Short.MAX_VALUE,
                    "underground exceeds short range"
            );
        }
    }

    /**
     * Optional structure-spawn brightness limits.
     */
    public record Brightness(int min, int max, boolean raw) {
        public Brightness {
            Preconditions.checkArgument(min >= Short.MIN_VALUE && min <= Short.MAX_VALUE, "min exceeds short range");
            Preconditions.checkArgument(max >= Short.MIN_VALUE && max <= Short.MAX_VALUE, "max exceeds short range");
        }
    }
}
