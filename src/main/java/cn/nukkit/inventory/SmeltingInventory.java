package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.item.Item;

public abstract class SmeltingInventory extends ContainerInventory {
    public SmeltingInventory(InventoryHolder holder, InventoryType type, int size) {
        super(holder, type, size);
    }

    public Item getResult() {
        return this.getItem(2);
    }

    public Item getFuel() {
        return this.getItem(1);
    }

    public Item getSmelting() {
        return this.getItem(0);
    }

    public boolean setResult(Item item) {
        return this.setItem(2, item);
    }

    public boolean setFuel(Item item) {
        return this.setItem(1, item);
    }

    public boolean setSmelting(Item item) {
        return this.setItem(0, item);
    }

    @Override
    public BlockEntityFurnace getHolder() {
        return (BlockEntityFurnace) this.holder;
    }

    @Override
    public boolean setItemByPlayer(Player player, int index, Item item, boolean send) {
        if (index == 2 && (item.isNull() || item.getCount() == 0)) {
            var holder = getHolder();
            var xp = holder.calculateXpDrop();
            if (xp > 0) {
                holder.setStoredXP(0);
                holder.level.dropExpOrb(player, xp);
            }
        }
        return setItem(index, item, send);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().scheduleUpdate();
    }
}
