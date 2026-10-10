package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.network.protocol.types.BannerPatternType;
import org.jetbrains.annotations.ApiStatus;


public class ItemBannerPattern extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemBannerPattern() {
        this(0, 1, DEFINITION);
    }

    public ItemBannerPattern(ItemDefinition definition) {
        this(0, 1, definition);
    }

    public ItemBannerPattern(Integer meta) {
        this(meta, 1, DEFINITION);
    }

    public ItemBannerPattern(Integer meta, ItemDefinition definition) {
        this(meta, 1, definition);
    }

    public ItemBannerPattern(Integer meta, int count) {
        this(meta, count, DEFINITION);
    }

    public ItemBannerPattern(Integer meta, int count, ItemDefinition definition) {
        super(BANNER_PATTERN, meta, count, "Bone", definition);
    }

    public ItemBannerPattern(String id) {
        this(id, DEFINITION);
    }

    public ItemBannerPattern(String id, ItemDefinition definition) {
        super(id, definition);
    }

    @ApiStatus.Internal
    public void internalAdjust() {
        BannerPatternType patternType = getPatternType();
        name = patternType.getName() + " Pattern";
    }

    public BannerPatternType getPatternType() {
        return BannerPatternType.fromTypeId(getDamage());
    }
}
