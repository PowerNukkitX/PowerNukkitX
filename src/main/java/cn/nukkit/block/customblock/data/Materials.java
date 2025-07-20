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

    /**
     * ambientOcclusion=true, faceDimming=true, tintType=null
     *
     * @see #up(RenderMethod, boolean, boolean, String)
     */
    public Materials up(RenderMethod renderMethod, String texture) {
        this.process("up", true, true, renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true
     * @param tintType         Set tintint method for the block
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials up(RenderMethod renderMethod, String texture, String tintType) {
        this.process("up", true, true, renderMethod, texture, tintType);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the up face. tintType=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the up face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @return the materials
     */
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the up face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the up face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @param tintType         Set tintint method for the block
     * @return the materials
     */
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintType) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture, tintType);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true, tintType=null
     *
     * @see #down(RenderMethod, boolean, boolean, String)
     */
    public Materials down(RenderMethod renderMethod, String texture) {
        this.process("down", true, true, renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true
     * @param tintType         Set tintint method for the block
     * 
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials down(RenderMethod renderMethod, String texture, String tintType) {
        this.process("down", true, true, renderMethod, texture, tintType);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the down face. tintType=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          指定down方向的Specify the texture's name of the down face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @return the materials
     */
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("down", ambientOcclusion, faceDimming, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the down face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          指定down方向的Specify the texture's name of the down face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @param tintType         Set tintint method for the block
     * @return the materials
     */
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintType) {
        this.process("down", ambientOcclusion, faceDimming, renderMethod, texture, tintType);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true, tintType=null
     *
     * @see #north(RenderMethod, boolean, boolean, String)
     */
    public Materials north(RenderMethod renderMethod, String texture) {
        this.process("north", true, true, renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true
     * @param tintType         Set tintint method for the block
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials north(RenderMethod renderMethod, String texture, String tintType) {
        this.process("north", true, true, renderMethod, texture, tintType);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the north face. tintType=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the north face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @return the materials
     */
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("north", ambientOcclusion, faceDimming, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the north face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the north face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @param tintType         Set tintint method for the block
     * @return the materials
     */
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintType) {
        this.process("north", ambientOcclusion, faceDimming, renderMethod, texture, tintType);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true, tintType=null
     *
     * @see #south(RenderMethod, boolean, boolean, String)
     */
    public Materials south(RenderMethod renderMethod, String texture) {
        this.process("south", true, true, renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true
     * @param tintType         Set tintint method for the block
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials south(RenderMethod renderMethod, String texture, String tintType) {
        this.process("south", true, true, renderMethod, texture, tintType);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the south face. tintType=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the south face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @return the materials
     */
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("south", ambientOcclusion, faceDimming, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the south face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the south face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @param tintType         Set tintint method for the block
     * @return the materials
     */
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintType) {
        this.process("south", ambientOcclusion, faceDimming, renderMethod, texture, tintType);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true, tintType=null
     *
     * @see #east(RenderMethod, boolean, boolean, String)
     */
    public Materials east(RenderMethod renderMethod, String texture) {
        this.process("east", true, true, renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true
     * @param tintType         Set tintint method for the block
     * 
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials east(RenderMethod renderMethod, String texture, String tintType) {
        this.process("east", true, true, renderMethod, texture, tintType);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the east face. tintType=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the east face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @return the materials
     */
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("east", ambientOcclusion, faceDimming, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the east face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name of the east face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @param tintType         Set tintint method for the block
     * @return the materials
     */
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintType) {
        this.process("east", ambientOcclusion, faceDimming, renderMethod, texture, tintType);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true, tintType=null
     *
     * @see #west(RenderMethod, boolean, boolean, String)
     */
    public Materials west(RenderMethod renderMethod, String texture) {
        this.process("west", true, true, renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true
     * @param tintType         Set tintint method for the block
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials west(RenderMethod renderMethod, String texture, String tintType) {
        this.process("west", true, true, renderMethod, texture, tintType);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the west face. tintType=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          指定west方向的Specify the texture's name of the west face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @return the materials
     */
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("west", ambientOcclusion, faceDimming, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and texture's name for the west face.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          指定west方向的Specify the texture's name of the west face
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @param tintType         Set tintint method for the block
     * @return the materials
     */
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintType) {
        this.process("west", ambientOcclusion, faceDimming, renderMethod, texture, tintType);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true, tintType=null
     *
     * @see #any(RenderMethod, boolean, boolean, String)
     */
    public Materials any(RenderMethod renderMethod, String texture) {
        this.process("*", true, true, renderMethod, texture, null);
        return this;
    }

    /**
     * ambientOcclusion=true, faceDimming=true
     * 
     * 
     * @see #up(RenderMethod, boolean, boolean, String, String)
     */
    public Materials any(RenderMethod renderMethod, String texture, String tintType) {
        this.process("*", true, true, renderMethod, texture, tintType);
        return this;
    }

    /**
     * Specify all corresponding rendering method, rendering parameters and texture name. tintType=null
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @return the materials
     */
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("*", ambientOcclusion, faceDimming, renderMethod, texture, null);
        return this;
    }

    /**
     * Specify all corresponding rendering method, rendering parameters and texture name.
     *
     * @param renderMethod     Rendering method to be used
     * @param texture          Specify the texture's name
     * @param ambientOcclusion Should I apply ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @return the materials
     */
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture, String tintType) {
        this.process("*", ambientOcclusion, faceDimming, renderMethod, texture, tintType);
        return this;
    }

    /**
     * Specify the corresponding rendering method, rendering parameters and materials.<p>
     * This method is completely customized. Please capture the package to confirm the legality of the parameters before using it.
     *
     * @param face             Specifies the name of the face. The optional value is：up, down, north, south, east, west, *
     * @param ambientOcclusion Should it be applied ambient light shielding when lighting?
     * @param faceDimming      Should it be dimmed according to the direction it is facing?
     * @param renderMethodName Rendering method to be used
     * @param texture          Specify the texture's name
     * @param tintType         Specify the tinting type
     */
    public void process(
        @NotNull String face,
        boolean ambientOcclusion,
        boolean faceDimming,
        @NotNull String renderMethodName,
        @NotNull String texture,
        @Nullable String tintType
    ) {
        CompoundTag faceTag = new CompoundTag()
            .putBoolean("ambient_occlusion", ambientOcclusion)
            .putBoolean("face_dimming", faceDimming)
            .putString("render_method", renderMethodName)
            .putString("texture", texture);

        if (tintType != null && !tintType.isBlank()) {
            faceTag.putString("tint_method", tintType);
        }
        this.tag.putCompound(face, faceTag);
    }

    private void process(
        @NotNull String face,
        boolean ambientOcclusion,
        boolean faceDimming,
        @NotNull RenderMethod renderMethod,
        @NotNull String texture,
        @Nullable String tintType
    ) {
        CompoundTag faceTag = new CompoundTag()
            .putBoolean("ambient_occlusion", ambientOcclusion)
            .putBoolean("face_dimming", faceDimming)
            .putString("render_method", renderMethod.name().toLowerCase(Locale.ENGLISH))
            .putString("texture", texture);

        if (tintType != null && !tintType.isBlank()) {
            faceTag.putString("tint_method", tintType);
        }
        this.tag.putCompound(face, faceTag);
    }

    public CompoundTag toCompoundTag() {
        return tag;
    }

    /**
     * Render methods and their properties:<p>
     * <p>
     * OPAQUE (default)        >> Transparency No | Translucency No | Backface Culling Yes | Distant Culling No | Ex: Dirt, Stone, Concrete<p>
     * BLEND                   >> Transparency Yes | Translucency Yes | Backface Culling Yes | Distant Culling No | Ex: Glass, Beacon, Honey Block<p>
     * DOUBLE_SIDED            >> Transparency No | Translucency No | Backface Culling No | Distant Culling No | Ex: Powder Snow<p>
     * ALPHA_TEST              >> Transparency Yes | Translucency No | Backface Culling No | Distant Culling Yes | Ex: Ladder, Monster Spawner, Vines<p>
     * ALPHA_TEST_SINGLE_SIDED >> Transparency Yes | Translucency No | Backface Culling Yes | Distant Culling Yes | Ex: Doors, Saplings, Trapdoors<p>
     * @see <a href="https://wiki.bedrock.dev/blocks/blocks-16.html#additional-notes">wiki.bedrock.dev</a><p>
     */
    public enum RenderMethod {
        OPAQUE,
        BLEND,
        DOUBLE_SIDED,
        ALPHA_TEST,
        ALPHA_TEST_SINGLE_SIDED
    }
}