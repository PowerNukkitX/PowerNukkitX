package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemWildArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemWildArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemWildArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemWildArmorTrimSmithingTemplate(Integer meta, int count) {
        super(WILD_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Wild Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.WILD_ARMOR_TRIM;
    }
}
