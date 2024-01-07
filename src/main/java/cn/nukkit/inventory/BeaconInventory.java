package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.block.BlockBeacon;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

/**
 * @author Rover656
 */
public class BeaconInventory extends BlockTypeInventory {
    public BeaconInventory(BlockEntityBeacon blockEntityBeacon) {
        super(blockEntityBeacon, InventoryType.BEACON);
    }

    @Override
    public BlockEntityBeacon getHolder() {
        return (BlockEntityBeacon) super.getHolder();
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        Item[] drops = who.getInventory().addItem(this.getItem(0));
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop);
            }
        }

        this.clear(0);
    }
}
