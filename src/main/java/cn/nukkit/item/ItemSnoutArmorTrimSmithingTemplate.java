package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemSnoutArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemSnoutArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSnoutArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSnoutArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Snout Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.SNOUT_ARMOR_TRIM;
    }
}
