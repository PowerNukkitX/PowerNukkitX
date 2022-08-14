package cn.nukkit.block.customblock;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * 继承这个类实现自定义方块
 * 重写Block中的方法控制方块属性
 */
public interface CustomBlock {
    /**
     * 控制自定义方块的命名空间<br>(例如 wiki:test_block)
     */
    String getNamespace();

    /**
     * 控制自定义方块所用的材质名称<br>(例如材质图片test.png设置test)
     */
    String getTexture();

    /* 以下几个方法需要被手动覆写 */
    double getFrictionFactor();

    double getResistance();

    int getLightFilter();

    int getLightLevel();

    double calculateBreakTime();

    Item toItem();

    default Block toCustomBlock() {
        return ((Block) this).clone();
    }

    /**
     * 控制自定义方块在创造栏中的分类,默认值construction
     *
     * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
     */
    default String getCreativeCategory() {
        return "construction";
    }

    /**
     * 控制自定义方块在创造栏中的组
     *
     * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
     */
    default String getCreativeCategoryGroup() {
        return "";
    }

    /**
     * 控制自定义方块的渲染方法<p>
     * 可选值:<br>opaque<br>alpha_test<br>blend<br>double_sided<p>
     * 默认值: "opaque"
     */
    default String getRenderMethod() {
        return "opaque";
    }

    /**
     * 控制自定义方块的形状<br>
     * Geometry identifier from geo file in 'RP/models/entity' folder
     */
    default String getGeometry() {
        return "";
    }

    /**
     * 控制自定义方块的客户端状态
     * @return Permutations NBT Tag
     */
    @Nullable
    default ListTag<CompoundTag> getPermutations() {
        return null;
    }

    /* 下面两个方法需要被覆写,请使用接口的定义 */
    default int getId() {
        return Block.CUSTOM_BLOCK_ID_MAP.get(getNamespace().toLowerCase(Locale.ENGLISH));
    }

    default String getName() {
        return this.getNamespace().split(":")[1].toLowerCase(Locale.ENGLISH);
    }

    default BlockPropertyData getBlockPropertyData() {
        var compoundTag = new CompoundTag()
                .putCompound("minecraft:creative_category", new CompoundTag()
                        .putString("category", this.getCreativeCategory()))
                .putCompound("minecraft:friction", new CompoundTag()
                        .putFloat("value", (float) this.getFrictionFactor()))
                .putCompound("minecraft:explosion_resistance", new CompoundTag()
                        .putInt("value", (int) this.getResistance()))
                .putCompound("minecraft:block_light_absorption", new CompoundTag()
                        .putFloat("value", (float) this.getLightFilter() / 15))
                .putCompound("minecraft:light_emission", new CompoundTag()
                        .putByte("emission", (byte) this.getLightLevel()))
                .putCompound("minecraft:rotation", new CompoundTag()
                        .putFloat("x", 0)
                        .putFloat("y", 0)
                        .putFloat("z", 0))
                .putCompound("minecraft:destructible_by_mining", new CompoundTag()
                        .putFloat("value", (float) (this.calculateBreakTime() * 2 / 3)))
                .putCompound("minecraft:material_instances", new CompoundTag()
                        .putCompound("mappings", new CompoundTag())
                        .putCompound("materials", new CompoundTag()
                                .putCompound("*", new CompoundTag()
                                        .putBoolean("ambient_occlusion", true)
                                        .putBoolean("face_dimming", true)
                                        .putString("render_method", this.getRenderMethod())
                                        .putString("texture", this.getTexture()))));
        if (!this.getCreativeCategoryGroup().isEmpty()) {
            compoundTag.getCompound("minecraft:creative_category").putString("group", this.getCreativeCategoryGroup());
        }
        if (!this.getGeometry().isEmpty()) {
            compoundTag.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", this.getGeometry()));
        }
        var nbt = new CompoundTag().putCompound("components", compoundTag);
        if (getPermutations() != null) {
            var permutations = getPermutations();
            permutations.setName("permutations");
            nbt.putList(permutations);
        }
        return new BlockPropertyData(this.getNamespace().toLowerCase(Locale.ENGLISH), new CompoundTag());
    }
}
