package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemRaiserArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemRaiserArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemRaiserArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemRaiserArmorTrimSmithingTemplate(Integer meta, int count) {
        super(RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Raiser Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.RAISER_ARMOR_TRIM_ARMOR_TRIM;
    }
}
