package cn.nukkit;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemWildArmorTrimSmithingTemplate extends Item {

    public ItemWildArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemWildArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemWildArmorTrimSmithingTemplate(Integer meta, int count) {
        super(WILD_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Wild Armor Trim Smithing Template");
    }
}
