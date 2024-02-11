package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;


public class Geometry implements NBTData {
    private final String geometryName;
    private String culling = "";
    private final Map<String, String> boneVisibilities = new LinkedHashMap<>();

    public Geometry(@NotNull String name) {
        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(!name.isBlank());
        this.geometryName = name;
    }

    /**
     * 控制模型对应骨骼是否显示
     * <p>
     * Control the visibility that the bone of geometry
     */
    public Geometry boneVisibility(@NotNull String boneName, boolean isVisibility) {
        Preconditions.checkNotNull(boneName);
        Preconditions.checkArgument(!boneName.isBlank());
        this.boneVisibilities.put(boneName, isVisibility ? "1.000000" : "0.000000");
        return this;
    }

    /**
     * 控制模型对应骨骼是否显示
     * <p>
     * Control the visibility that the bone of geometry
     */
    public Geometry boneVisibility(@NotNull String boneName, String condition) {
        Preconditions.checkNotNull(boneName);
        Preconditions.checkArgument(!boneName.isBlank());
        this.boneVisibilities.put(boneName, condition);
        return this;
    }

    public Geometry culling(@NotNull String cullingName) {
        Preconditions.checkNotNull(cullingName);
        this.culling = cullingName;
        return this;
    }

    @Override
    public CompoundTag toCompoundTag() {
        var boneVisibility = new CompoundTag();
        for (var entry : boneVisibilities.entrySet()) {
            boneVisibility.putCompound(entry.getKey(), new CompoundTag()
                    .putString("expression", entry.getValue())
                    .putInt("version", 1)
            );
        }
        CompoundTag compoundTag = new CompoundTag()
                .putString("identifier", geometryName)
                .putString("culling", culling);
        if (!boneVisibilities.isEmpty()) {
            compoundTag.putCompound("bone_visibility", boneVisibility);
        }
        return compoundTag;
    }
}
