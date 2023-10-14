package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemWayfinderArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemWayfinderArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemWayfinderArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemWayfinderArmorTrimSmithingTemplate(Integer meta, int count) {
        super(WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Wayfinder Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.WAYFINDER_ARMOR_TRIM;
    }
}
