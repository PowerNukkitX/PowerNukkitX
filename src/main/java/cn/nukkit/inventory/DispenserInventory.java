package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockentity.BlockEntityDispenser;

@PowerNukkitOnly
public class DispenserInventory extends EjectableInventory {

    @PowerNukkitOnly
    public DispenserInventory(BlockEntityDispenser blockEntity) {
        super(blockEntity, InventoryType.DISPENSER);
    }

    @Override
    public BlockEntityDispenser getHolder() {
        return (BlockEntityDispenser) super.getHolder();
    }
}
