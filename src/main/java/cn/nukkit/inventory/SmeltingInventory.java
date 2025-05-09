package cn.nukkit.inventory;

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
        //This wont call onSlotChange. Therefore no EXP will be dropped.
        this.setItemInternal(2, item);
        sendSlot(2, getViewers());
        return true;
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
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        if (index == 2 && (before.isNull() || before.getCount() > 0)) {
            var holder = getHolder();
            short xp = holder.calculateXpDrop();
            if (xp > 0) {
                holder.setStoredXP(0);
                holder.level.dropExpOrb(holder, xp);
            }
        }
        this.getHolder().scheduleUpdate();
    }
}
