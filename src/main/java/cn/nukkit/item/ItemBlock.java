package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBlock extends Item {
    /**
     * @deprecated 
     */
    
    public ItemBlock(Block block) {
        this(block, 0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBlock(Block block, int aux) {
        this(block, aux, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBlock(Block block, int aux, int count) {
        super(block, aux, count, block.getName(), true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
        if (meta != 0) {
            $1nt $1 = Registries.BLOCKSTATE_ITEMMETA.get(block.getId(), meta);
            if (i != 0) {
                BlockState $2 = Registries.BLOCKSTATE.get(i);
                this.block = Registries.BLOCK.get(blockState);
            }
        }
    }

    @Override
    public ItemBlock clone() {
        ItemBlock $3 = (ItemBlock) super.clone();
        block.block = this.block.clone();
        return block;
    }

    @Override
    @NotNull
    public Block getBlock() {
        return this.block.clone();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return block.isLavaResistant();
    }
}
