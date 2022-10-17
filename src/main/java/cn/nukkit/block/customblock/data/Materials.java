package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;
import lombok.NonNull;

import java.util.Locale;

/**
 * Materials工厂类
 */
public class Materials {
    /**
     * 被加工的NBT
     */
    private final CompoundTag tag;

    private Materials() {
        this.tag = new CompoundTag();
    }

    /**
     * 静态构造方法
     *
     * @param Materials 被加工的Materials CompoundTag
     * @return 工厂实例
     */
    public static Materials builder() {
        return new Materials();
    }

    /**
     * 向Materials CompoundTag中填写内容
     *
     * @param face         指定几何模型中对应的面,可选值:<br>'up', 'down', 'north', 'south', 'east', 'west', or '*'.
     * @param renderMethod 要使用的渲染方法 可选值:<br>'opaque', 'alpha_test', 'blend', 'double_sided'.
     * @param texture      指定几何模型中对应的面材质的纹理名称。
     */
    public Materials up(RenderMethod renderMethod, String texture) {
        this.process("up", true, true, renderMethod, texture);
        return this;
    }

    public Materials up(RenderMethod renderMethod, Boolean ambientOcclusion, Boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }


    public Materials down(RenderMethod renderMethod, String texture) {
        this.process("down", true, true, renderMethod, texture);
        return this;
    }

    public Materials down(RenderMethod renderMethod, Boolean ambientOcclusion, Boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    public Materials north(RenderMethod renderMethod, String texture) {
        this.process("north", true, true, renderMethod, texture);
        return this;
    }

    public Materials north(RenderMethod renderMethod, Boolean ambientOcclusion, Boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    public Materials south(RenderMethod renderMethod, String texture) {
        this.process("south", true, true, renderMethod, texture);
        return this;
    }

    public Materials south(RenderMethod renderMethod, Boolean ambientOcclusion, Boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    public Materials east(RenderMethod renderMethod, String texture) {
        this.process("any", true, true, renderMethod, texture);
        return this;
    }

    public Materials east(RenderMethod renderMethod, Boolean ambientOcclusion, Boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    public Materials west(RenderMethod renderMethod, String texture) {
        this.process("west", true, true, renderMethod, texture);
        return this;
    }

    public Materials west(RenderMethod renderMethod, Boolean ambientOcclusion, Boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    public Materials any(RenderMethod renderMethod, String texture) {
        this.process("*", true, true, renderMethod, texture);
        return this;
    }

    public Materials any(RenderMethod renderMethod, Boolean ambientOcclusion, Boolean faceDimming, String texture) {
        this.process("up", ambientOcclusion, faceDimming, renderMethod, texture);
        return this;
    }

    /**
     * 向Materials CompoundTag中填写内容
     *
     * @param face             指定几何模型中对应的面,可选值:<br>'up', 'down', 'north', 'south', 'east', 'west', or '*'.
     * @param ambientOcclusion 这种材质在照明时是否应该应用环境光遮蔽?
     * @param faceDimming      这种材料是否应该根据它所面对的方向变暗？
     * @param renderMethod     要使用的渲染方法 可选值:<br>'opaque', 'alpha_test', 'blend', 'double_sided'.
     * @param texture          指定几何模型中对应的面材质的纹理名称。
     */
    private void process(@NonNull String face, Boolean ambientOcclusion, Boolean faceDimming, @NonNull RenderMethod renderMethod, @NonNull String texture) {
        this.tag.putCompound(face, new CompoundTag()
                .putBoolean("ambient_occlusion", ambientOcclusion)
                .putBoolean("face_dimming", faceDimming)
                .putString("render_method", renderMethod.name().toLowerCase(Locale.ENGLISH))
                .putString("texture", texture));
    }

    public CompoundTag build() {
        return tag;
    }

    public enum RenderMethod {
        OPAQUE,
        ALPHA_TEST,
        BLEND,
        DOUBLE_SIDED
    }
}
