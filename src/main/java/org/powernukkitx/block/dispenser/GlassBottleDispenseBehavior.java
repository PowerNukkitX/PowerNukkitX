package org.powernukkitx.block.dispenser;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;


public class GlassBottleDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        if (target instanceof BlockFlowingWater w && w.getLiquidDepth() == 0)
            return Item.get(Item.POTION);
        return super.dispense(block, face, item);
    }
}
