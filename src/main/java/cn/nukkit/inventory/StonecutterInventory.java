package cn.nukkit.inventory;

import cn.nukkit.Player;

import cn.nukkit.block.BlockSmithingTable;
import cn.nukkit.block.BlockStonecutterBlock;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;


public class StonecutterInventory extends ContainerInventory {


    public StonecutterInventory(BlockStonecutterBlock blockStonecutterBlock) {
        super(blockStonecutterBlock, InventoryType.STONECUTTER, 3);
    }

    @Override
    public BlockStonecutterBlock getHolder() {
        return (BlockStonecutterBlock) super.getHolder();
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        Item[] drops = who.getInventory().addItem(this.getItem(0));
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().getVector3().add(0.5, 0.5, 0.5), drop);
            }
        }

        this.clear(0);
        who.resetCraftingGridType();
    }
}
