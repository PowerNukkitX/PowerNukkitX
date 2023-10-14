package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemSpireArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemSpireArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSpireArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSpireArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Spire Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.SPIRE_ARMOR_TRIM;
    }
}
