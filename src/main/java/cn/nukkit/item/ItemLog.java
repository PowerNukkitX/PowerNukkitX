package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemLog extends Item {
    public ItemLog() {
        this(0, 1);
    }

    public ItemLog(Integer meta) {
        this(meta, 1);
    }

    public ItemLog(Integer meta, int count) {
        super(LOG, meta, count);
        adjust();
    }

    public void adjust() {
        switch (getAux()) {
            case 0:
                this.name = "Oak Log";
                this.block = Block.get(BlockID.OAK_LOG);
                return;
            case 1:
                this.name = "Spruce Log";
                this.block = Block.get(BlockID.SPRUCE_LOG);
                return;
            case 2:
                this.name = "Birch Log";
                this.block = Block.get(BlockID.BIRCH_LOG);
                return;
            case 3:
                this.name = "Jungle Log";
                this.block = Block.get(BlockID.JUNGLE_LOG);
        }
    }
}