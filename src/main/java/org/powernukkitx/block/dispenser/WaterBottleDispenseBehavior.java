package org.powernukkitx.block.dispenser;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemPotion;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.entity.effect.PotionType;


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
