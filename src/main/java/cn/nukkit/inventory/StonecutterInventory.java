package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockStonecutter;
import cn.nukkit.block.BlockStonecutterBlock;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;


public class StonecutterInventory extends BlockTypeInventory {

    public StonecutterInventory(BlockStonecutterBlock block) {
        super(block, InventoryType.STONECUTTER);
    }

    @Override
    public BlockStonecutterBlock getHolder() {
        return (BlockStonecutterBlock) super.getHolder();
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;

        Item[] drops = who.getInventory().addItem(this.getItem(0));
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop);
            }
        }

        this.clear(0);
        who.resetCraftingGridType();
    }
}
