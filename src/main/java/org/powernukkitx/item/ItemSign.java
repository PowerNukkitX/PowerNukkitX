package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * Make sure to keep the mapping between the sign and standing_sign blocks correct: the item is specified via this.block and the block via toItem.
 */
public abstract class ItemSign extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(16)
            .build();

    protected ItemSign(String id) {
        this(id, DEFINITION);
    }

    protected ItemSign(String id, ItemDefinition definition) {
        super(id, definition);
        if (id.equals(DARK_OAK_SIGN)) {
            this.block = Block.get(BlockID.DARKOAK_STANDING_SIGN);
        } else if (id.equals(OAK_SIGN)) {
            this.block = Block.get(BlockID.STANDING_SIGN);
        } else {
            this.block = Block.get(id.replace("_sign", "_standing_sign"));
        }
    }
}
