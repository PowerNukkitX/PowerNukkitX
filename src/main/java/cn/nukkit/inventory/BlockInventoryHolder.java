package cn.nukkit.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.metadata.FixedMetadataValue;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.plugin.InternalPlugin;

import java.util.function.Supplier;

public interface BlockInventoryHolder extends InventoryHolder {
    String KEY = "inventory";


    Supplier<ContainerInventory> blockInventorySupplier();

    default Block getBlock() {
        return (Block) this;
    }

    @Override
    default Inventory getInventory() {
        return getOrCreateInventory();
    }

    default Inventory getOrCreateInventory() {
        Block block = getBlock();
        MetadataValue meta = block.getMetadata(KEY, InternalPlugin.INSTANCE);
        if (meta == null) {
            ContainerInventory containerInventory = blockInventorySupplier().get();
            block.setMetadata(KEY, new FixedMetadataValue(InternalPlugin.INSTANCE, containerInventory));
            return containerInventory;
        } else {
            return (ContainerInventory) meta.value();
        }
    }
}
