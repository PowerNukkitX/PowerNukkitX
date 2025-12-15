package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;


/**
 * Used to map the face of the block to the actual material instance and set the rendering method and parameters
 */
public class Materials implements NBTData {
    private final CompoundTag tag;

    private Materials() {
        this.tag = new CompoundTag();
    }

    /**
     * Builder materials.
     *
     * @return the materials
     */
    public static Materials builder() {
        return new Materials();
    }

    public static final class PackedBools {
        private final boolean faceDimming;
        private final boolean randomUVRotation;
        private final boolean textureVariation;

        public PackedBools(boolean faceDimming, boolean randomUVRotation, boolean textureVariation) {
            this.faceDimming = faceDimming;
            this.randomUVRotation = randomUVRotation;
            this.textureVariation = textureVariation;
        }

        public byte toByte() {
            int v = 0;
            if (faceDimming) v |= 0x1;
            if (randomUVRotation) v |= 0x2;
            if (textureVariation) v |= 0x4;
            return (byte) (v & 0xFF);
        }
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false), tintMethod=null
     *
     * @see #up(RenderMethod, boolean, boolean, String)
     */
    public Materials up(RenderMethod renderMethod, String texture) {
        this.process("up", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false)
     * @see TintMethod      Set tintint method for the block
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials up(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("up", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the up face. tintMethod=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the up face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @return the materials
     */
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("up", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the up face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the up face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @see TintMethod         Set tintint method for the block
     * @return the materials
     */
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("up", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false), tintMethod=null
     *
     * @see #down(RenderMethod, boolean, boolean, String)
     */
    public Materials down(RenderMethod renderMethod, String texture) {
        this.process("down", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false)
     * @see TintMethod         Set tintint method for the block
     * 
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials down(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("down", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the down face. tintMethod=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          指定down方向的Specify the texture's name of the down face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @return the materials
     */
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("down", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the down face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          指定down方向的Specify the texture's name of the down face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @see TintMethod         Set tintint method for the block
     * @return the materials
     */
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("down", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false), tintMethod=null
     *
     * @see #north(RenderMethod, boolean, boolean, String)
     */
    public Materials north(RenderMethod renderMethod, String texture) {
        this.process("north", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false)
     * @see TintMethod         Set tintint method for the block
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials north(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("north", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the north face. tintMethod=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the north face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @return the materials
     */
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("north", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the north face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the north face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @see TintMethod         Set tintint method for the block
     * @return the materials
     */
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("north", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false), tintMethod=null
     *
     * @see #south(RenderMethod, boolean, boolean, String)
     */
    public Materials south(RenderMethod renderMethod, String texture) {
        this.process("south", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false)
     * @see TintMethod         Set tintint method for the block
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials south(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("south", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the south face. tintMethod=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the south face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @return the materials
     */
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("south", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the south face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the south face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @see TintMethod         Set tintint method for the block
     * @return the materials
     */
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("south", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false), tintMethod=null
     *
     * @see #east(RenderMethod, boolean, boolean, String)
     */
    public Materials east(RenderMethod renderMethod, String texture) {
        this.process("east", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false)
     * @see TintMethod         Set tintint method for the block
     * 
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials east(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("east", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the east face. tintMethod=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the east face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @return the materials
     */
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("east", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the east face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the east face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @see TintMethod         Set tintint method for the block
     * @return the materials
     */
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("east", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false), tintMethod=null
     *
     * @see #west(RenderMethod, boolean, boolean, String)
     */
    public Materials west(RenderMethod renderMethod, String texture) {
        this.process("west", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false)
     * @see TintMethod         Set tintint method for the block
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials west(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("west", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the west face. tintMethod=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          指定west方向的Specify the texture's name of the west face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @return the materials
     */
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("west", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the west face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          指定west方向的Specify the texture's name of the west face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @see TintMethod         Set tintint method for the block
     * @return the materials
     */
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("west", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false), tintMethod=null
     *
     * @see #any(RenderMethod, boolean, boolean, String)
     */
    public Materials any(RenderMethod renderMethod, String texture) {
        this.process("*", true, new PackedBools(true, false, false), renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, PackedBools(true, false, false)
     * 
     * 
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials any(RenderMethod renderMethod, String texture, TintMethod tintMethod) {
        this.process("*", true, new PackedBools(true, false, false), renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Specify all corresponding rendering method, rendering parameters and texture name. tintMethod=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @return the materials
     */
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture) {
        this.process("*", ambientOcclusion, packedBools, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify all corresponding rendering method, rendering parameters and texture name.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @return the materials
     */
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, PackedBools packedBools, String texture, TintMethod tintMethod) {
        this.process("*", ambientOcclusion, packedBools, renderMethod, texture, tintMethod);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and materials.<p>
     * This method is completely customized. Please capture the package to confirm the legality of the parameters before using it.
     *
     * @param face             Specifies the name of the face. The optional value is：up, down, north, south, east, west, *
     * @param ambientOcclusion Should it be applied ambient light shielding when lighting?
     * @param packedBools      Carries bools for face dimming, random UV rotation and texture variation support.
     * @param renderMethodName Rendering method to be used
     * @param texture          Specify the texture's name
     * @see TintMethod       Specify the tinting type
     */
    public void process(
        @NotNull String face,
        boolean ambientOcclusion,
        @NotNull PackedBools packedBools,
        @NotNull String renderMethodName,
        @NotNull String texture,
        @Nullable TintMethod tintMethod
    ) {
        CompoundTag faceTag = new CompoundTag()
            .putBoolean("ambient_occlusion", ambientOcclusion)
            .putByte("packed_bools", packedBools.toByte())
            .putString("render_method", renderMethodName)
            .putString("texture", texture);

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
        CompoundTag faceTag = new CompoundTag()
            .putBoolean("ambient_occlusion", ambientOcclusion)
            .putByte("packed_bools", packedBools.toByte())
            .putString("render_method", renderMethod.name().toLowerCase(Locale.ENGLISH))
            .putString("texture", texture);

        if (tintMethod != null && tintMethod != TintMethod.NONE) {
            faceTag.putString("tint_method", tintMethod.name().toLowerCase(Locale.ENGLISH));
        }
        this.tag.putCompound(face, faceTag);
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
     * Tint multiplied to the color. Tint method logic varies, but often refers to the "rain" and "temperature" of the biome <p>
     * Supported tint methods are "NONE", "DEFAULT_FOLIAGE", "BIRCH_FOLIAGE", "EVERGREEN_FOLIAGE", "DRY_FOLIAGE", "GRASS" and "WATER".
     * */
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
            switch (key) {
                case "NONE": return NONE;
                case "DEFAULT_FOLIAGE": return DEFAULT_FOLIAGE;
                case "BIRCH_FOLIAGE": return BIRCH_FOLIAGE;
                case "EVERGREEN_FOLIAGE": return EVERGREEN_FOLIAGE;
                case "DRY_FOLIAGE": return DRY_FOLIAGE;
                case "GRASS": return GRASS;
                case "WATER": return WATER;
                default: return NONE;
            }
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