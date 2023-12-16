package cn.nukkit.block.state;

import cn.nukkit.block.state.property.type.BlockPropertyType;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Allay Project 2023/4/29
 *
 * @author daoge_cmd
 */
public interface BlockState {
    BlockPropertyType.BlockPropertyValue<?, ?, ?>[] getBlockPropertyValues();

    int blockStateHash();

    long unsignedBlockStateHash();

    CompoundTag getBlockStateTag();
}
