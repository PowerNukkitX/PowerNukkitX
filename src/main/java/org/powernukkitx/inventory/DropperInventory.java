package org.powernukkitx.inventory;


import org.powernukkitx.blockentity.BlockEntityDropper;
import org.powernukkitx.blockentity.BlockEntityNameable;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;


public class DropperInventory extends EjectableInventory {


    public DropperInventory(BlockEntityDropper blockEntity) {
        super(blockEntity, ContainerType.DROPPER, 9);
    }

    @Override
    public BlockEntityDropper getHolder() {
        return (BlockEntityDropper) super.getHolder();
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
