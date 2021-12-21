package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public class CampfireInventory extends ContainerInventory {

    @PowerNukkitOnly
    public CampfireInventory(BlockEntityCampfire campfire) {
        super(campfire, InventoryType.CAMPFIRE);
    }

    @PowerNukkitOnly
    public CampfireInventory(BlockEntityCampfire furnace, InventoryType inventoryType) {
        super(furnace, inventoryType);
    }

    @Override
    public BlockEntityCampfire getHolder() {
        return (BlockEntityCampfire) this.holder;
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().scheduleUpdate();
        this.getHolder().spawnToAll();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canAddItem(Item item) {
        return super.canAddItem(item);
    }
}
