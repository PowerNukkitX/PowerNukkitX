package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.80")
public class Geometry implements NBTData {
    private final String geometryName;
    private final Map<String, Boolean> boneVisibilities = new LinkedHashMap<>();

    public Geometry(@NotNull String name) {
        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(name.isBlank());
        this.geometryName = name;
    }

    /**
     * 控制模型对应骨骼是否显示
     * <p>
     * Control the visibility that the bone of geometry
     */
    public Geometry boneVisibility(@NotNull String boneName, boolean isVisibility) {
        Preconditions.checkNotNull(boneName);
        Preconditions.checkArgument(boneName.isBlank());
        this.boneVisibilities.put(boneName, isVisibility);
        return this;
    }


    @Override
    public CompoundTag toCompoundTag() {
        ListTag<CompoundTag> bones = new ListTag<>();
        for (var entry : boneVisibilities.entrySet()) {
            bones.add(new CompoundTag()
                    .putString("bone_name", entry.getKey())
                    .putBoolean("bone_visibility_bool", entry.getValue())
            );
        }
        CompoundTag compoundTag = new CompoundTag("minecraft:geometry")
                .putString("value", geometryName)
                .putByte("legacyBlockLightAbsorption", 0)
                .putByte("legacyTopRotation", 0);
        if (bones.size() > 0) {
            compoundTag.putList("boneVisibilities", bones);
        }
        return compoundTag;
    }
}
