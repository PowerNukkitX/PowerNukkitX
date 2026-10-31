package org.powernukkitx.item.customitem;

import org.powernukkitx.Player;
import org.powernukkitx.inventory.BundleInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.StorageItem;
import org.powernukkitx.math.Vector3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A pack item with {@code minecraft:storage_item}, which stores items as a bundle does within the limits
 * the pack sets.
 */
public class ItemDataDrivenStorage extends ItemDataDriven implements StorageItem {

    private @Nullable Inventory holder;
    private @Nullable BundleInventory inventory;

    /**
     * @param serverComponents the server behaviour, whose {@link ServerComponents#storage()} must be set
     */
    public ItemDataDrivenStorage(@NotNull String id, @Nullable CustomItemDefinition definition,
                                 @NotNull ServerComponents serverComponents) {
        super(id, definition, serverComponents);
    }

    @Override
    public @Nullable Inventory getHolder() {
        return holder;
    }

    @Override
    public void onChange(Inventory inventory) {
        StorageItem.super.onChange(inventory);
        this.holder = inventory;
        for (Player player : inventory.getViewers()) {
            getInventory().sendContents(player);
        }
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = new BundleInventory(this);
        }
        if (inventory.getHolder() != this) {
            inventory.setHolder(this);
        }
        return inventory;
    }

    @Override
    public int getMaxStorageWeight() {
        return getServerComponents().storage().maxWeight();
    }

    @Override
    public int getMaxStorageSlots() {
        return getServerComponents().storage().maxSlots();
    }

    @Override
    public boolean canStore(@NotNull Item item) {
        Storage storage = getServerComponents().storage();
        if (item instanceof StorageItem && !storage.allowNested()) {
            return false;
        }
        if (storage.bannedItems().contains(item.getId())) {
            return false;
        }
        return storage.allowedItems().isEmpty() || storage.allowedItems().contains(item.getId());
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return dropStoredItem(player);
    }

    @Override
    public ItemDataDrivenStorage clone() {
        return (ItemDataDrivenStorage) super.clone();
    }
}
