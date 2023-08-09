package cn.nukkit;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemCoastArmorTrimSmithingTemplate extends Item {

    public ItemCoastArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemCoastArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemCoastArmorTrimSmithingTemplate(Integer meta, int count) {
        super(COAST_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Coast Armor Trim Smithing Template");
    }

}
