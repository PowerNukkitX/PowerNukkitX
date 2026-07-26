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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
        Optional<Item> item = getInventory().getContents().values().stream().findFirst();
        if (item.isPresent()) {
            Item instance = item.get();
            getInventory().remove(instance);
            player.dropItem(instance);
            getInventory().sendContents(player);
            getLevel().addSound(getVector3(), Sound.BUNDLE_DROP_CONTENTS);
            player.getInventory().setItemInMainHand(this);
            return true;
        } else return false;
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

