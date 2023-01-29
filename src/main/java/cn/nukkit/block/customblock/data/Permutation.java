package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * The type Permutation builder.
 */
@PowerNukkitXOnly
@Since("1.19.31-r1")
public record Permutation(Component component, String condition) implements NBTData {

    @Override
    public CompoundTag toCompoundTag() {
        return new CompoundTag().putCompound(component.toCompoundTag()).putString("condition", condition);
    }
}
