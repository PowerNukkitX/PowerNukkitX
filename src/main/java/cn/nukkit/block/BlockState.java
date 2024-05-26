package cn.nukkit.block;

import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.CompoundTagView;
import cn.nukkit.registry.Registries;
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
    static BlockState makeUnknownBlockState(int hash, CompoundTag blockTag) {
        return BlockStateImpl.makeUnknownBlockState(hash, blockTag);
    }

    static short computeSpecialValue(BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        byte specialValueBits = 0;
        for (var value : propertyValues) specialValueBits += value.getPropertyType().getBitSize();
        return computeSpecialValue(specialValueBits, propertyValues);
    }

    static short computeSpecialValue(byte specialValueBits, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        short specialValue = 0;
        for (var value : propertyValues) {
            specialValue |= (short) (((short) value.getIndex()) << (specialValueBits - value.getPropertyType().getBitSize()));
            specialValueBits -= value.getPropertyType().getBitSize();
        }
        return specialValue;
    }

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

    default boolean isDefaultState() {
        return Registries.BLOCK.getBlockProperties(getIdentifier()).getDefaultState() == this;
    }

    default Block toBlock() {
        return Block.get(this);
    }

    default Block toBlock(Position position) {
        return Block.get(this, position);
    }

    default Item toItem() {
        return toBlock().toItem();
    }
}
