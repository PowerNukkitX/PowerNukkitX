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
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class ItemBundle extends Item implements INBT, InventoryHolder {

    @Getter
    private Inventory holder;
    private BundleInventory inventory;

    public ItemBundle() {
        this(BUNDLE);
    }

    public ItemBundle(String id) {
        super(id);
    }

    @Override
    public void onChange(Inventory inventory) {
        INBT.super.onChange(inventory);
        this.holder = inventory;
        if (holder == null || holder != inventory.getHolder()) {
            for (Player player : inventory.getViewers()) {
                getInventory().sendContents(player);
            }
        }
    }

    public int getBundleId() {
        return getNbt().getInt("bundle_id");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
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

    public void saveNBT() {
        CompoundTag tag = this.getNbt();
        ListTag<CompoundTag> items = new ListTag<>(Tag.TAG_Compound);
        for(var entry : getInventory().getContents().entrySet()) {
            items.add(ItemHelper.write(entry.getValue(), entry.getKey()));
        }
        tag.putList("storage_item_component_content", items);
        this.setNbt(tag);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        BundleInventory inventory = (BundleInventory) getInventory();
        int previousContentSize = getContentSize(inventory);
        Optional<Map.Entry<Integer, Item>> item = inventory.getContents().entrySet().stream()
                .min(Map.Entry.comparingByKey());
        if (item.isPresent()) {
            Item instance = item.get().getValue();
            inventory.clear(item.get().getKey(), false);
            player.dropItem(instance);
            player.getInventory().setItemInMainHand(this, false);
            player.getInventory().sendSlot(player.getInventory().getHeldItemIndex(), player);
            clearRemovedSlots(player, inventory, previousContentSize);
            inventory.sendContents(player);
            getLevel().addSound(getVector3(), Sound.BUNDLE_DROP_CONTENTS);
            return true;
        } else return false;
    }

    private int getContentSize(BundleInventory inventory) {
        return inventory.getContents().keySet().stream().mapToInt(Integer::intValue).max().orElse(-1) + 1;
    }

    /*
     * Sends an AIR Item first, because otherwise the client shows the last item in all previous slots for some reason.
     * This only applies when dropping items by clicking the bundle.
     * Inventory Transactions don't need those. (It would break if sent there)
     */
    private void clearRemovedSlots(Player player, BundleInventory inventory, int previousContentSize) {
        int contentSize = getContentSize(inventory);
        for (int slot = contentSize; slot < previousContentSize; slot++) {
            InventorySlotPacket packet = new InventorySlotPacket();
            packet.setContainerID(ContainerId.CONTAINER_ID_REGISTRY);
            packet.setSlot(slot);
            packet.setItem(Item.AIR.toNetwork());
            packet.setFullContainerName(
                    new FullContainerName(ContainerEnumName.DYNAMIC_CONTAINER, getBundleId())
            );
            player.sendPacket(packet);
        }
    }

    @Override
    public Level getLevel() {
        return holder.getHolder().getLevel();
    }

    @Override
    public double getX() {
        return holder.getHolder().getX();
    }

    @Override
    public double getY() {
        return holder.getHolder().getY();
    }

    @Override
    public double getZ() {
        return holder.getHolder().getZ();
    }

    @Override
    public Vector3 getVector3() {
        return holder.getHolder().getVector3();
    }
}
