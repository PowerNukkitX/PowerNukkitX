package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Defines material instances for custom blocks.
 * <p>
 * Material instances control the texture, render method, tinting, ambient occlusion,
 * face dimming, isotropic UV behavior and packed boolean flags used by the client.
 */
public class Materials implements NBTData {
    private final CompoundTag tag;

    private Materials() {
        this.tag = new CompoundTag();
    }

    /**
     * Creates a new material instances builder.
     *
     * @return new materials builder
     */
    public static Materials builder() {
        return new Materials();
    }

    /**
     * Packed material flags used by the client.
     * <p>
     * These values are encoded into the {@code packed_bools} byte.
     */
    public static final class PackedBools {
        private final boolean faceDimming;
        private final boolean randomUVRotation;
        private final boolean textureVariation;

        /**
         * Creates packed material flags.
         *
         * @param faceDimming true to enable directional face dimming
         * @param randomUVRotation true to enable randomized UV rotation
         * @param textureVariation true to enable texture variation
         */
        public PackedBools(boolean faceDimming, boolean randomUVRotation, boolean textureVariation) {
            this.faceDimming = faceDimming;
            this.randomUVRotation = randomUVRotation;
            this.textureVariation = textureVariation;
        }

        /**
         * @return true if directional face dimming is enabled
         */
        public boolean faceDimming() {
            return this.faceDimming;
        }

        /**
         * @return true if randomized UV rotation is enabled
         */
        public boolean randomUVRotation() {
            return this.randomUVRotation;
        }

        /**
         * @return true if texture variation is enabled
         */
        public boolean textureVariation() {
            return this.textureVariation;
        }

        /**
         * Encodes the packed material flags into the byte format expected by the client.
         *
         * @return packed bools byte
         */
        public byte toByte() {
            int v = 0;
            if (faceDimming) v |= 0x1;
            if (randomUVRotation) v |= 0x2;
            if (textureVariation) v |= 0x4;
            return (byte) (v & 0xFF);
        }
    }

    /**
     * Optional material instance settings.
     * <p>
     * These values are only written when explicitly configured.
     */
    public static final class Settings {
        private Float ambientOcclusion;
        private Boolean faceDimming;
        private Boolean isotropic;

        private Settings() {
        }

        /**
         * Creates a new material settings builder.
         *
         * @return new settings instance
         */
        public static Settings builder() {
            return new Settings();
        }

        /**
         * Sets the ambient occlusion exponent.
         * <p>
         * 0.0 disables ambient occlusion. 1.0 is the normal/default value.
         *
         * @param value ambient occlusion value
         * @return this settings instance
         */
        public Settings ambientOcclusion(float value) {
            this.ambientOcclusion = value;
            return this;
        }

        /**
         * Sets whether the material should be dimmed by the direction it is facing.
         *
         * @param value true to enable face dimming
         * @return this settings instance
         */
        public Settings faceDimming(boolean value) {
            this.faceDimming = value;
            return this;
        }

        /**
         * Sets whether the material should randomize its UVs.
         * <p>
         * This requires a format version of at least 1.21.80.
         *
         * @param value true to randomize UVs
         * @return this settings instance
         */
        public Settings isotropic(boolean value) {
            this.isotropic = value;
            return this;
        }

        private void apply(@NotNull CompoundTag tag) {
            if (this.ambientOcclusion != null) {
                tag.putFloat("ambient_occlusion", this.ambientOcclusion);
            }

            if (this.faceDimming != null) {
                tag.putBoolean("face_dimming", this.faceDimming);
            }

            if (this.isotropic != null) {
                tag.putBoolean("isotropic", this.isotropic);
            }
        }
    }

    /**
     * Sets the material instance for the up face.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @return the materials builder
     */
    public Materials up(RenderMethod renderMethod, String texture) {
        this.process("up", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the up face with tint.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials up(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("up", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the up face.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @return the materials builder
     */
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("up", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the up face with tint.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("up", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the up face with additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials up(RenderMethod renderMethod, String texture, Settings settings) {
        this.process("up", true, new PackedBools(true, false, false), renderMethod, texture, null, settings);
        return this;
    }

    /**
     * Sets the material instance for the up face with tint and additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials up(RenderMethod renderMethod, String texture, TintMethod tintMethod, Settings settings) {
        this.process("up", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod, settings);
        return this;
    }



    /**
     * Sets the material instance for the down face.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @return the materials builder
     */
    public Materials down(RenderMethod renderMethod, String texture) {
        this.process("down", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the down face with tint.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials down(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("down", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the down face.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @return the materials builder
     */
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("down", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the down face with tint.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("down", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the down face with additional settings.
     * <p>
     * Uses ambient occlusion enabled and default packed bools unless overridden by settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials down(RenderMethod renderMethod, String texture, Settings settings) {
        this.process("down", true, new PackedBools(true, false, false), renderMethod, texture, null, settings);
        return this;
    }

    /**
     * Sets the material instance for the down face with tint and additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials down(RenderMethod renderMethod, String texture, TintMethod tintMethod, Settings settings) {
        this.process("down", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod, settings);
        return this;
    }

    /**
     * Sets the material instance for the north face.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @return the materials builder
     */
    public Materials north(RenderMethod renderMethod, String texture) {
        this.process("north", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the north face with tint.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials north(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("north", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the north face.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @return the materials builder
     */
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("north", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the north face with tint.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("north", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the north face with additional settings.
     * <p>
     * Uses ambient occlusion enabled and default packed bools unless overridden by settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials north(RenderMethod renderMethod, String texture, Settings settings) {
        this.process("north", true, new PackedBools(true, false, false), renderMethod, texture, null, settings);
        return this;
    }

    /**
     * Sets the material instance for the north face with tint and additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials north(RenderMethod renderMethod, String texture, TintMethod tintMethod, Settings settings) {
        this.process("north", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod, settings);
        return this;
    }

    /**
     * Sets the material instance for the south face.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @return the materials builder
     */
    public Materials south(RenderMethod renderMethod, String texture) {
        this.process("south", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the south face with tint.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials south(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("south", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the south face.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @return the materials builder
     */
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("south", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the south face with tint.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("south", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the south face with additional settings.
     * <p>
     * Uses ambient occlusion enabled and default packed bools unless overridden by settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials south(RenderMethod renderMethod, String texture, Settings settings) {
        this.process("south", true, new PackedBools(true, false, false), renderMethod, texture, null, settings);
        return this;
    }

    /**
     * Sets the material instance for the south face with tint and additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials south(RenderMethod renderMethod, String texture, TintMethod tintMethod, Settings settings) {
        this.process("south", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod, settings);
        return this;
    }

    /**
     * Sets the material instance for the east face.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @return the materials builder
     */
    public Materials east(RenderMethod renderMethod, String texture) {
        this.process("east", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the east face with tint.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials east(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("east", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the east face.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @return the materials builder
     */
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("east", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the east face with tint.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("east", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the east face with additional settings.
     * <p>
     * Uses ambient occlusion enabled and default packed bools unless overridden by settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials east(RenderMethod renderMethod, String texture, Settings settings) {
        this.process("east", true, new PackedBools(true, false, false), renderMethod, texture, null, settings);
        return this;
    }

    /**
     * Sets the material instance for the east face with tint and additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials east(RenderMethod renderMethod, String texture, TintMethod tintMethod, Settings settings) {
        this.process("east", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod, settings);
        return this;
    }

    /**
     * Sets the material instance for the west face.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @return the materials builder
     */
    public Materials west(RenderMethod renderMethod, String texture) {
        this.process("west", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the west face with tint.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials west(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("west", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the west face.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @return the materials builder
     */
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("west", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for the west face with tint.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("west", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for the west face with additional settings.
     * <p>
     * Uses ambient occlusion enabled and default packed bools unless overridden by settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials west(RenderMethod renderMethod, String texture, Settings settings) {
        this.process("west", true, new PackedBools(true, false, false), renderMethod, texture, null, settings);
        return this;
    }

    /**
     * Sets the material instance for the west face with tint and additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials west(RenderMethod renderMethod, String texture, TintMethod tintMethod, Settings settings) {
        this.process("west", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod, settings);
        return this;
    }

    /**
     * Sets the material instance for all faces.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @return the materials builder
     */
    public Materials any(RenderMethod renderMethod, String texture) {
        this.process("*", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for all faces with tint.
     * <p>
     * Uses ambient occlusion enabled and default packed bools.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials any(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("*", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for all faces.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @return the materials builder
     */
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("*", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for all faces with tint.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("*", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for all faces.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion ambient occlusion exponent. 0.0 disables it, 1.0 is normal/default
     * @param packedBools packed material flags
     * @param texture texture name
     * @return the materials builder
     */
    public Materials any(RenderMethod renderMethod, float ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("*", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Sets the material instance for all faces with tint.
     *
     * @param renderMethod rendering method to use
     * @param ambientOcclusion ambient occlusion exponent. 0.0 disables it, 1.0 is normal/default
     * @param packedBools packed material flags
     * @param texture texture name
     * @param tintMethod tint method to use
     * @return the materials builder
     */
    public Materials any(RenderMethod renderMethod, float ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("*", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Sets the material instance for all faces with additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials any(RenderMethod renderMethod, String texture, Settings settings) {
        this.process("*", true, new PackedBools(true, false, false), renderMethod, texture, null, settings);
        return this;
    }

    /**
     * Sets the material instance for all faces with tint and additional settings.
     *
     * @param renderMethod rendering method to use
     * @param texture texture name
     * @param tintMethod tint method to use
     * @param settings optional material settings
     * @return the materials builder
     */
    public Materials any(RenderMethod renderMethod, String texture, TintMethod tintMethod, Settings settings) {
        this.process("*", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod, settings);
        return this;
    }

    private CompoundTag getOrCreateFaceTag(@NotNull String face) {
        Tag existing = this.tag.get(face);

        if (existing instanceof CompoundTag compoundTag) {
            return compoundTag;
        }

        CompoundTag faceTag = new CompoundTag();
        this.tag.putCompound(face, faceTag);
        return faceTag;
    }

    /**
     * Sets a material instance using a custom render method name.
     *
     * @param face material instance name or face. Valid face values are: up, down, north, south, east, west, *
     * @param ambientOcclusion true to write ambient occlusion as 1.0, false to write it as 0.0
     * @param packedBools packed material flags
     * @param renderMethodName render method name to write
     * @param texture texture name
     * @param tintMethod optional tint method
     */
    public void process(
        @NotNull String face,
        boolean ambientOcclusion,
        @NotNull PackedBools packedBools,
        @NotNull String renderMethodName,
        @NotNull String texture,
        @Nullable TintMethod tintMethod
    ) {
        this.process(face, ambientOcclusion ? 1.0f : 0.0f, packedBools, renderMethodName, texture, tintMethod);
    }

    /**
     * Sets a material instance using a custom render method name.
     *
     * @param face material instance name or face. Valid face values are: up, down, north, south, east, west, *
     * @param ambientOcclusion ambient occlusion exponent. 0.0 disables it, 1.0 is normal/default
     * @param packedBools packed material flags
     * @param renderMethodName render method name to write
     * @param texture texture name
     * @param tintMethod optional tint method
     */
    public void process(
        @NotNull String face,
        float ambientOcclusion,
        @NotNull PackedBools packedBools,
        @NotNull String renderMethodName,
        @NotNull String texture,
        @Nullable TintMethod tintMethod
    ) {
        this.process(face, ambientOcclusion, packedBools, renderMethodName, texture, tintMethod, null);
    }

    /**
     * Sets a material instance using a custom render method name and optional settings.
     *
     * @param face material instance name or face. Valid face values are: up, down, north, south, east, west, *
     * @param ambientOcclusion ambient occlusion exponent. 0.0 disables it, 1.0 is normal/default
     * @param packedBools packed material flags
     * @param renderMethodName render method name to write
     * @param texture texture name
     * @param tintMethod optional tint method
     * @param settings optional material settings
     */
    public void process(
        @NotNull String face,
        float ambientOcclusion,
        @NotNull PackedBools packedBools,
        @NotNull String renderMethodName,
        @NotNull String texture,
        @Nullable TintMethod tintMethod,
        @Nullable Settings settings
    ) {
        CompoundTag faceTag = this.getOrCreateFaceTag(face)
            .putFloat("ambient_occlusion", ambientOcclusion)
            .putByte("packed_bools", packedBools.toByte())
            .putString("render_method", renderMethodName)
            .putString("texture", texture);

        if (settings != null) {
            settings.apply(faceTag);
        }

        if (tintMethod != null && tintMethod != TintMethod.NONE) {
            faceTag.putString("tint_method", tintMethod.name().toLowerCase(Locale.ENGLISH));
        }

        this.tag.putCompound(face, faceTag);
    }

    private void process(
        @NotNull String face,
        boolean ambientOcclusion,
        @NotNull PackedBools packedBools,
        @NotNull RenderMethod renderMethod,
        @NotNull String texture,
        @Nullable TintMethod tintMethod
    ) {
        this.process(face, ambientOcclusion ? 1.0f : 0.0f, packedBools, renderMethod, texture, tintMethod);
    }

    private void process(
        @NotNull String face,
        float ambientOcclusion,
        @NotNull PackedBools packedBools,
        @NotNull RenderMethod renderMethod,
        @NotNull String texture,
        @Nullable TintMethod tintMethod
    ) {
        this.process(face, ambientOcclusion, packedBools, renderMethod.name().toLowerCase(Locale.ENGLISH), texture, tintMethod);
    }

    private void process(
        @NotNull String face,
        boolean ambientOcclusion,
        @NotNull PackedBools packedBools,
        @NotNull RenderMethod renderMethod,
        @NotNull String texture,
        @Nullable TintMethod tintMethod,
        @Nullable Settings settings
    ) {
        this.process(face, ambientOcclusion ? 1.0f : 0.0f, packedBools, renderMethod, texture, tintMethod, settings);
    }

    private void process(
        @NotNull String face,
        float ambientOcclusion,
        @NotNull PackedBools packedBools,
        @NotNull RenderMethod renderMethod,
        @NotNull String texture,
        @Nullable TintMethod tintMethod,
        @Nullable Settings settings
    ) {
        this.process(face, ambientOcclusion, packedBools, renderMethod.name().toLowerCase(Locale.ENGLISH), texture, tintMethod, settings);
    }

    public CompoundTag toCompoundTag() {
        return tag;
    }

    /**
     * Render methods and their properties:<p>
     * <p>
     * OPAQUE (default)                     >> Transparency No | Translucency No | Backface Culling Yes | Distant Culling No | Ex: Dirt, Stone, Concrete<p>
     * BLEND                                >> Transparency Yes | Translucency Yes | Backface Culling Yes | Distant Culling No | Ex: Glass, Beacon, Honey Block<p>
     * DOUBLE_SIDED                         >> Transparency No | Translucency No | Backface Culling No | Distant Culling No | Ex: Powder Snow<p>
     * ALPHA_TEST                           >> Transparency Yes | Translucency No | Backface Culling No | Distant Culling Yes | Ex: Ladder, Monster Spawner, Vines<p>
     * ALPHA_TEST_SINGLE_SIDED              >> Transparency Yes | Translucency No | Backface Culling Yes | Distant Culling Yes | Ex: Doors, Saplings, Trapdoors<p>
     * ALPHA_TEST_TO_OPAQUE,                >> Transparency Yes | Translucency No | Backface Culling No | Distant Culling Yes | will shift to "opaque" in the distance.
     * ALPHA_TEST_SINGLE_SIDED_TO_OPAQUE,   >> Transparency Yes | Translucency No | Backface Culling Yes | Distant Culling Yes | will shift to "opaque" in the distance.
     * BLEND_TO_OPAQUE                      >> Transparency No | Translucency No | Backface Culling Yes | Distant Culling No | will shift to "opaque" in the distance.
     * @see <a href="https://wiki.bedrock.dev/blocks/blocks-16.html#additional-notes">wiki.bedrock.dev</a><p>
     */
    public enum RenderMethod {
        OPAQUE,
        BLEND,
        DOUBLE_SIDED,
        ALPHA_TEST,
        ALPHA_TEST_SINGLE_SIDED,
        ALPHA_TEST_TO_OPAQUE,
        ALPHA_TEST_SINGLE_SIDED_TO_OPAQUE,
        BLEND_TO_OPAQUE
    }

    /**
     * Tint method multiplied into the material color.
     * <p>
     * Tint behavior depends on the client and usually uses biome temperature/rainfall data.
     * {@link #NONE} means no tint method should be written.
     */
    public enum TintMethod {
        NONE,
        DEFAULT_FOLIAGE,
        BIRCH_FOLIAGE,
        EVERGREEN_FOLIAGE,
        DRY_FOLIAGE,
        GRASS,
        WATER;

        public static @Nullable TintMethod fromString(String s) {
            if (s == null || s.isBlank()) return null;
            String key = s.trim()
                    .toUpperCase(Locale.ENGLISH)
                    .replaceAll("[\\s-]+", "_");

            return switch (key) {
                case "NONE" -> NONE;
                case "DEFAULT_FOLIAGE" -> DEFAULT_FOLIAGE;
                case "BIRCH_FOLIAGE" -> BIRCH_FOLIAGE;
                case "EVERGREEN_FOLIAGE" -> EVERGREEN_FOLIAGE;
                case "DRY_FOLIAGE" -> DRY_FOLIAGE;
                case "GRASS" -> GRASS;
                case "WATER" -> WATER;
                default -> null;
            };
        }
    }



    // Deprecated methods.

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     */
    @Deprecated
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintMethod) {
        this.process("up", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     */
    @Deprecated
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("down", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintMethod) {
        this.process("down", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     */
    @Deprecated
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("north", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintMethod) {
        this.process("north", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     */
    @Deprecated
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("south", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintMethod) {
        this.process("south", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     */
    @Deprecated
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("east", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintMethod) {
        this.process("east", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     */
    @Deprecated
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("west", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintMethod) {
        this.process("west", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     */
    @Deprecated
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("*", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * <p><b>Deprecated:</b> {@code faceDimming} is deprecated, use {@link PackedBools} instead.
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintMethod) {
        this.process("*", ambientOcclusion, new PackedBools(faceDimming, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }


    /**
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials up(RenderMethod renderMethod, String texture, String tintMethod) {
        this.process("up", true, new PackedBools(true, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials down(RenderMethod renderMethod, String texture, String tintMethod) {
        this.process("down", true, new PackedBools(true, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials north(RenderMethod renderMethod, String texture, String tintMethod) {
        this.process("north", true, new PackedBools(true, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials south(RenderMethod renderMethod, String texture, String tintMethod) {
        this.process("south", true, new PackedBools(true, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials east(RenderMethod renderMethod, String texture, String tintMethod) {
        this.process("east", true, new PackedBools(true, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials west(RenderMethod renderMethod, String texture, String tintMethod) {
        this.process("west", true, new PackedBools(true, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }

    /**
     * <p><b>Deprecated:</b> String {@code tintMethod} is deprecated, use {@link TintMethod} enums instead.
     */
    @Deprecated
    public Materials any(RenderMethod renderMethod, String texture, String tintMethod) {
        this.process("*", true, new PackedBools(true, false, false), renderMethod, texture, TintMethod.fromString(tintMethod));
        return this;
    }
}