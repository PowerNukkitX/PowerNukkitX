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
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
@Slf4j
public class BlockEntityItemFrame extends BlockEntitySpawnable {

    public BlockEntityItemFrame(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        NbtMapBuilder builder = this.namedTag.toBuilder();
        if (!namedTag.containsKey("Item")) {
            // [ITEM_DEBUG] Log when a frame loads without an Item tag (new or corrupted)
            log.debug("[ITEM_DEBUG] ItemFrame at {},{},{} loadNBT: no 'Item' tag present, initializing to AIR. namedTag keys: {}",
                    (int) x, (int) y, (int) z, namedTag.keySet());
            builder.putCompound("Item", ItemHelper.write(new ItemBlock(Block.get(BlockID.AIR)), null));
        } else {
            // [ITEM_DEBUG] Log what item is loaded from NBT
            Item loaded = ItemHelper.read(namedTag.getCompound("Item"));
            if (loaded != null && !loaded.isNull()) {
                log.debug("[ITEM_DEBUG] ItemFrame at {},{},{} loadNBT: loaded item {} x{}",
                        (int) x, (int) y, (int) z, loaded.getId(), loaded.getCount());
            }
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
        // [ITEM_DEBUG] Log every item change with caller info
        Item oldItem = getItem();
        boolean wasNonEmpty = oldItem != null && !oldItem.isNull();
        boolean isNowEmpty = item == null || item.isNull();
        if (wasNonEmpty || !isNowEmpty) {
            String caller = getCallerInfo();
            log.debug("[ITEM_DEBUG] ItemFrame at {},{},{} setItem: {} x{} -> {} x{}, setChanged={}, caller: {}",
                    (int) x, (int) y, (int) z,
                    wasNonEmpty ? oldItem.getId() : "AIR", wasNonEmpty ? oldItem.getCount() : 0,
                    isNowEmpty ? "AIR" : item.getId(), isNowEmpty ? 0 : item.getCount(),
                    setChanged, caller);
        }

        this.namedTag = this.namedTag.toBuilder().putCompound("Item", ItemHelper.write(item)).build();
        if (setChanged) {
            this.setDirty();
        } else this.level.updateComparatorOutputLevel(this);
    }

    /**
     * [ITEM_DEBUG] Helper to get a meaningful caller from the stack trace
     */
    private static String getCallerInfo() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // skip getStackTrace, getCallerInfo, setItem (possibly 2 overloads)
        for (int i = 3; i < Math.min(stack.length, 8); i++) {
            String cls = stack[i].getClassName();
            if (!cls.contains("BlockEntityItemFrame")) {
                return stack[i].toString();
            }
        }
        return stack.length > 3 ? stack[3].toString() : "unknown";
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
    public void close() {
        // [ITEM_DEBUG] Log when a frame is closed while still holding an item — this is the key diagnostic
        if (!this.closed) {
            Item item = null;
            try {
                item = getItem();
            } catch (Exception ignored) {
            }
            if (item != null && !item.isNull()) {
                log.debug("[ITEM_DEBUG] ItemFrame at {},{},{} CLOSE with item still present: {} x{}. Stack trace:",
                        (int) x, (int) y, (int) z, item.getId(), item.getCount(),
                        new Throwable("ItemFrame close trace"));
            }
        }
        super.close();
    }

    @Override
    public NbtMap getSpawnCompound() {
        if (!this.namedTag.containsKey("Item")) {
            // [ITEM_DEBUG] This should not normally happen — log it
            log.debug("[ITEM_DEBUG] ItemFrame at {},{},{} getSpawnCompound: 'Item' tag missing from namedTag, resetting to AIR",
                    (int) x, (int) y, (int) z);
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
            itemEntity = level.dropAndGetItem(add(0.5, 0.25, 0.5), drop);
            if (itemEntity == null) {
                // [ITEM_DEBUG] dropAndGetItem returned null — item was NOT spawned
                log.debug("[ITEM_DEBUG] ItemFrame at {},{},{} dropItemAndGetEntity: dropAndGetItem returned null for {} x{}, item NOT removed from frame",
                        (int) x, (int) y, (int) z, drop.getId(), drop.getCount());
                if (player != null) {
                    spawnTo(player);
                }
                return null;
            }
        } else {
            // [ITEM_DEBUG] Drop chance failed
            log.debug("[ITEM_DEBUG] ItemFrame at {},{},{} dropItemAndGetEntity: drop chance failed (chance={}), item {} x{} destroyed",
                    (int) x, (int) y, (int) z, this.getItemDropChance(), drop.getId(), drop.getCount());
        }

        setItem(Item.get(BlockID.AIR, 0, 1), true);
        setItemRotation(0);
        spawnToAll();
        level.addLevelEvent(this, LevelEvent.SOUND_ITEMFRAME_BREAK);

        return itemEntity;
    }
}
