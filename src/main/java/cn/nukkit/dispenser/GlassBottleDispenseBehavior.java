package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;


public class GlassBottleDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        if (target instanceof BlockFlowingWater w && w.getLiquidDepth() == 0)
            return Item.get(Item.POTION);
        return super.dispense(block, face, item);
    }
}
