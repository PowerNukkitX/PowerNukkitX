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

    <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> BlockState setPropertyValue(BlockProperties properties, PROPERTY property, DATATYPE value);

    BlockState setPropertyValue(BlockProperties properties, BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue);

    BlockState setPropertyValues(BlockProperties properties, BlockPropertyType.BlockPropertyValue<?, ?, ?>... values);

    default Block toBlock() {
        return Block.get(this);
    }
}
