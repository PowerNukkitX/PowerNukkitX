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

    public BlockEntityItemFrame(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
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
    public String getName() {
        return "Item Frame";
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock() instanceof BlockFrame;
    }

    public int getItemRotation() {
        return this.namedTag.getByte("ItemRotation");
    }

    public void setItemRotation(int itemRotation) {
        this.namedTag.putByte("ItemRotation", itemRotation);
        this.level.updateComparatorOutputLevel(this);
        this.setDirty();
    }

    public Item getItem() {
        CompoundTag NBTTag = this.namedTag.getCompound("Item");
        return NBTIO.getItemHelper(NBTTag);
    }

    public void setItem(Item item) {
        this.setItem(item, true);
    }

    public void setItem(Item item, boolean setChanged) {
        this.namedTag.putCompound("Item", NBTIO.putItemHelper(item));
        if (setChanged) {
            this.setDirty();
        } else this.level.updateComparatorOutputLevel(this);
    }

    public float getItemDropChance() {
        return this.namedTag.getFloat("ItemDropChance");
    }

    public void setItemDropChance(float chance) {
        this.namedTag.putFloat("ItemDropChance", chance);
    }

    @Override
    public void setDirty() {
        this.spawnToAll();
        super.setDirty();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (!this.namedTag.contains("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        Item item = getItem();
        CompoundTag tag = super.getSpawnCompound();

        if (!item.isNull()) {
            CompoundTag itemTag = NBTIO.putItemHelper(item);
            int networkDamage = item.getDamage();
            String namespacedId = item.getId();
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

    public int getAnalogOutput() {
        return this.getItem() == null || this.getItem().isNull() ? 0 : this.getItemRotation() % 8 + 1;
    }

    public boolean dropItem(Player player) {
        Item before = this.getItem();
        if (before == null || before.isNull()) {
            return false;
        }
        EntityItem drop = dropItemAndGetEntity(player);
        if (drop != null) {
            return true;
        }
        Item after = this.getItem();
        return after == null || after.isNull();
    }

    public @Nullable EntityItem dropItemAndGetEntity(@Nullable Player player) {
        Level level = getValidLevel();
        Item drop = getItem();
        if (drop.isNull()) {
            if (player != null) {
                spawnTo(player);
            }
            return null;
        }


        EntityItem itemEntity = null;
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
