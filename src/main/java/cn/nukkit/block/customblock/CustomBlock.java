package cn.nukkit.block.customblock;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFallableMeta;
import cn.nukkit.block.BlockMeta;
import cn.nukkit.blockproperty.*;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * 继承这个类实现自定义方块
 * 重写Block中的方法控制方块属性
 */
public interface CustomBlock {
    double getFrictionFactor();

    double getResistance();

    int getLightFilter();

    int getLightLevel();

    double calculateBreakTime();

    String getNamespaceId();

    Item toItem();

    CustomBlockDefinition getDefinition();

    /* 下面两个方法需要被手动覆写,请使用接口的定义 */
    default int getId() {
        return Block.CUSTOM_BLOCK_ID_MAP.get(getNamespaceId().toLowerCase(Locale.ENGLISH));
    }

    default String getName() {
        return this.getNamespaceId().split(":")[1].toLowerCase(Locale.ENGLISH);
    }

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
}
