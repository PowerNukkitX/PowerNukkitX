package org.powernukkitx.item;

import org.powernukkitx.block.BlockBed;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.utils.DyeColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBed extends Item {
    public static final ItemDefinition DEFINITION = Item.DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemBed() {
        this(0, 1);
    }

    public ItemBed(Integer meta) {
        this(meta, 1);
    }

    public ItemBed(Integer meta, int count) {
        super(BlockID.BED, meta, count, DEFINITION);
    }

    public void internalAdjust() {
        name = DyeColor.getByWoolData(meta).getName() + " Bed";
        block = BlockBed.PROPERTIES.getDefaultState().toBlock();
    }
}
