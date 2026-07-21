package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Geometry component for custom blocks.
 * Defines the 3D model used to render the block.
 */
public class GeometryComponent implements BlockComponent {
    private String identifier = "minecraft:geometry.full_block";
    private final Map<String, Boolean> boneVisibility = new LinkedHashMap<>();
    private String culling = "";
    private String cullingLayer = "minecraft:culling_layer.undefined";
    private String cullingShape = "";
    private boolean ignoreGeometryForIsSolid = true;
    private boolean needsLegacyTopRotation = false;
    private boolean useLegacyBlockLightAbsorption = false;
    private boolean uvLock = false;

    public GeometryComponent() {
    }

    public GeometryComponent(String identifier) {
        this.identifier = identifier;
    }

    public GeometryComponent identifier(String identifier) {
        this.identifier = (identifier != null && !identifier.isBlank()) ? identifier : "minecraft:geometry.full_block";
        return this;
    }

    public GeometryComponent boneVisible(String bone, boolean visible) {
        boneVisibility.put(bone, visible);
        return this;
    }

    public GeometryComponent culling(String culling) {
        this.culling = culling != null ? culling : "";
        return this;
    }

    public GeometryComponent cullingLayer(String layer) {
        this.cullingLayer = layer != null ? layer : "minecraft:culling_layer.undefined";
        return this;
    }

    public GeometryComponent cullingShape(String shape) {
        this.cullingShape = shape != null ? shape : "";
        return this;
    }

    public GeometryComponent ignoreGeometryForIsSolid(boolean value) {
        this.ignoreGeometryForIsSolid = value;
        return this;
    }

    public GeometryComponent needsLegacyTopRotation(boolean value) {
        this.needsLegacyTopRotation = value;
        return this;
    }

    public GeometryComponent useLegacyBlockLightAbsorption(boolean value) {
        this.useLegacyBlockLightAbsorption = value;
        return this;
    }

    public GeometryComponent uvLock(boolean value) {
        this.uvLock = value;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.GEOMETRY;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("identifier", identifier);
        CompoundTag bones = new CompoundTag();
        for (Map.Entry<String, Boolean> entry : boneVisibility.entrySet()) {
            bones.putByte(entry.getKey(), (byte) (entry.getValue() ? 1 : 0));
        }
        tag.putCompound("bone_visibility", bones);
        tag.putString("culling", culling);
        tag.putString("culling_layer", cullingLayer);
        tag.putString("culling_shape", cullingShape);
        tag.putByte("ignoreGeometryForIsSolid", (byte) (ignoreGeometryForIsSolid ? 1 : 0));
        tag.putByte("needsLegacyTopRotation", (byte) (needsLegacyTopRotation ? 1 : 0));
        tag.putByte("useLegacyBlockLightAbsorption", (byte) (useLegacyBlockLightAbsorption ? 1 : 0));
        tag.putByte("uv_lock", (byte) (uvLock ? 1 : 0));
        return tag;
    }
}