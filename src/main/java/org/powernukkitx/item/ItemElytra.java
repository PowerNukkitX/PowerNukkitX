package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemElytra extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .chestplate(true)
            .maxDurability(433)
            .build();

    public ItemElytra() {
        this(0, 1);
    }

    public ItemElytra(Integer meta) {
        this(meta, 1);
    }

    public ItemElytra(Integer meta, int count) {
        super(ELYTRA, meta, count, "Elytra", DEFINITION);
    }

    @Override
    public boolean isArmor() {
        return false;
    }
}
