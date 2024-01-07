package cn.nukkit.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.SoftFixedMetaValue;
import cn.nukkit.plugin.InternalPlugin;

import java.util.function.Supplier;

public interface BlockInventoryHolder extends InventoryHolder {
    String key = "inventory";

    Supplier<BlockTypeInventory> getBlockInventorySupplier();

    default void setInventoryMetaData(Block block) {
        block.setMetadata(key, new SoftFixedMetaValue(InternalPlugin.INSTANCE, getBlockInventorySupplier().get()));
    }

    default void removeInventoryMetaData(Block block) {
        if (block.hasMetadata(key, InternalPlugin.INSTANCE)) {
            block.removeMetadata(key, InternalPlugin.INSTANCE);
        }
    }

    default BlockTypeInventory getInventoryMetaData(Block block) {
        MetadataValue inventory = block.getMetadata(key, InternalPlugin.INSTANCE);
        if (inventory != null) {
            BlockTypeInventory value = (BlockTypeInventory) inventory.value();
            if (value == null) {
                value = getBlockInventorySupplier().get();
                block.setMetadata(key, new SoftFixedMetaValue(InternalPlugin.INSTANCE, value));
            }
            return value;
        }
        return null;
    }
}
