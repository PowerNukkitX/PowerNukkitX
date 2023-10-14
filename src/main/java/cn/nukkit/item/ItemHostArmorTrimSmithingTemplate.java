package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemHostArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemHostArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemHostArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemHostArmorTrimSmithingTemplate(Integer meta, int count) {
        super(HOST_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Host Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.HOST_ARMOR_TRIM;
    }
}
