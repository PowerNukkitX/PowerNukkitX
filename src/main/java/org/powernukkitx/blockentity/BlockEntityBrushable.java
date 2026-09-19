package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;

/**
 * @author Buddelbubi
 * @since 2026/03/31
 */
public class BlockEntityBrushable extends BlockEntitySpawnable {
    public static final int BRUSH_COUNT_COMPLETE = 10;
    public static final byte BRUSH_DIRECTION_NONE = 6;

    public BlockEntityBrushable(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.SUSPICIOUS_GRAVEL || getBlock().getId() == Block.SUSPICIOUS_SAND;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
    }

    /**
     * Returns the persisted brush count, clamped to the valid range.
     *
     * @return brush count
     */
    public int getBrushCount() {
        return Math.max(0, Math.min(BRUSH_COUNT_COMPLETE, this.nbt.getInt("brush_count")));
    }

    /**
     * Sets the persisted brush count.
     *
     * @param brushCount brush count
     */
    public void setBrushCount(int brushCount) {
        this.nbt.putInt("brush_count", Math.max(0, Math.min(BRUSH_COUNT_COMPLETE, brushCount)));
        this.setDirty();
    }

    /**
     * Returns the persisted brush direction.
     *
     * @return brush direction index
     */
    public byte getBrushDirection() {
        return this.nbt.getByte("brush_direction");
    }

    /**
     * Sets the persisted brush direction.
     *
     * @param face brush direction, or {@code null} for no active direction
     */
    public void setBrushDirection(BlockFace face) {
        this.nbt.putByte("brush_direction", face != null ? (byte) face.getIndex() : BRUSH_DIRECTION_NONE);
        this.setDirty();
    }

    /**
     * Returns the persisted brushable block type.
     *
     * @return persisted type
     */
    public String getType() {
        return this.nbt.getString("type");
    }

    /**
     * Returns whether this brushable block entity contains an item.
     *
     * @return whether an item is present
     */
    public boolean hasItem() {
        return this.nbt.containsCompound("item") && !getItem().isNull();
    }

    public Item getItem() {
        if (!this.nbt.containsCompound("item")) return Item.AIR;
        return ItemHelper.read(this.nbt.getCompound("item"));
    }

    public void setItem(Item item) {
        if (item == null || item.isNull()) {
            this.nbt.remove("item");
        } else {
            this.nbt.putCompound("item", ItemHelper.write(item));
        }

        this.setDirty();
    }

    /*
     * TODO: Implement Bedrock loot_tables resolution.
     *
     * LootTable and LootTableSeed are preserved exactly as BDS stores them,
     * but PNX does not yet resolve the referenced Bedrock loot table into
     * the archaeology item.
     */
    /**
     * Returns whether a Bedrock loot table is persisted.
     *
     * @return whether a loot table is present
     */
    public boolean hasLootTable() {
        return this.nbt.containsString("LootTable") && !this.nbt.getString("LootTable").isEmpty();
    }

    /**
     * Returns the persisted Bedrock loot table identifier.
     *
     * @return loot table identifier
     */
    public String getLootTable() {
        return this.nbt.getString("LootTable");
    }

    /**
     * Returns the persisted Bedrock loot table seed.
     *
     * @return loot table seed
     */
    public int getLootTableSeed() {
        return this.nbt.getInt("LootTableSeed");
    }

    /**
     * Removes the persisted Bedrock loot table data.
     */
    public void clearLootTable() {
        this.nbt.remove("LootTable", "LootTableSeed");
        this.setDirty();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = super.getSpawnCompound()
                .putInt("brush_count", getBrushCount())
                .putByte("brush_direction", getBrushDirection())
                .putString("type", getType());

        if (this.nbt.containsCompound("item")) {
            tag.putCompound("item", this.nbt.getCompound("item").copy());
        }

        return tag;
    }
}
