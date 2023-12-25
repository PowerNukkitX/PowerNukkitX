package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPotato extends ItemEdible {

    public ItemPotato() {
        this(0, 1);
    }

    public ItemPotato(Integer meta) {
        this(meta, 1);
    }

    public ItemPotato(Integer meta, int count) {
        super(POTATO, meta, count, "Potato");
        this.block = Block.get(BlockID.POTATOES);
    }
}
