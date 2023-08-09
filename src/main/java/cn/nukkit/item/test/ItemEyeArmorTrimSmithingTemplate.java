package cn.nukkit.item.test;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemEyeArmorTrimSmithingTemplate extends Item {

    public ItemEyeArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemEyeArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemEyeArmorTrimSmithingTemplate(Integer meta, int count) {
        super(EYE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Eye Armor Trim Smithing Template");
    }
}
