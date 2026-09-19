package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFrame;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.event.block.ItemFrameUseEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

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
        this.scheduleComparatorOutputUpdate();
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
        return Math.floorMod(Math.round(this.getNbt().getFloat("ItemRotation") / 45.0f), 8);
    }

    public void setItemRotation(int itemRotation) {
        int normalizedRotation = Math.floorMod(itemRotation, 8);
        this.nbt.putFloat("ItemRotation", normalizedRotation * 45.0f);
        this.level.updateComparatorOutputLevel(this);
        this.setDirty();
    }

    public Item getItem() {
        return ItemHelper.read(this.getNbt().getCompound("Item"));
    }

    public void setItem(Item item) {
        this.setItem(item, true);
    }

    public void setItem(Item item, boolean setChanged) {
        if (item == null || item.isNull()) {
            this.nbt.remove("Item");
        } else {
            this.nbt.putCompound("Item", ItemHelper.write(item));
        }

        if (setChanged) {
            this.setDirty();
        } else this.level.updateComparatorOutputLevel(this);
    }

    public float getItemDropChance() {
        return getNbt().containsFloat("ItemDropChance") ? getNbt().getFloat("ItemDropChance") : 1.0f;
    }

    public void setItemDropChance(float chance) {
        this.nbt.putFloat("ItemDropChance", chance);
    }

    @Override
    public void setDirty() {
        this.spawnToAll();
        super.setDirty();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (!this.nbt.contains("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        Item item = getItem();
        CompoundTag tag = super.getSpawnCompound();

        if (!item.isNull()) {
            CompoundTag builder = ItemHelper.write(item, null);
            int networkDamage = item.getDamage();
            String namespacedId = item.getId();
            if (namespacedId != null) {
                builder.remove("id");
                builder.putShort("Damage", (short) networkDamage);
                builder.putString("Name", namespacedId);
            }
            if (item instanceof ItemBlock) {
                builder.putCompound("Block", CompoundTag.fromNetwork(item.getBlockUnsafe().getBlockState().getBlockStateTag()));
            }
            tag.putCompound("Item", builder)
                    .putByte("ItemRotation", (byte) this.getItemRotation());
        }
        return tag;
    }

    public int getAnalogOutput() {
        Item item = this.getItem();
        return item == null || item.isNull() ? 0 : this.getItemRotation() % 8 + 1;
    }

    public boolean dropItem(Player player) {
        Item before = this.getItem();
        if (before == null || before.isNull()) {
            return false;
        }

        ItemFrameUseEvent event = new ItemFrameUseEvent(player, this.getBlock(), this, before, ItemFrameUseEvent.Action.DROP);
        this.getLevel().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

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
            EntityItem[] entities = level.dropItemAndGetEntities(add(0.5, 0.25, 0.5), drop);
            if (entities.length == 0) {
                if (player != null) {
                    spawnTo(player);
                }
                return null;
            } else itemEntity = entities[0];
        }

        setItem(Item.get(BlockID.AIR, 0, 1), true);
        setItemRotation(0);
        spawnToAll();
        level.addLevelEvent(this, LevelEvent.SOUND_ITEMFRAME_BREAK);

        return itemEntity;
    }
}
