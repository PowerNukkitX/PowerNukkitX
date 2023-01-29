package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Since("1.19.50-r3")
@PowerNukkitXOnly
@Builder(buildMethodName = "toCompoundTag")
@Getter
public class BoneCondition implements NBTData {
    @NonNull
    String conditionName;
    /**
     * The molang condition.
     */
    @NonNull
    String conditionExpr;
    @NonNull
    String boneName;
    @Builder.Default
    int molangVersion = 6;

    @Override
    public CompoundTag toCompoundTag() {
        return new CompoundTag(conditionName)
                .putString("bone_condition", conditionExpr)
                .putString("bone_name", conditionName)
                .putInt("molang_version", molangVersion);
    }
}
