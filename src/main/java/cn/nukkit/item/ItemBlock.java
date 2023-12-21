package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.registry.Registries;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class ItemBlock extends Item {
    public ItemBlock(Block block) {
        this(block, 0, 1);
    }

    public ItemBlock(Block block, Integer meta) {
        this(block, meta, 1);
    }

    public ItemBlock(Block block, Integer meta, int count) {
        super(block.getItemId(), meta, count, block.getName());
        this.block = block;
    }

    @Override
    public void setMeta(Integer meta) {
        int i = Registries.BLOCKSTATE_ITEMMETA.get(block.getId(), meta);
        if (i == 0) {
            throw new IllegalArgumentException("Unknown meta mapping in block: " + block.getId());
        }
        BlockState blockState = Registries.BLOCKSTATE.get(i);
        this.block = Registries.BLOCK.get(blockState);
    }

    @Override
    public ItemBlock clone() {
        ItemBlock block = (ItemBlock) super.clone();
        block.block = this.block.clone();
        return block;
    }

    @NotNull
    @Override
    public Block getBlock() {
        return this.block.clone();
    }


    @Override
    public boolean isLavaResistant() {
        return block.isLavaResistant();
    }
}
