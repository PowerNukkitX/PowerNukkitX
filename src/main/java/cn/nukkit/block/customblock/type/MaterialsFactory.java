package cn.nukkit.block.customblock.type;

import cn.nukkit.nbt.tag.CompoundTag;
import lombok.NonNull;

/**
 * Materials工厂类
 */
public class MaterialsFactory {
    /**
     * 被加工的NBT
     */
    private final CompoundTag tag;

    private MaterialsFactory(CompoundTag Materials) {
        this.tag = Materials;
    }

    /**
     * 静态构造方法
     *
     * @param Materials 被加工的Materials CompoundTag
     * @return 工厂实例
     */
    public static MaterialsFactory of(CompoundTag Materials) {
        return new MaterialsFactory(Materials);
    }

    /**
     * 向Materials CompoundTag中填写内容
     *
     * @param face         指定几何模型中对应的面,可选值:<br>'up', 'down', 'north', 'south', 'east', 'west', or '*'.
     * @param renderMethod 要使用的渲染方法 可选值:<br>'opaque', 'alpha_test', 'blend', 'double_sided'.
     * @param texture      指定几何模型中对应的面材质的纹理名称。
     */
    public MaterialsFactory process(@NonNull String face, @NonNull String renderMethod, @NonNull String texture) {
        this.process(face, true, true, renderMethod, texture);
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
    public MaterialsFactory process(@NonNull String face, Boolean ambientOcclusion, Boolean faceDimming, @NonNull String renderMethod, @NonNull String texture) {
        this.tag.putCompound(face, new CompoundTag()
                .putBoolean("ambient_occlusion", ambientOcclusion)
                .putBoolean("face_dimming", faceDimming)
                .putString("render_method", renderMethod)
                .putString("texture", texture));
        return this;
    }

    public CompoundTag build() {
        return tag;
    }
}
