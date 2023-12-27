package cn.nukkit.block;

import cn.nukkit.block.property.type.BlockPropertyType;
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

    short specialValue();

    long unsignedBlockStateHash();

    @UnmodifiableView
    List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> getBlockPropertyValues();

    @UnmodifiableView
    CompoundTagView getBlockStateTag();

    <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> DATATYPE getPropertyValue(PROPERTY p);

    <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> void setPropertyValue(Block block, PROPERTY property, DATATYPE value);

    void setPropertyValue(Block block, BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue);

    void setPropertyValues(Block block, BlockPropertyType.BlockPropertyValue<?, ?, ?>... values);
}
