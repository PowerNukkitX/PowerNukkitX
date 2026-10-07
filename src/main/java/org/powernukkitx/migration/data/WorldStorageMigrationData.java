package org.powernukkitx.migration.data;

import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Carries world-global LevelDB storage state through versioned world storage migration steps.
 *
 * @author Curse
 */
public record WorldStorageMigrationData(
        CompoundTag legacyDynamicPropertiesRoot,
        CompoundTag dynamicProperties,
        CompoundTag portals,
        CompoundTag biomeData,
        long worldStartCount,
        List<LegacyTickingArea> legacyTickingAreas,
        Map<UUID, CompoundTag> tickingAreas,
        boolean dynamicPropertiesChanged,
        boolean portalsChanged,
        boolean biomeDataChanged,
        boolean tickingAreasChanged
) {
    /**
     * Represents one legacy PNX ticking area prepared for world storage migration.
     *
     * @author Curse
     */
    public record LegacyTickingArea(String name, String levelName, int dimensionId, List<LegacyTickingAreaChunk> chunks) {
    }

    /**
     * Represents one chunk included in a legacy PNX ticking area.
     *
     * @author Curse
     */
    public record LegacyTickingAreaChunk(int x, int z) {
    }
}
