package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemEyeArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemEyeArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemEyeArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemEyeArmorTrimSmithingTemplate(Integer meta, int count) {
        super(EYE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Eye Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.EYE_ARMOR_TRIM;
    }
}
