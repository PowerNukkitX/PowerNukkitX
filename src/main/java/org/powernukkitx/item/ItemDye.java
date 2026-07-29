package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.utils.DyeColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemDye extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder().build();

    public ItemDye() {
        this(0, 1, DEFINITION);
    }

    public ItemDye(ItemDefinition definition) {
        this(0, 1, definition);
    }

    public ItemDye(Integer meta) {
        this(meta, 1, DEFINITION);
    }

    public ItemDye(Integer meta, ItemDefinition definition) {
        this(meta, 1, definition);
    }

    public ItemDye(DyeColor dyeColor) {
        this(dyeColor.getItemDyeMeta(), 1, DEFINITION);
    }

    public ItemDye(DyeColor dyeColor, ItemDefinition definition) {
        this(dyeColor.getItemDyeMeta(), 1, definition);
    }

    public ItemDye(DyeColor dyeColor, int amount) {
        this(dyeColor.getItemDyeMeta(), amount, DEFINITION);
    }

    public ItemDye(DyeColor dyeColor, int amount, ItemDefinition definition) {
        this(dyeColor.getItemDyeMeta(), amount, definition);
    }

    public ItemDye(Integer meta, int amount) {
        this(meta, amount, DEFINITION);
    }

    public ItemDye(Integer meta, int amount, ItemDefinition definition) {
        super(DYE, meta, amount, meta <= 15 ? DyeColor.getByDyeData(meta).getDyeName() : DyeColor.getByDyeData(meta).getName() + " Dye", definition);
    }

    public ItemDye(String id) {
        this(id, DEFINITION);
    }

    public ItemDye(String id, ItemDefinition definition) {
        super(id, definition);
    }

    @Override
    public boolean isFertilizer() {
        return meta == 15;
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeData(meta);
    }
}
