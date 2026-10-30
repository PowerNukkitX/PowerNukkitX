package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.inventory.BundleInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemBundle extends Item implements StorageItem {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    @Getter
    private Inventory holder;
    private BundleInventory inventory;

    public ItemBundle() {
        this(BUNDLE, DEFINITION);
    }

    public ItemBundle(ItemDefinition definition) {
        this(BUNDLE, definition);
    }

    public ItemBundle(String id) {
        this(id, DEFINITION);
    }

    public ItemBundle(String id, ItemDefinition definition) {
        super(id, definition);
    }

    @Override
    public void onChange(Inventory inventory) {
        StorageItem.super.onChange(inventory);
        this.holder = inventory;
        if (holder == null || holder != inventory.getHolder()) {
            for (Player player : inventory.getViewers()) {
                getInventory().sendContents(player);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        if(inventory == null) {
            CompoundTag tag;
            inventory = new BundleInventory(this);
            tag = this.getNbt();
            this.setNbt(tag);
        }
        if(inventory.getHolder() != this) inventory.setHolder(this);
        return inventory;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return dropStoredItem(player);
    }
}
