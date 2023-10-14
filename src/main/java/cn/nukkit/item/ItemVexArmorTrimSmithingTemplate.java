package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author Glorydark
 */
public class ItemVexArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemVexArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemVexArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemVexArmorTrimSmithingTemplate(Integer meta, int count) {
        super(VEX_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Vex Armor Trim Smithing Template");
    }

    @Override
    public ItemTrimPatternType getPattern() {
        return ItemTrimPatternType.VEX_ARMOR_TRIM;
    }
}
