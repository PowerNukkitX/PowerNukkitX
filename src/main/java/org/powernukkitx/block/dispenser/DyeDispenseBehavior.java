package org.powernukkitx.block.dispenser;

import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;


public class DyeDispenseBehavior extends DefaultDispenseBehavior {


    public DyeDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

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
