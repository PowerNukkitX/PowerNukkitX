package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.math.BlockFace;
import cn.nukkit.entity.effect.PotionType;


public class WaterBottleDispenseBehavior extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        if (((ItemPotion) item).getPotion() != PotionType.WATER)
            return super.dispense(block, face, item);
        var targetId = block.getSide(face).getId();
        if (targetId == BlockID.DIRT || targetId == BlockID.DIRT_WITH_ROOTS) {
            block.level.setBlock(block.getSideVec(face), Block.get(BlockID.MUD));
            return null;
        }
        return super.dispense(block, face, item);
    }
}
