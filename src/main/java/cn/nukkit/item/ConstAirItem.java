package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class ConstAirItem extends Item {
    /**
     * @deprecated 
     */
    
    public ConstAirItem() {
        super("minecraft:air");
        this.meta = 0;
        this.count = 0;
        this.netId = 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getDamage() {
        return 0;
    }

    @Override
    public Integer getNetId() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getCount() {
        return 0;
    }

    @Override
    public Block getBlockUnsafe() {
        return new BlockAir();
    }

    @Override
    public @NotNull Block getBlock() {
        return  new BlockAir();
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void setCanDestroy(Block[] blocks) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setCanPlaceOn(Block[] blocks) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setNetId(Integer netId) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setCount(int count) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setItemLockMode(ItemLockMode mode) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setKeepOnDeath(boolean keepOnDeath) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isNull() {
        return true;
    }

    @Override
    public Item clone() {
        return this;
    }
}
