package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemLog2 extends Item {
    public ItemLog2() {
        this(0, 1);
    }

    public ItemLog2(Integer meta) {
        this(meta, 1);
    }

    public ItemLog2(Integer meta, int count) {
        super(LOG2, meta, count);
        adjust();
    }

    public void adjust() {
        switch (getAux()) {
            case 0:
                this.name = "Acacia Log";
                this.block = Block.get(BlockID.ACACIA_LOG);
                return;
            case 1:
                this.name = "Dark Oak Log";
                this.block = Block.get(BlockID.DARK_OAK_LOG);
        }

    }
}