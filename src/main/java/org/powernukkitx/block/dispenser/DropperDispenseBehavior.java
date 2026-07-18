package org.powernukkitx.block.dispenser;

import org.powernukkitx.block.Block;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;


public class DropperDispenseBehavior extends DefaultDispenseBehavior {


    public DropperDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (block.level.getBlockEntityIfLoaded(target) instanceof InventoryHolder) {
            InventoryHolder invHolder = (InventoryHolder) block.level.getBlockEntityIfLoaded(target);
            Inventory inv = invHolder.getInventory();
            Item clone = item.clone();
            clone.count = 1;

            if (inv.canAddItem(clone)) {
                inv.addItem(clone);
            } else {
                return clone;
            }
        } else {
            block.level.addSound(block, Sound.RANDOM_CLICK);
            return super.dispense(block, face, item);
        }
        return null;
    }
}
