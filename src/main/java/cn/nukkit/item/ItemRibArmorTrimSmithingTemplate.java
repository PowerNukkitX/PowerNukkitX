package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemRibArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemRibArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemRibArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemRibArmorTrimSmithingTemplate(Integer meta, int count) {
        super(RIB_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Rib Armor Trim Smithing Template");
    }

    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.RIB_ARMOR_TRIM;
    }
}
