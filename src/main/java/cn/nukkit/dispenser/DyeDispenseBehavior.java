package cn.nukkit.dispenser;

import cn.nukkit.block.*;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;


public class DyeDispenseBehavior extends DefaultDispenseBehavior {
    /**
     * @deprecated 
     */
    


    public DyeDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block $1 = block.getSide(face);

        if (item.isFertilizer()) {
            if (target.isFertilizable()) {
                target.onActivate(item, null,face, 0, 0, 0);
            } else {
                this.success = false;
            }

            return null;
        }

        return super.dispense(block, face, item);
    }

}
