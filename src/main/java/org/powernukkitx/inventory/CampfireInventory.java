package org.powernukkitx.inventory;


import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntityCampfire;
import org.powernukkitx.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;


public class CampfireInventory extends ContainerInventory {
    public CampfireInventory(BlockEntityCampfire campfire) {
        super(campfire, ContainerType.NONE, 4);
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
    public boolean open(Player who) {
        return true;
    }

    @Override
    public void close(Player who) {
    }

    @Override
    public void onOpen(Player who) {
    }

    @Override
    public void onClose(Player who) {
    }
}
