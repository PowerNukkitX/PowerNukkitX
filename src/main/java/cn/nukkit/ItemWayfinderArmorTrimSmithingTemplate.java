package cn.nukkit;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemWayfinderArmorTrimSmithingTemplate extends Item {

    public ItemWayfinderArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemWayfinderArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemWayfinderArmorTrimSmithingTemplate(Integer meta, int count) {
        super(WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Wayfinder Armor Trim Smithing Template");
    }
}
