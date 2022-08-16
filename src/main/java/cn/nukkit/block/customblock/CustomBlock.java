package cn.nukkit.block.customblock;

import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.*;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.*;
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
     *
     * @return Permutations NBT Tag
     */
    @Nullable
    default ListTag<CompoundTag> getPermutations() {
        return null;
    }

    /**
     * 获取方块属性NBT定义
     *
     * @return BlockProperties in NBT Tag format
     */
    @Nullable
    default ListTag<CompoundTag> getPropertiesNBT() {
        if (this instanceof Block block) {
            var properties = block.getProperties();
            if (properties == CommonBlockProperties.EMPTY_PROPERTIES || properties.getAllProperties().size() == 0) {
                return null;
            }
            var nbtList = new ListTag<CompoundTag>("properties");
            for (var each : properties.getAllProperties()) {
                if (each.getProperty() instanceof BooleanBlockProperty booleanBlockProperty) {
                    nbtList.add(new CompoundTag().putString("name", booleanBlockProperty.getName())
                            .putList(new ListTag<>("enum")
                                    .add(new IntTag("", 0))
                                    .add(new IntTag("", 1))));
                } else if (each.getProperty() instanceof IntBlockProperty intBlockProperty) {
                    var enumList = new ListTag<IntTag>("enum");
                    for (int i = intBlockProperty.getMinValue(); i < intBlockProperty.getMaxValue(); i++) {
                        enumList.add(new IntTag("", i));
                    }
                    nbtList.add(new CompoundTag().putString("name", intBlockProperty.getName()).putList(enumList));
                } else if (each.getProperty() instanceof UnsignedIntBlockProperty unsignedIntBlockProperty) {
                    var enumList = new ListTag<LongTag>("enum");
                    for (long i = unsignedIntBlockProperty.getMinValue(); i < unsignedIntBlockProperty.getMaxValue(); i++) {
                        enumList.add(new LongTag("", i));
                    }
                    nbtList.add(new CompoundTag().putString("name", unsignedIntBlockProperty.getName()).putList(enumList));
                } else if (each.getProperty() instanceof ArrayBlockProperty<?> arrayBlockProperty) {
                    var enumList = new ListTag<StringTag>("enum");
                    for (var e : arrayBlockProperty.getUniverse()) {
                        enumList.add(new StringTag("", e.toString()));
                    }
                    nbtList.add(new CompoundTag().putString("name", arrayBlockProperty.getName()).putList(enumList));
                }
            }
            return nbtList;
        }
        return null;
    }

    /**
     * 对自动生成的ComponentNBT进行处理
     * @param componentNBT 自动生成的component NBT
     * @return 处理后的ComponentNBT
     */
    default CompoundTag componentNBTProcessor(CompoundTag componentNBT) {
        return componentNBT;
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
                .putCompound("minecraft:geometry", new CompoundTag()
                        .putString("value", "geometry.blocks"))
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
        compoundTag = componentNBTProcessor(compoundTag);
        var nbt = new CompoundTag().putCompound("components", compoundTag);
        if (getPermutations() != null) {
            var permutations = getPermutations();
            permutations.setName("permutations");
            nbt.putList(permutations);
        }
        nbt.putInt("molangVersion", 0);
        var propertiesNBT = getPropertiesNBT();
        if (propertiesNBT != null) {
            propertiesNBT.setName("properties");
            nbt.putList(propertiesNBT);
        }
        return new BlockPropertyData(this.getNamespace().toLowerCase(Locale.ENGLISH), nbt);
    }
}
