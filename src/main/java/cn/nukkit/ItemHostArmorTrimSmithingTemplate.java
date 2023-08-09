package cn.nukkit;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemHostArmorTrimSmithingTemplate extends Item {

    public ItemHostArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemHostArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemHostArmorTrimSmithingTemplate(Integer meta, int count) {
        super(HOST_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Host Armor Trim Smithing Template");
    }
}
