package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class AnvilInventory extends BlockTypeInventory {
    public AnvilInventory(BlockAnvil inventory) {
        super(inventory, InventoryType.ANVIL);
    }

    @Override
    public BlockAnvil getHolder() {
        return (BlockAnvil) super.getHolder();
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;

        Item[] drops = new Item[]{getInputSlot(), getMaterialSlot()};
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

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ANVIL;
    }

    public Item getInputSlot() {
        return this.getItem(0);
    }

    public Item getMaterialSlot() {
        return this.getItem(1);
    }

    public Item getOutputSlot() {
        return this.getItem(2);
    }

    public boolean setInputSlot(Item item) {
        return setInputSlot(item, true);
    }

    public boolean setInputSlot(Item item, boolean send) {
        return setItem(0, item, send);
    }

    public boolean setMaterialSlot(Item item) {
        return setMaterialSlot(item, true);
    }

    public boolean setMaterialSlot(Item item, boolean send) {
        return setItem(1, item, send);
    }

    public boolean setOutputSlot(Item item) {
        return setOutputSlot(item, true);
    }

    public boolean setOutputSlot(Item item, boolean send) {
        return setItem(2, item, send);
    }
}
