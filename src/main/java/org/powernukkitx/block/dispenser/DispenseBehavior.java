package org.powernukkitx.block.dispenser;

import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;

/**
 * @author CreeperFace
 */
public interface DispenseBehavior {


    Item dispense(BlockDispenser block, BlockFace face, Item item);

}
