package cn.nukkit.block.customblock.data;

import com.google.common.base.Preconditions;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;


public class Geometry implements NBTData {
    private final String geometryName;
    private String culling = "";
    private String culling_shape = "";
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
        this.boneVisibilities.put(boneName, isVisibility ? "true" : "false");
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

    public Geometry cullingShape(@NotNull String cullingName) {
        Preconditions.checkNotNull(cullingName);
        this.culling_shape = cullingName;
        return this;
    }

    @Override
    public NbtMap toCompoundTag() {
        var boneVisibility = NbtMap.builder();
        for (var entry : boneVisibilities.entrySet()) {
            boneVisibility.putString(entry.getKey(), entry.getValue());
        }
        NbtMapBuilder compoundTag = NbtMap.builder()
                .putString("identifier", geometryName)
                .putByte("legacyBlockLightAbsorption", (byte) 0)
                .putByte("legacyTopRotation", (byte) 0);
        if (!boneVisibilities.isEmpty()) {
            compoundTag.putCompound("bone_visibility", boneVisibility.build());
        }
        if (!culling.isBlank()) {
            compoundTag.putString("culling", culling);
        }
        if (!culling_shape.isBlank()) {
            compoundTag.putString("culling_shape", culling_shape);
        }

        return compoundTag.build();
    }
}
