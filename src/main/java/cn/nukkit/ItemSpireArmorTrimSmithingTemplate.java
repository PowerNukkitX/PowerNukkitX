package cn.nukkit;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemSpireArmorTrimSmithingTemplate extends Item {

    public ItemSpireArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSpireArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSpireArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Spire Armor Trim Smithing Template");
    }
}
