package cn.nukkit;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemSnoutArmorTrimSmithingTemplate extends Item {

    public ItemSnoutArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSnoutArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSnoutArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Snout Armor Trim Smithing Template");
    }

}
