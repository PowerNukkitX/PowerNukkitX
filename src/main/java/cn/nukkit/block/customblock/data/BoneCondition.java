package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.NonNull;

@Since("1.19.50-r3")
@PowerNukkitXOnly
public record BoneCondition(@NonNull String conditionName, @NonNull String conditionExpr,
                            @NonNull String boneName) implements NBTData {
    private static final int molangVersion = 6;

    @Override
    public CompoundTag toCompoundTag() {
        return new CompoundTag(conditionName)
                .putString("bone_condition", conditionExpr)
                .putString("bone_name", conditionName)
                .putInt("molang_version", molangVersion);
    }
}
