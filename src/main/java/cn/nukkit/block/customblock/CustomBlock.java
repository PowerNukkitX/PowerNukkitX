package cn.nukkit.block.customblock;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFallableMeta;
import cn.nukkit.block.BlockMeta;
import cn.nukkit.block.customblock.type.MaterialsFactory;
import cn.nukkit.blockproperty.*;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    /* 下面两个方法需要被覆写,请使用接口的定义 */
    default int getId() {
        return Block.CUSTOM_BLOCK_ID_MAP.get(getNamespace().toLowerCase(Locale.ENGLISH));
    }

    default String getName() {
        return this.getNamespace().split(":")[1].toLowerCase(Locale.ENGLISH);
    }

    Item toItem();

    default Block toCustomBlock() {
        return ((Block) this).clone();
    }

    default Block toCustomBlock(int meta) {
        var block = toCustomBlock();
        if (block instanceof BlockMeta || block instanceof BlockFallableMeta) {
            block.getMutableState().setDataStorageFromInt(meta, true);
        }
        return block;
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
     * 将几何文件中的face(面)名称映射到实际的材质实例
     */
    default CompoundTag getMaterials() {
        return MaterialsFactory.of(new CompoundTag()).process("*", "opaque", getTexture()).build();
    }

    /**
     * 以度为单位设置块围绕立方体中心的旋转,旋转顺序为 xyz.角度必须是90的倍数。
     */
    default Vector3 getRotation() {
        return new Vector3(0, 0, 0);
    }

    @Nullable
    default ListTag<StringTag> getBlockTags() {
        return null;
    }

    /**
     * 控制自定义方块的形状<br>
     * Geometry identifier from geo file in 'RP/models/blocks' folder
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

    default boolean reverseSending() {
        return true;
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
            var tmpList = new ArrayList<>(properties.getAllProperties());
            if (reverseSending()) {
                Collections.reverse(tmpList);
            }
            for (var each : tmpList) {
                if (each.getProperty() instanceof BooleanBlockProperty booleanBlockProperty) {
                    nbtList.add(new CompoundTag().putString("name", booleanBlockProperty.getName())
                            .putList(new ListTag<>("enum")
                                    .add(new IntTag("", 0))
                                    .add(new IntTag("", 1))));
                } else if (each.getProperty() instanceof IntBlockProperty intBlockProperty) {
                    var enumList = new ListTag<IntTag>("enum");
                    for (int i = intBlockProperty.getMinValue(); i <= intBlockProperty.getMaxValue(); i++) {
                        enumList.add(new IntTag("", i));
                    }
                    nbtList.add(new CompoundTag().putString("name", intBlockProperty.getName()).putList(enumList));
                } else if (each.getProperty() instanceof UnsignedIntBlockProperty unsignedIntBlockProperty) {
                    var enumList = new ListTag<LongTag>("enum");
                    for (long i = unsignedIntBlockProperty.getMinValue(); i <= unsignedIntBlockProperty.getMaxValue(); i++) {
                        enumList.add(new LongTag("", i));
                    }
                    nbtList.add(new CompoundTag().putString("name", unsignedIntBlockProperty.getName()).putList(enumList));
                } else if (each.getProperty() instanceof ArrayBlockProperty<?> arrayBlockProperty) {
                    if (arrayBlockProperty.isOrdinal()) {
                        var enumList = new ListTag<IntTag>("enum");
                        var universe = arrayBlockProperty.getUniverse();
                        for (int i = 0, universeLength = universe.length; i < universeLength; i++) {
                            enumList.add(new IntTag("", i));
                        }
                        nbtList.add(new CompoundTag().putString("name", arrayBlockProperty.getName()).putList(enumList));
                    } else {
                        var enumList = new ListTag<StringTag>("enum");
                        for (var e : arrayBlockProperty.getUniverse()) {
                            enumList.add(new StringTag("", e.toString()));
                        }
                        nbtList.add(new CompoundTag().putString("name", arrayBlockProperty.getName()).putList(enumList));
                    }
                }
            }
            return nbtList;
        }
        return null;
    }

    /**
     * 对自动生成的ComponentNBT进行处理
     *
     * @param componentNBT 自动生成的component NBT
     * @return 处理后的ComponentNBT
     */
    default CompoundTag componentNBTProcessor(CompoundTag componentNBT) {
        return componentNBT;
    }

    default BlockPropertyData getBlockPropertyData() {
        //内部处理components组件
        var compoundTag = new CompoundTag()
                .putCompound("minecraft:creative_category", new CompoundTag()
                        .putString("category", this.getCreativeCategory()))
                .putCompound("minecraft:friction", new CompoundTag()
                        .putFloat("value", (float) this.getFrictionFactor()))
                .putCompound("minecraft:explosion_resistance", new CompoundTag()
                        .putInt("value", (int) this.getResistance()))
                .putCompound("minecraft:block_light_filter", new CompoundTag()
                        .putFloat("lightLevel", (byte) this.getLightFilter()))
                .putCompound("minecraft:light_emission", new CompoundTag()
                        .putByte("emission", (byte) this.getLightLevel()))
                .putCompound("minecraft:rotation", new CompoundTag()
                        .putFloat("x", (float) this.getRotation().x)
                        .putFloat("y", (float) this.getRotation().y)
                        .putFloat("z", (float) this.getRotation().z))
                .putCompound("minecraft:destructible_by_mining", new CompoundTag()
                        .putFloat("value", (float) (this.calculateBreakTime() * 2 / 3)));
        if (this.getTexture() != null) {
            compoundTag.putCompound("minecraft:material_instances", new CompoundTag()
                    .putCompound("mappings", new CompoundTag())
                    .putCompound("materials", this.getMaterials()));
        }
        if (!this.getCreativeCategoryGroup().isEmpty()) {
            compoundTag.getCompound("minecraft:creative_category").putString("group", this.getCreativeCategoryGroup());
        }
        //设置方块对应的几何模型，需要在资源包定义
        if (!this.getGeometry().isEmpty()) {
            compoundTag.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", this.getGeometry()));
        } else {
            compoundTag.putCompound("minecraft:unit_cube", new CompoundTag());
        }
        //提供外部操作components组件
        compoundTag = componentNBTProcessor(compoundTag);
        //方块components
        var nbt = new CompoundTag()
                .putCompound("components", compoundTag);
        //方块BlockTags
        if (getBlockTags() != null) nbt.putList(getBlockTags());
        //设置方块的permutations
        if (getPermutations() != null) {
            var permutations = getPermutations();
            permutations.setName("permutations");
            nbt.putList(permutations);
        }
        //设置方块的properties
        var propertiesNBT = getPropertiesNBT();
        if (propertiesNBT != null) {
            propertiesNBT.setName("properties");
            nbt.putList(propertiesNBT);
        }
        //molang版本
        nbt.putInt("molangVersion", 6);
        return new BlockPropertyData(this.getNamespace(), nbt);
    }
}
