package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract public class ItemArmor extends Item {
    public static final ItemDefinition ARMOR = DEFAULT_DEFINITION.toBuilder()
            .armor(true)
            .canTakeDamage(true)
            .maxStackSize(1)
            .build();

    public ItemArmor(String id) {
        this(id, 0, 1, (String) null, ARMOR);
    }

    public ItemArmor(String id, ItemDefinition definition) {
        this(id, 0, 1, (String) null, definition);
    }

    public ItemArmor(String id, Integer meta) {
        this(id, meta, 1, (String) null, ARMOR);
    }

    public ItemArmor(String id, Integer meta, ItemDefinition definition) {
        this(id, meta, 1, (String) null, definition);
    }

    public ItemArmor(String id, Integer meta, int count) {
        this(id, meta, count, (String) null, ARMOR);
    }

    public ItemArmor(String id, Integer meta, int count, ItemDefinition definition) {
        this(id, meta, count, (String) null, definition);
    }

    public ItemArmor(String id, Integer meta, int count, String name) {
        this(id, meta, count, name, ARMOR);
    }

    public ItemArmor(String id, Integer meta, int count, String name, ItemDefinition definition) {
        super(id, meta, count, name, definition);
    }

    @Override
    public int getEnchantAbility() {
        return switch (this.getTier()) {
            case WEARABLE_TIER_CHAIN -> 12;
            case WEARABLE_TIER_LEATHER, WEARABLE_TIER_NETHERITE -> 15;
            case WEARABLE_TIER_DIAMOND -> 10;
            case WEARABLE_TIER_GOLD -> 25;
            case WEARABLE_TIER_IRON -> 9;
            default -> 0;
        };
    }

    @Override
    public boolean isUnbreakable() {
        final Object tag = this.getNbtEntry("Unbreakable");
        return tag instanceof Byte b && b > 0;
    }
}
