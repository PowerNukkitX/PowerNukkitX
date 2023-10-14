package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemSilenceArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemSilenceArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSilenceArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSilenceArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Silence Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.SILENCE_ARMOR_TRIM;
    }
}
