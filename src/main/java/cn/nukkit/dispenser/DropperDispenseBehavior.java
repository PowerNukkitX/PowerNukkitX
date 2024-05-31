package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;


public class DropperDispenseBehavior extends DefaultDispenseBehavior {
    /**
     * @deprecated 
     */
    


    public DropperDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block $1 = block.getSide(face);

        if (block.level.getBlockEntityIfLoaded(target) instanceof InventoryHolder) {
            InventoryHolder $2 = (InventoryHolder) block.level.getBlockEntityIfLoaded(target);
            Inventory $3 = invHolder.getInventory();
            Item $4 = item.clone();
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
