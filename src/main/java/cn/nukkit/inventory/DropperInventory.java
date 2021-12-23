package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockentity.BlockEntityDropper;

@PowerNukkitOnly
public class DropperInventory extends EjectableInventory {

    @PowerNukkitOnly
    public DropperInventory(BlockEntityDropper blockEntity) {
        super(blockEntity, InventoryType.DROPPER);
    }

    @Override
    public BlockEntityDropper getHolder() {
        return (BlockEntityDropper) super.getHolder();
    }
}
