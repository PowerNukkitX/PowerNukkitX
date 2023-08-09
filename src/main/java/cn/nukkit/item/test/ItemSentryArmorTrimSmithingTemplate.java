package cn.nukkit.item.test;

import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public class ItemSentryArmorTrimSmithingTemplate extends Item {

    public ItemSentryArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSentryArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSentryArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Sentry Armor Trim Smithing Template");
    }
}
