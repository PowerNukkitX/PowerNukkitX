package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFrame;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.block.ItemFrameUseEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
public class BlockEntityItemFrame extends BlockEntitySpawnable {

    public BlockEntityItemFrame(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        NbtMapBuilder builder = this.namedTag.toBuilder();
        if (!namedTag.containsKey("Item")) {
            builder.putCompound("Item", ItemHelper.write(new ItemBlock(Block.get(BlockID.AIR)), null));
        }
        if (!namedTag.containsKey("ItemRotation")) {
            builder.putByte("ItemRotation", (byte) 0);
        }
        if (!namedTag.containsKey("ItemDropChance")) {
            builder.putFloat("ItemDropChance", 1.0f);
        }
        this.namedTag = builder.build();
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
        this.namedTag = this.namedTag.toBuilder().putByte("ItemRotation", (byte) itemRotation).build();
        this.level.updateComparatorOutputLevel(this);
        this.setDirty();
    }

    public Item getItem() {
        return ItemHelper.read(this.namedTag.getCompound("Item"));
    }

    public void setItem(Item item) {
        this.setItem(item, true);
    }

    public void setItem(Item item, boolean setChanged) {
        this.namedTag = this.namedTag.toBuilder().putCompound("Item", ItemHelper.write(item, null)).build();
        if (setChanged) {
            this.setDirty();
        } else this.level.updateComparatorOutputLevel(this);
    }

    public float getItemDropChance() {
        return this.namedTag.getFloat("ItemDropChance");
    }

    public void setItemDropChance(float chance) {
        this.namedTag = this.namedTag.toBuilder().putFloat("ItemDropChance", chance).build();
    }

    @Override
    public void setDirty() {
        this.spawnToAll();
        super.setDirty();
    }

    @Override
    public NbtMap getSpawnCompound() {
        if (!this.namedTag.containsKey("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        Item item = getItem();
        NbtMapBuilder tag = super.getSpawnCompound().toBuilder();

        if (!item.isNull()) {
            NbtMapBuilder builder = ItemHelper.write(item, null).toBuilder();
            int networkDamage = item.getDamage();
            String namespacedId = item.getId();
            if (namespacedId != null) {
                builder.remove("id");
                builder.putShort("Damage", (short) networkDamage);
                builder.putString("Name", namespacedId);
            }
            if (item.isBlock()) {
                builder.putCompound("Block", item.getBlockUnsafe().getBlockState().getBlockStateTag());
            }
            tag.putCompound("Item", builder.build())
                    .putByte("ItemRotation", (byte) this.getItemRotation());
        }
        return tag.build();
    }

    public int getAnalogOutput() {
        return this.getItem() == null || this.getItem().isNull() ? 0 : this.getItemRotation() % 8 + 1;
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
        level.addLevelEvent(this, LevelEvent.SOUND_ITEMFRAME_BREAK);

        return itemEntity;
    }
}
