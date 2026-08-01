package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.item.definition.ItemDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBlock extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder().build();

    public ItemBlock(Block block) {
        this(block, 0, 1, DEFINITION);
    }

    public ItemBlock(Block block, ItemDefinition definition) {
        this(block, 0, 1, definition);
    }

    public ItemBlock(Block block, int aux) {
        this(block, aux, 1, DEFINITION);
    }

    public ItemBlock(Block block, int aux, ItemDefinition definition) {
        this(block, aux, 1, definition);
    }

    public ItemBlock(Block block, int aux, int count) {
        this(block, aux, count, DEFINITION);
    }

    public ItemBlock(Block block, int aux, int count, ItemDefinition definition) {
        super(block, aux, count, block.getName(), true, definition);
    }

    @Override
    public void setDamage(int meta) {

    }

    @Override
    public ItemBlock clone() {
        ItemBlock block = (ItemBlock) super.clone();
        block.block = this.block.clone();
        return block;
    }

    @Override
    @NotNull
    public Block getBlock() {
        return this.block.clone();
    }

    @Override
    public boolean isLavaResistant() {
        return block.isLavaResistant();
    }
}
