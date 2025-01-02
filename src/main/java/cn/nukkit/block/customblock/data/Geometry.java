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
     * Controls the visibility of the specified bone in the geometry.
     *
     * @param boneName     The name of the bone.
     * @param isVisibility The visibility state of the bone.
     * @return The current Geometry instance.
     */
    public Geometry boneVisibility(@NotNull String boneName, boolean isVisibility) {
        Preconditions.checkNotNull(boneName);
        Preconditions.checkArgument(!boneName.isBlank());
        this.boneVisibilities.put(boneName, isVisibility ? "true" : "false");
        return this;
    }

    /**
     * Controls the visibility of the specified bone in the geometry based on a condition.
     *
     * @param boneName  The name of the bone.
     * @param condition The condition for the bone's visibility.
     * @return The current Geometry instance.
     */
    public Geometry boneVisibility(@NotNull String boneName, String condition) {
        Preconditions.checkNotNull(boneName);
        Preconditions.checkArgument(!boneName.isBlank());
        this.boneVisibilities.put(boneName, condition);
        return this;
    }

    /**
     * Sets the culling name for the geometry.
     *
     * @param cullingName The name of the culling.
     * @return The current Geometry instance.
     */
    public Geometry culling(@NotNull String cullingName) {
        Preconditions.checkNotNull(cullingName);
        this.culling = cullingName;
        return this;
    }

    /**
     * Converts the Geometry instance to a CompoundTag.
     *
     * @return A CompoundTag representing the Geometry instance.
     */
    @Override
    public CompoundTag toCompoundTag() {
        var boneVisibility = new CompoundTag();
        for (var entry : boneVisibilities.entrySet()) {
            boneVisibility.putString(entry.getKey(), entry.getValue());
        }
        CompoundTag compoundTag = new CompoundTag()
                .putString("identifier", geometryName)
                .putByte("legacyBlockLightAbsorption", 0)
                .putByte("legacyTopRotation", 0);
        if (!boneVisibilities.isEmpty()) {
            compoundTag.putCompound("bone_visibility", boneVisibility);
        }
        return compoundTag;
    }
}