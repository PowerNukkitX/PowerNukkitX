package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Block component for custom items.
 * Defines which block the item places when used.
 */
public class BlockComponentItem implements ItemComponent {
    private String blockStateName = "";
    private int blockFacePlacement = 0;
    private int blockPlacement = 0;

    public BlockComponentItem() {
    }

    public BlockComponentItem(String blockStateName) {
        this.blockStateName = blockStateName != null ? blockStateName : "";
    }

    public BlockComponentItem blockStateName(String value) {
        this.blockStateName = value != null ? value : "";
        return this;
    }

    public BlockComponentItem blockFacePlacement(int value) {
        this.blockFacePlacement = value;
        return this;
    }

    public BlockComponentItem blockPlacement(int value) {
        this.blockPlacement = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.BLOCK;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putString("block_state_name", blockStateName)
                .putInt("block_face_placement", blockFacePlacement)
                .putInt("block_placement", blockPlacement);
    }
}