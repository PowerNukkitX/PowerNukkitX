package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class ItemBlock extends Item {
    public ItemBlock(Block block) {
        this(block, 0, 1);
    }

    public ItemBlock(Block block, Integer aux) {
        this(block, aux, 1);
    }

    public ItemBlock(Block block, Integer aux, int count) {
        super(block.getItemId(), aux, count, block.getName());
        this.block = block;
    }

    @Override
    public void setDamage(int meta) {
        int i = Registries.BLOCKSTATE_ITEMMETA.get(block.getId(), meta);
        if (i != 0) {
            BlockState blockState = Registries.BLOCKSTATE.get(i);
            this.block = Registries.BLOCK.get(blockState);
        }
    }

    @Override
    public ItemBlock clone() {
        ItemBlock block = (ItemBlock) super.clone();
        block.block = this.block.clone();
        return block;
    }

    @Override
    @NotNull public Block getBlock() {
        return this.block.clone();
    }

    @Override
    public boolean isLavaResistant() {
        return block.isLavaResistant();
    }
}
