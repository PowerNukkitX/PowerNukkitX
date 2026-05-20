package cn.nukkit.block.dispenser;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public interface DispenseBehavior {


    Item dispense(BlockDispenser block, BlockFace face, Item item);

}
