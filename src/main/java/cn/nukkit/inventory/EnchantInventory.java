package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockEnchantingTable;
import cn.nukkit.blockentity.BlockEntityEnchantTable;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantInventory extends BlockTypeInventory {
    public EnchantInventory(BlockEntityEnchantTable blockEntityEnchantTable) {
        super(blockEntityEnchantTable, InventoryType.ENCHANT_TABLE);
    }

    @Override
    public BlockEntityEnchantTable getHolder() {
        return (BlockEntityEnchantTable) super.getHolder();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ENCHANT;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        Item[] drops = new Item[]{getItem(0), getItem(1)};
        drops = who.getInventory().addItem(drops);
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop);
            }
        }

        clear(0);
        clear(1);
        who.resetCraftingGridType();
    }

    public Item getInputSlot() {
        return this.getItem(0);
    }

    public Item getOutputSlot() {
        return this.getItem(0);
    }

    public Item getReagentSlot() {
        return this.getItem(1);
    }
}
