package cn.nukkit;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemTideArmorTrimSmithingTemplate extends Item {

    public ItemTideArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemTideArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemTideArmorTrimSmithingTemplate(Integer meta, int count) {
        super(TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Tide Armor Trim Smithing Template");
    }
}
