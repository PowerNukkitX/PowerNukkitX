package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemDuneArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemDuneArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemDuneArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemDuneArmorTrimSmithingTemplate(Integer meta, int count) {
        super(DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Dune Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.DUNE_ARMOR_TRIM;
    }
}
