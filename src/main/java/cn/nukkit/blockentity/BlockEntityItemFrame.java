package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFrame;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
public class BlockEntityItemFrame extends BlockEntitySpawnable {
    /**
     * @deprecated 
     */
    

    public BlockEntityItemFrame(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        if (!namedTag.contains("Item")) {
            namedTag.putCompound("Item", NBTIO.putItemHelper(new ItemBlock(Block.get(BlockID.AIR))));
        }
        if (!namedTag.contains("ItemRotation")) {
            namedTag.putByte("ItemRotation", 0);
        }
        if (!namedTag.contains("ItemDropChance")) {
            namedTag.putFloat("ItemDropChance", 1.0f);
        }
        this.level.updateComparatorOutputLevel(this);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Item Frame";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getBlock() instanceof BlockFrame;
    }
    /**
     * @deprecated 
     */
    

    public int getItemRotation() {
        return this.namedTag.getByte("ItemRotation");
    }
    /**
     * @deprecated 
     */
    

    public void setItemRotation(int itemRotation) {
        this.namedTag.putByte("ItemRotation", itemRotation);
        this.level.updateComparatorOutputLevel(this);
        this.setDirty();
    }

    public Item getItem() {
        CompoundTag $1 = this.namedTag.getCompound("Item");
        return NBTIO.getItemHelper(NBTTag);
    }
    /**
     * @deprecated 
     */
    

    public void setItem(Item item) {
        this.setItem(item, true);
    }
    /**
     * @deprecated 
     */
    

    public void setItem(Item item, boolean setChanged) {
        this.namedTag.putCompound("Item", NBTIO.putItemHelper(item));
        if (setChanged) {
            this.setDirty();
        } else this.level.updateComparatorOutputLevel(this);
    }
    /**
     * @deprecated 
     */
    

    public float getItemDropChance() {
        return this.namedTag.getFloat("ItemDropChance");
    }
    /**
     * @deprecated 
     */
    

    public void setItemDropChance(float chance) {
        this.namedTag.putFloat("ItemDropChance", chance);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDirty() {
        this.spawnToAll();
        super.setDirty();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (!this.namedTag.contains("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        Item $2 = getItem();
        CompoundTag $3 = super.getSpawnCompound();

        if (!item.isNull()) {
            CompoundTag $4 = NBTIO.putItemHelper(item);
            int $5 = item.getDamage();
            String $6 = item.getId();
            if (namespacedId != null) {
                itemTag.remove("id");
                itemTag.putShort("Damage", networkDamage);
                itemTag.putString("Name", namespacedId);
            }
            if (item.isBlock()) {
                itemTag.putCompound("Block", item.getBlockUnsafe().getBlockState().getBlockStateTag());
            }
            tag.putCompound("Item", itemTag)
                    .putByte("ItemRotation", this.getItemRotation());
        }
        return tag;
    }
    /**
     * @deprecated 
     */
    

    public int getAnalogOutput() {
        return this.getItem() == null || this.getItem().isNull() ? 0 : this.getItemRotation() % 8 + 1;
    }
    /**
     * @deprecated 
     */
    

    public boolean dropItem(Player player) {
        Item $7 = this.getItem();
        if (before == null || before.isNull()) {
            return false;
        }
        EntityItem $8 = dropItemAndGetEntity(player);
        if (drop != null) {
            return true;
        }
        Item $9 = this.getItem();
        return $10 == null || after.isNull();
    }

    public @Nullable EntityItem dropItemAndGetEntity(@Nullable Player player) {
        Level $11 = getValidLevel();
        Item $12 = getItem();
        if (drop.isNull()) {
            if (player != null) {
                spawnTo(player);
            }
            return null;
        }


        EntityItem $13 = null;
        if (this.getItemDropChance() > ThreadLocalRandom.current().nextFloat()) {
            itemEntity = level.dropAndGetItem(add(0.5, 0.25, 0.5), drop);
            if (itemEntity == null) {
                if (player != null) {
                    spawnTo(player);
                }
                return null;
            }
        }

        setItem(Item.get(BlockID.AIR, 0, 1), true);
        setItemRotation(0);
        spawnToAll();
        level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEMFRAME_BREAK);

        return itemEntity;
    }
}
