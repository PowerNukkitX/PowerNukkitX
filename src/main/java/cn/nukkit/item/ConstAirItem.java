package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.nbt.tag.CompoundTag;

public class ConstAirItem extends Item {
    public ConstAirItem() {
        super("minecraft:air");
        this.block = new BlockAir();
        this.count = 0;
        this.netId = 0;
    }

    @Override
    public void setDamage(int meta) {
    }

    @Override
    public void setBlockUnsafe(Block block) {
    }

    @Override
    public Item setCompoundTag(byte[] tags) {
        return this;
    }

    @Override
    public Item setCompoundTag(CompoundTag tag) {
        return this;
    }

    @Override
    public Item setCustomBlockData(CompoundTag compoundTag) {
        return this;
    }

    @Override
    public Item setCustomName(String name) {
        return this;
    }

    @Override
    public Item setLore(String... lines) {
        return this;
    }

    @Override
    public Item setNamedTag(CompoundTag tag) {
        return this;
    }

    @Override
    public Item setRepairCost(int cost) {
        return this;
    }

    @Override
    public void setCanDestroy(Block[] blocks) {
    }

    @Override
    public void setCanPlaceOn(Block[] blocks) {
    }

    @Override
    public void setNetId(Integer netId) {
    }

    @Override
    public void setCount(int count) {
    }

    @Override
    public void setItemLockMode(ItemLockMode mode) {
    }

    @Override
    public void setKeepOnDeath(boolean keepOnDeath) {
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public Item clone() {
        return this;
    }
}
