package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.inventory.BundleInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.ItemHelper;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * An item that stores other items inside it, as a bundle does. The stored items live in a
 * {@link BundleInventory} that the client addresses as a dynamic container by {@link #getBundleId()}, and
 * are saved into the item's NBT under {@code storage_item_component_content}.
 * <p>
 * The position methods report the position of whatever holds the inventory the item is in, so they may
 * only be called while the item sits in an inventory, after {@link #onChange(Inventory)} told it which.
 */
public interface StorageItem extends InventoryHolder, INBT {

    /**
     * @return the inventory this item currently sits in, or null before it was placed in one
     */
    @Nullable Inventory getHolder();

    /**
     * @return the item's NBT, created empty when it has none
     */
    CompoundTag getOrCreateNbt();

    /**
     * @return the item as sent to clients
     */
    ItemData toNetwork();

    /**
     * @return the total weight the stored items may reach; an item of a stack of 64 weighs 1
     */
    default int getMaxStorageWeight() {
        return 64;
    }

    /**
     * @return how many different stacks the item can store
     */
    default int getMaxStorageSlots() {
        return 64;
    }

    /**
     * @param item the stack about to be stored, never empty
     * @return whether the item accepts the stack; the weight limit is checked separately
     */
    default boolean canStore(@NotNull Item item) {
        return true;
    }

    /**
     * @return the identifier of the dynamic container clients use for this item's contents
     */
    default int getBundleId() {
        return getNbt().getInt("bundle_id");
    }

    /**
     * Writes the stored items into the item's NBT and marks the chunk of its holder changed.
     */
    default void saveNBT() {
        CompoundTag tag = this.getNbt();
        ListTag<CompoundTag> items = new ListTag<>(Tag.TAG_Compound);
        for (Map.Entry<Integer, Item> entry : getInventory().getContents().entrySet()) {
            items.add(ItemHelper.write(entry.getValue(), entry.getKey()));
        }
        tag.putList("storage_item_component_content", items);
        this.setNbt(tag);
        getLevel().getChunk((int) getX() >> 4, (int) getZ() >> 4).setChanged();
    }

    /**
     * Drops the first stored stack at the player, as using a bundle in the air does. The item must be the
     * one in the player's main hand.
     *
     * @return whether a stack was dropped
     */
    default boolean dropStoredItem(@NotNull Player player) {
        BundleInventory inventory = (BundleInventory) getInventory();
        int previousContentSize = storedContentSize(inventory);
        Optional<Map.Entry<Integer, Item>> item = inventory.getContents().entrySet().stream()
                .min(Map.Entry.comparingByKey());
        if (item.isEmpty()) {
            return false;
        }
        Item instance = item.get().getValue();
        inventory.clear(item.get().getKey(), false);
        player.dropItem(instance);
        player.getInventory().setItemInMainHand((Item) this, false);
        player.getInventory().sendSlot(player.getInventory().getHeldItemIndex(), player);
        clearRemovedSlots(player, inventory, previousContentSize);
        inventory.sendContents(player);
        getLevel().addSound(getVector3(), Sound.BUNDLE_DROP_CONTENTS);
        return true;
    }

    private static int storedContentSize(BundleInventory inventory) {
        return inventory.getContents().keySet().stream().mapToInt(Integer::intValue).max().orElse(-1) + 1;
    }

    /**
     * Sends air into the slots that emptied, because otherwise the client shows the last item in all
     * previous slots. This only applies when dropping items by using the item; inventory transactions
     * must not send these.
     */
    private void clearRemovedSlots(Player player, BundleInventory inventory, int previousContentSize) {
        int contentSize = storedContentSize(inventory);
        for (int slot = contentSize; slot < previousContentSize; slot++) {
            InventorySlotPacket packet = new InventorySlotPacket();
            packet.setContainerID(ContainerId.CONTAINER_ID_REGISTRY);
            packet.setSlot(slot);
            packet.setItem(Item.AIR.toNetwork());
            packet.setFullContainerName(new FullContainerName(ContainerEnumName.DYNAMIC_CONTAINER, getBundleId()));
            player.sendPacket(packet);
        }
    }

    @Override
    default Level getLevel() {
        return getHolder().getHolder().getLevel();
    }

    @Override
    default double getX() {
        return getHolder().getHolder().getX();
    }

    @Override
    default double getY() {
        return getHolder().getHolder().getY();
    }

    @Override
    default double getZ() {
        return getHolder().getHolder().getZ();
    }

    @Override
    default Vector3 getVector3() {
        return getHolder().getHolder().getVector3();
    }
}
