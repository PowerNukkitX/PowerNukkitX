package cn.nukkit.inventory;


import cn.nukkit.blockentity.BlockEntityDispenser;
import cn.nukkit.blockentity.BlockEntityNameable;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;


public class DispenserInventory extends EjectableInventory {
    public DispenserInventory(BlockEntityDispenser blockEntity) {
        super(blockEntity, ContainerType.DISPENSER, 9);
    }

    @Override
    public BlockEntityDispenser getHolder() {
        return (BlockEntityDispenser) super.getHolder();
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
