package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.inventory.BundleInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
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
        if(holder == null || holder != inventory.getHolder()) {
            for(Player player : inventory.getViewers()) {
                getInventory().sendContents(player);
            }
        }
    }

    public int getBundleId() {
        return getNamedTag().getInt("bundle_id");
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
            tag = this.getNamedTag();
            this.setNamedTag(tag);
        }
        if(inventory.getHolder() != this) inventory.setHolder(this);
        return inventory;
    }

    public void saveNBT() {
        CompoundTag tag = this.getNamedTag();
        ListTag<CompoundTag> items = new ListTag<>(Tag.TAG_Compound);
        for(var entry : getInventory().getContents().entrySet()) {
            items.add(entry.getKey(), NBTIO.putItemHelper(entry.getValue(), entry.getKey()));
        }
        tag.putList("storage_item_component_content", items);
        this.setNamedTag(tag);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        Optional<Item> item = getInventory().getContents().values().stream().findFirst();
        if(item.isPresent()) {
            Item instance = item.get();
            getInventory().remove(instance);
            player.dropItem(instance);
            getInventory().sendContents(player);
            getLevel().addSound(getVector3(), Sound.BUNDLE_DROP_CONTENTS);
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

