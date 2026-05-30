package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

/**
 * @author fromgate
 * @since 27.03.2016
 */
abstract public class ItemColorArmor extends ItemArmor {

    public ItemColorArmor(String id) {
        super(id);
    }

    public ItemColorArmor(String id, Integer meta) {
        super(id, meta);
    }

    public ItemColorArmor(String id, Integer meta, int count) {
        super(id, meta, count);
    }

    public ItemColorArmor(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
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
