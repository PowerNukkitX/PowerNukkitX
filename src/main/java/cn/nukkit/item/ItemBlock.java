package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBlock extends Item {
    public ItemBlock(Block block) {
        this(block, 0, 1);
    }

    public ItemBlock(Block block, int aux) {
        this(block, aux, 1);
    }

    public ItemBlock(Block block, int aux, int count) {
        super(block, aux, count, block.getName(), true);
    }

    @Override
    public void setDamage(int meta) {
        if (meta != 0) {
            int i = Registries.BLOCKSTATE_ITEMMETA.get(block.getId(), meta);
            if (i != 0) {
                BlockState blockState = Registries.BLOCKSTATE.get(i);
                this.block = Registries.BLOCK.get(blockState);
            }
        }
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
