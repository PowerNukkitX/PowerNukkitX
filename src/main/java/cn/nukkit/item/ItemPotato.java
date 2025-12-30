package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPotato extends ItemFood {

    public ItemPotato() {
        this(POTATO, 0, 1, "Potato");
    }

    public ItemPotato(Integer meta) {
        this(POTATO, meta, 1,"Potato");
    }

    public ItemPotato(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
        this.block = Block.get(BlockID.POTATOES);
    }

    @Override
    public int getNutrition() {
        return 1;
    }

    @Override
    public float getSaturation() {
        return 0.6F;
    }
}
