package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;

/**
 * The Permutation class represents a permutation with a component, a condition, and an array of block tags.
 */
public record Permutation(Component component, String condition, String[] blockTags) implements NBTData {

    /**
     * Constructs a Permutation with the specified component and condition.
     *
     * @param component The component associated with this permutation.
     * @param condition The condition for this permutation.
     */
    public Permutation(Component component, String condition) {
        this(component, condition, new String[]{});
    }

    /**
     * Converts the Permutation instance to a CompoundTag.
     *
     * @return A CompoundTag representing the Permutation instance.
     */
    @Override
    public CompoundTag toCompoundTag() {
        CompoundTag result = new CompoundTag()
                .putCompound("components", component.toCompoundTag())
                .putString("condition", condition);
        ListTag<StringTag> stringTagListTag = new ListTag<>();
        for (String s : blockTags) {
            stringTagListTag.add(new StringTag(s));
        }
        if (stringTagListTag.size() > 0) {
            result.putList("blockTags", stringTagListTag);
        }
        return result;
    }
}