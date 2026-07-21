package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Material instances component for custom blocks.
 * Defines how the block is rendered (textures, render methods, etc.).
 */
public class MaterialInstancesComponent implements BlockComponent {
    private final Map<String, MaterialInstance> materials = new LinkedHashMap<>();
    private final Map<String, String> mappings = new LinkedHashMap<>();

    public MaterialInstancesComponent() {
    }

    public MaterialInstancesComponent material(String face, MaterialInstance instance) {
        materials.put(face, instance);
        return this;
    }

    public MaterialInstancesComponent mapping(String bone, String material) {
        mappings.put(bone, material);
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.MATERIAL_INSTANCES;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag mats = new CompoundTag();
        for (Map.Entry<String, MaterialInstance> entry : materials.entrySet()) {
            mats.putCompound(entry.getKey(), entry.getValue().toNBT());
        }
        tag.putCompound("materials", mats);
        CompoundTag maps = new CompoundTag();
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            maps.putString(entry.getKey(), entry.getValue());
        }
        tag.putCompound("mappings", maps);
        return tag;
    }

    /**
     * Represents a single material instance.
     */
    public static class MaterialInstance {
        private float ambientOcclusion = 1.0f;
        private byte packedBools = 0x1;
        private byte isotropic = 0;
        private String renderMethod = "opaque";
        private String texture = "missing_texture";
        private String tintMethod = "none";
        private String faceDimming = "true";

        public MaterialInstance ambientOcclusion(float value) {
            this.ambientOcclusion = value;
            return this;
        }

        public MaterialInstance packedBools(byte value) {
            this.packedBools = value;
            return this;
        }

        public MaterialInstance isotropic(byte value) {
            this.isotropic = value;
            return this;
        }

        public MaterialInstance renderMethod(String method) {
            this.renderMethod = method != null ? method : "opaque";
            return this;
        }

        public MaterialInstance texture(String texture) {
            this.texture = (texture != null && !texture.isBlank()) ? texture : "missing_texture";
            return this;
        }

        public MaterialInstance tintMethod(String method) {
            this.tintMethod = method != null ? method : "none";
            return this;
        }

        public MaterialInstance faceDimming(boolean value) {
            this.faceDimming = value ? "true" : "false";
            return this;
        }

        public CompoundTag toNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("ambient_occlusion", ambientOcclusion);
            tag.putByte("packed_bools", packedBools);
            tag.putByte("isotropic", isotropic);
            tag.putString("render_method", renderMethod);
            tag.putString("texture", texture);
            tag.putString("tint_method", tintMethod);
            tag.putString("face_dimming", faceDimming);
            return tag;
        }
    }
}