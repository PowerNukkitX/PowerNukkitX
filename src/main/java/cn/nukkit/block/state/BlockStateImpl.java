package cn.nukkit.block.state;

import cn.nukkit.block.state.property.type.BlockPropertyType;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.HashUtils;

import java.util.Arrays;
import java.util.TreeMap;

import static cn.nukkit.block.state.BlockProperties.computeSpecialValue;

/**
 * Allay Project 12/15/2023
 *
 * @author Cool_Loong
 */
record BlockStateImpl(String identifier,
                      int blockhash,
                      short specialValue,
                      BlockPropertyType.BlockPropertyValue<?, ?, ?>[] blockPropertyValues,
                      CompoundTag blockStateTag
) implements BlockState {
    private static CompoundTag buildBlockStateTag(String identifier, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        //build block state tag
        var states = new CompoundTag("", new TreeMap<>());
        for (var value : propertyValues) {
            switch (value.getPropertyType().getType()) {
                case INT -> states.putInt(value.getPropertyType().getName(), (int) value.getSerializedValue());
                case ENUM -> states.putString(value.getPropertyType().getName(), value.getSerializedValue().toString());
                case BOOLEAN -> states.putByte(value.getPropertyType().getName(), (byte) value.getSerializedValue());
            }
        }
        return new CompoundTag()
                .putString("name", identifier)
                .putCompound("states", states)
                .putInt("version", ProtocolInfo.BLOCK_STATE_VERSION_NO_REVISION);
    }

    public BlockStateImpl(String identifier, int blockStateHash, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        this(identifier,
                blockStateHash,
                computeSpecialValue(propertyValues),
                propertyValues,
                buildBlockStateTag(identifier, propertyValues)
        );
    }

    public BlockStateImpl(String identifier, int blockStateHash, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues, short specialValue) {
        this(identifier,
                blockStateHash,
                specialValue,
                propertyValues,
                buildBlockStateTag(identifier, propertyValues)
        );
    }

    public BlockStateImpl(String identifier, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        this(identifier, HashUtils.computeBlockStateHash(identifier, Arrays.stream(propertyValues).toList()), propertyValues);
    }

    @Override
    public int blockStateHash() {
        return this.blockhash;
    }

    @Override
    public long unsignedBlockStateHash() {
        return Integer.toUnsignedLong(this.blockhash);
    }

    @Override
    public CompoundTag getBlockStateTag() {
        return this.blockStateTag;
    }

    @Override
    public BlockPropertyType.BlockPropertyValue<?, ?, ?>[] getBlockPropertyValues() {
        return blockPropertyValues;
    }
}
