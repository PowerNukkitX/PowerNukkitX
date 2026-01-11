package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemPitcherPod extends Item {
    public ItemPitcherPod() {
        this(0, 1);
    }

    public ItemPitcherPod(Integer meta) { this(meta, 1); }

    public ItemPitcherPod(Integer meta, Integer count) {
        super(PITCHER_POD, 0, count, "Pitcher Pod");
        this.block = Block.get(BlockID.PITCHER_CROP);
    }
}