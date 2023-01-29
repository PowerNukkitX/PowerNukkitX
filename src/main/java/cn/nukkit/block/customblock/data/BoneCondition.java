package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

@Since("1.19.50-r3")
@PowerNukkitXOnly

public record BoneCondition(@NotNull String conditionName,
                            @NotNull String boneName,
                            @NotNull String conditionExpr) implements NBTData {
    private static final int molangVersion = 6;

    @Override
    public CompoundTag toCompoundTag() {
        return new CompoundTag(conditionName)
                .putString("bone_condition", conditionExpr)
                .putString("bone_name", conditionName)
                .putInt("molang_version", molangVersion);
    }
}
