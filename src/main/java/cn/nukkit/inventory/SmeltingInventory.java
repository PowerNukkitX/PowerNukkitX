package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

public abstract class SmeltingInventory extends ContainerInventory {

    public final static int SLOT_SMELTING = 0;
    public final static int SLOT_FUEL = 1;
    public final static int SLOT_RESULT = 2;
    public SmeltingInventory(InventoryHolder holder, ContainerType type, int size) {
        super(holder, type, size);
    }

    public Item getResult() {
        return this.getItem(SLOT_RESULT);
    }

    public Item getFuel() {
        return this.getItem(SLOT_FUEL);
    }

    public Item getSmelting() {
        return this.getItem(SLOT_SMELTING);
    }

    public boolean setResult(Item item) {
        //This wont call onSlotChange. Therefore no EXP will be dropped.
        this.setItemInternal(2, item);
        sendSlot(2, getViewers());
        return true;
    }

    public boolean setFuel(Item item) {
        return this.setItem(SLOT_FUEL, item);
    }

    public boolean setSmelting(Item item) {
        return this.setItem(SLOT_SMELTING, item);
    }

    @Override
    public BlockEntityFurnace getHolder() {
        return (BlockEntityFurnace) this.holder;
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        this.getHolder().scheduleUpdate();
    }
}
