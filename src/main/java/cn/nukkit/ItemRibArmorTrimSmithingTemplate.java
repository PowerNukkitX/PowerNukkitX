package cn.nukkit;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemRibArmorTrimSmithingTemplate extends Item {

    public ItemRibArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemRibArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemRibArmorTrimSmithingTemplate(Integer meta, int count) {
        super(RIB_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Rib Armor Trim Smithing Template");
    }
}
