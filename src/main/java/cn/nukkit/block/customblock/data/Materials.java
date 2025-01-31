package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Used to map the face of a block to a material instance, and set the rendering method and parameters.
 */
public class Materials implements NBTData {
    private final CompoundTag tag;

    private Materials() {
        this.tag = new CompoundTag();
    }

    /**
     * Builder for materials.
     *
     * @return the materials instance
     */
    public static Materials builder() {
        return new Materials();
    }

    /**
     * Sets the rendering method and texture for the up face with default parameters.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the up face
     * @return the materials instance
     * @see #up(RenderMethod, boolean, boolean, String)
     */
    public Materials up(RenderMethod renderMethod, String texture) {
        this.process("up", true, true, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method, rendering parameters, and texture for the up face.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the up face
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @return the materials instance
     */
    public Materials up(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method and texture for the down face with default parameters.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the down face
     * @return the materials instance
     * @see #down(RenderMethod, boolean, boolean, String)
     */
    public Materials down(RenderMethod renderMethod, String texture) {
        this.process("down", true, true, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method, rendering parameters, and texture for the down face.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the down face
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @return the materials instance
     */
    public Materials down(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("down", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method and texture for the north face with default parameters.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the north face
     * @return the materials instance
     * @see #north(RenderMethod, boolean, boolean, String)
     */
    public Materials north(RenderMethod renderMethod, String texture) {
        this.process("north", true, true, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method, rendering parameters, and texture for the north face.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the north face
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @return the materials instance
     */
    public Materials north(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("north", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method and texture for the south face with default parameters.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the south face
     * @return the materials instance
     * @see #south(RenderMethod, boolean, boolean, String)
     */
    public Materials south(RenderMethod renderMethod, String texture) {
        this.process("south", true, true, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method, rendering parameters, and texture for the south face.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the south face
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @return the materials instance
     */
    public Materials south(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("south", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method and texture for the east face with default parameters.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the east face
     * @return the materials instance
     * @see #east(RenderMethod, boolean, boolean, String)
     */
    public Materials east(RenderMethod renderMethod, String texture) {
        this.process("east", true, true, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method, rendering parameters, and texture for the east face.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the east face
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @return the materials instance
     */
    public Materials east(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("east", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method and texture for the west face with default parameters.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the west face
     * @return the materials instance
     * @see #west(RenderMethod, boolean, boolean, String)
     */
    public Materials west(RenderMethod renderMethod, String texture) {
        this.process("west", true, true, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method, rendering parameters, and texture for the west face.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name of the west face
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @return the materials instance
     */
    public Materials west(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("west", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method and texture for all faces with default parameters.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name
     * @return the materials instance
     * @see #any(RenderMethod, boolean, boolean, String)
     */
    public Materials any(RenderMethod renderMethod, String texture) {
        this.process("*", true, true, renderMethod, texture);
        return this;
    }

    /**
     * Sets the rendering method, rendering parameters, and texture for all faces.
     *
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @return the materials instance
     */
    public Materials any(RenderMethod renderMethod, boolean ambientOcclusion, boolean faceDimming, String texture) {
        this.process("*", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    /**
     * Processes the rendering method, rendering parameters, and texture for a specified face.
     *
     * @param face the name of the face (e.g., up, down, north, south, east, west, *)
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @param renderMethodName the rendering method to be used
     * @param texture the texture's name
     */
    public void process(@NotNull String face, boolean ambientOcclusion, boolean faceDimming, @NotNull String renderMethodName, @NotNull String texture) {
        this.tag.putCompound(face, new CompoundTag()
                .putBoolean("ambient_occlusion", ambientOcclusion)
                .putBoolean("face_dimming", faceDimming)
                .putString("render_method", renderMethodName)
                .putString("texture", texture));
    }

    /**
     * Processes the rendering method, rendering parameters, and texture for a specified face.
     *
     * @param face the name of the face (e.g., up, down, north, south, east, west, *)
     * @param ambientOcclusion should ambient light shielding be applied when lighting?
     * @param faceDimming should it be dimmed according to the direction it is facing?
     * @param renderMethod the rendering method to be used
     * @param texture the texture's name
     */
    private void process(@NotNull String face, boolean ambientOcclusion, boolean faceDimming, @NotNull RenderMethod renderMethod, @NotNull String texture) {
        this.tag.putCompound(face, new CompoundTag()
                .putBoolean("ambient_occlusion", ambientOcclusion)
                .putBoolean("face_dimming", faceDimming)
                .putString("render_method", renderMethod.name().toLowerCase(Locale.ENGLISH))
                .putString("texture", texture));
    }

    /**
     * Converts the materials to a CompoundTag.
     *
     * @return the CompoundTag representation of the materials
     */
    public CompoundTag toCompoundTag() {
        return tag;
    }

    /**
     * The enum representing rendering methods.
     *
     * @see <a href="https://wiki.bedrock.dev/blocks/blocks-16.html#additional-notes">wiki.bedrock.dev</a>
     */
    public enum RenderMethod {
        OPAQUE,
        ALPHA_TEST,
        BLEND,
        DOUBLE_SIDED
    }
}