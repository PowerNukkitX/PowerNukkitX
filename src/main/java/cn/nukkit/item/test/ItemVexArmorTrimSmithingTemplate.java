package cn.nukkit.item.test;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemVexArmorTrimSmithingTemplate extends Item {

    public ItemVexArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemVexArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemVexArmorTrimSmithingTemplate(Integer meta, int count) {
        super(VEX_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Vex Armor Trim Smithing Template");
    }
}
