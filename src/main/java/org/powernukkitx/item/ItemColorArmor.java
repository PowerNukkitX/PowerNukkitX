package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.BlockColor;
import org.powernukkitx.utils.DyeColor;

/**
 * @author fromgate
 * @since 27.03.2016
 */
abstract public class ItemColorArmor extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder().build();

    public ItemColorArmor(String id) {
        this(id, 0, 1, (String) null, DEFINITION);
    }

    public ItemColorArmor(String id, ItemDefinition definition) {
        this(id, 0, 1, (String) null, definition);
    }

    public ItemColorArmor(String id, Integer meta) {
        this(id, meta, 1, (String) null, DEFINITION);
    }

    public ItemColorArmor(String id, Integer meta, ItemDefinition definition) {
        this(id, meta, 1, (String) null, definition);
    }

    public ItemColorArmor(String id, Integer meta, int count) {
        this(id, meta, count, (String) null, DEFINITION);
    }

    public ItemColorArmor(String id, Integer meta, int count, ItemDefinition definition) {
        this(id, meta, count, (String) null, definition);
    }

    public ItemColorArmor(String id, Integer meta, int count, String name) {
        this(id, meta, count, name, DEFINITION);
    }

    public ItemColorArmor(String id, Integer meta, int count, String name, ItemDefinition definition) {
        super(id, meta, count, name, definition);
    }

    /**
     * Set leather armor color
     *
     * @param dyeColor - DyeColor object
     * @return - Return colored item
     */
    public ItemColorArmor setColor(DyeColor dyeColor) {
        BlockColor blockColor = dyeColor.getColor();
        return setColor(blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue());
    }

    /**
     * Set leather armor color
     *
     * @param color - BlockColor object
     * @return - Return colored item
     */
    public ItemColorArmor setColor(BlockColor color) {
        return setColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Set leather armor color
     *
     * @param r - red
     * @param g - green
     * @param b - blue
     * @return - Return colored item
     */
    public ItemColorArmor setColor(int r, int g, int b) {
        int rgb = r << 16 | g << 8 | b;
        CompoundTag tag = this.getOrCreateNbt();
        tag.putInt("customColor", rgb);
        this.setNbt(tag);
        return this;
    }

    /**
     * Get color of Leather Item
     *
     * @return - BlockColor, or null if item has no color
     */
    public BlockColor getColor() {
        if (!this.hasNbt()) return null;
        CompoundTag tag = this.getNbt();
        if (!tag.containsInt("customColor")) return null;
        int rgb = tag.getInt("customColor");
        return new BlockColor(rgb);
    }
}
