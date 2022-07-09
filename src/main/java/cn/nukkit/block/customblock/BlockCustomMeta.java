//todo 20220709 实现原版多状态方块
/*package cn.nukkit.block.customblock;

import cn.nukkit.blockproperty.*;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Log4j2
public abstract class BlockCustomMeta extends BlockCustom {
    protected List<BlockProperty<?>> propertyList = new ArrayList<>();

    protected BlockCustomMeta() {
        this(0);
    }

    protected BlockCustomMeta(int meta) throws InvalidBlockPropertyMetaException {
        if (meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true);
        }
    }
    *//**
 * 添加方块的属性到{@link BlockCustomMeta#propertyList}
 *//*
    protected abstract void properties();

    @Override
    public BlockPropertyData getBlockPropertyData() {
        if(initialized.compareAndSet(false, true)){
            initBlockPropertyData();
            properties();
        }
        if (!propertyList.isEmpty()) {
            var properties = new ListTag<CompoundTag>("properties");
            for (var property : propertyList) {
                if (property instanceof IntBlockProperty property1) {
                    properties.add(new CompoundTag()
                            .putIntArray("enum", IntStream.rangeClosed(property1.getMinValue(), property1.getMaxValue()).toArray())
                            .putString("name", property1.getName()));
                } else if (property instanceof BooleanBlockProperty property2) {
                    properties.add(new CompoundTag()
                            .putByteArray("enum", new byte[]{0b01})
                            .putString("name", property2.getName()));
                } else if (property instanceof ArrayBlockProperty<?> property3) {
                    if(property3.getValueClass().equals(String.class)){
                        properties.add(new CompoundTag()
                                .putStringArray("enum",(String[]) property3.getUniverse())
                                .putString("name", property3.getName()));
                    }
                }
            }
            compoundTag.putList(properties);
        }
        return new BlockPropertyData(this.getNamespace(), new CompoundTag().putCompound("components", compoundTag));
    }

    @Override
    public BlockProperties getProperties() {
        if(propertyList.isEmpty()) return CommonBlockProperties.EMPTY_PROPERTIES;
        return new BlockProperties(propertyList.toArray(new BlockProperty<?>[]{}));
    }
}*/
