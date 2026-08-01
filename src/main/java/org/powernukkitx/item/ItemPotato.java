package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPotato extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(1)
            .saturation(0.6f)
            .build();

    public ItemPotato() {
        this(POTATO, 0, 1, "Potato", DEFINITION);
    }

    public ItemPotato(ItemDefinition definition) {
        this(POTATO, 0, 1, "Potato", definition);
    }

    public ItemPotato(Integer meta) {
        this(POTATO, meta, 1, "Potato", DEFINITION);
    }

    public ItemPotato(Integer meta, ItemDefinition definition) {
        this(POTATO, meta, 1, "Potato", definition);
    }

    public ItemPotato(String id, Integer meta, int count, String name) {
        this(id, meta, count, name, DEFINITION);
    }

    public ItemPotato(String id, Integer meta, int count, String name, ItemDefinition definition) {
        super(id, meta, count, name, definition);
        this.block = Block.get(BlockID.POTATOES);
    }
}
