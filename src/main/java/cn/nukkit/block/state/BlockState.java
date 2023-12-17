package cn.nukkit.block.state;

import cn.nukkit.block.state.property.type.BlockPropertyType;
import cn.nukkit.nbt.tag.CompoundTagView;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Allay Project 2023/4/29
 *
 * @author daoge_cmd
 */
@Unmodifiable
public interface BlockState {
    String getIdentifier();

    int blockStateHash();

    long unsignedBlockStateHash();

    @UnmodifiableView
    List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> getBlockPropertyValues();

    @UnmodifiableView
    CompoundTagView getBlockStateTag();
}
