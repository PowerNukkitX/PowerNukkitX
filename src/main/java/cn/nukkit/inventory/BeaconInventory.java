package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.item.Item;

/**
 * @author Rover656
 */
public class BeaconInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    /**
     * @deprecated 
     */
    

    public BeaconInventory(BlockEntityBeacon blockBeacon) {
        super(blockBeacon, InventoryType.BEACON, 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onClose(Player who) {
        super.onClose(who);

        Item[] drops = who.getInventory().addItem(this.getItem(0));
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().getVector3().add(0.5, 0.5, 0.5), drop);
            }
        }

        this.clear(0);
    }

    @Override
    public BlockEntityBeacon getHolder() {
        return (BlockEntityBeacon) super.getHolder();
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
