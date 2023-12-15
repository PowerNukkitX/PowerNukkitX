package cn.nukkit.block.state;

import cn.nukkit.block.state.property.type.BlockPropertyType;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.HashUtils;
import com.google.common.base.Preconditions;

import java.util.*;

import static cn.nukkit.block.state.BlockProperties.computeSpecialValue;

/**
 * Allay Project 12/15/2023
 *
 * @author Cool_Loong
 */
record BlockStateImpl(String identifier,
                      int blockhash,
                      BlockPropertyType.BlockPropertyValue<?, ?, ?>[] blockPropertyValues,
                      CompoundTag blockStateTag,
                      Long specialValue
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
                propertyValues,
                buildBlockStateTag(identifier, propertyValues),
                computeSpecialValue(propertyValues)
        );
    }

    public BlockStateImpl(String identifier, int blockStateHash, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues, Long specialValue) {
        this(identifier,
                blockStateHash,
                propertyValues,
                buildBlockStateTag(identifier, propertyValues),
                specialValue
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
    public Map<BlockPropertyType<?>, BlockPropertyType.BlockPropertyValue<?, ?, ?>> getPropertyValues() {
        return Collections.unmodifiableMap(Arrays.stream(blockPropertyValues).collect(
                LinkedHashMap<BlockPropertyType<?>, BlockPropertyType.BlockPropertyValue<?, ?, ?>>::new,
                (hashMap, blockPropertyValue) -> hashMap.put(blockPropertyValue.getPropertyType(), blockPropertyValue),
                LinkedHashMap::putAll));
    }

    @Override
    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> DATATYPE getPropertyValue(PROPERTY p) {
        for (var property : blockPropertyValues) {
            if (property.getPropertyType() == p) {
                return (DATATYPE) property.getValue();
            }
        }
        throw new IllegalArgumentException("Property " + p + " is not supported by this block");
    }

    @Override
    public BlockState setProperty(BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue) {
        var newPropertyValues = new BlockPropertyType.BlockPropertyValue<?, ?, ?>[this.blockPropertyValues.length];
        var succeed = false;
        for (int i = 0; i < blockPropertyValues.length; i++) {
            if (blockPropertyValues[i].getPropertyType() == propertyValue.getPropertyType()) {
                succeed = true;
                newPropertyValues[i] = propertyValue;
            } else newPropertyValues[i] = blockPropertyValues[i];
        }
        if (!succeed) {
            throw new IllegalArgumentException("Property " + propertyValue.getPropertyType() + " is not supported by this block");
        }
        return getNewBlockState(newPropertyValues);
    }

    @Override
    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> BlockState setProperty(PROPERTY property, DATATYPE value) {
        return setProperty(property.createValue(value));
    }

    @Override
    public BlockState setProperties(List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> propertyValues) {
        var newPropertyValues = new BlockPropertyType.BlockPropertyValue<?, ?, ?>[this.blockPropertyValues.length];
        var succeedCount = 0;
        var succeed = new boolean[propertyValues.size()];
        for (int i = 0; i < blockPropertyValues.length; i++) {
            int index;
            if ((index = propertyValues.indexOf(blockPropertyValues[i])) != -1) {
                succeedCount++;
                succeed[index] = true;
                newPropertyValues[i] = propertyValues.get(index);
            } else newPropertyValues[i] = blockPropertyValues[i];
        }
        if (succeedCount != propertyValues.size()) {
            var errorMsgBuilder = new StringBuilder("Properties ");
            for (int i = 0; i < propertyValues.size(); i++) {
                if (!succeed[i]) {
                    errorMsgBuilder.append(propertyValues.get(i).getPropertyType().getName());
                    if (i != propertyValues.size() - 1)
                        errorMsgBuilder.append(", ");
                }
            }
            errorMsgBuilder.append(" are not supported by this block");
            throw new IllegalArgumentException(errorMsgBuilder.toString());
        }
        return getNewBlockState(newPropertyValues);
    }

    @Override
    public CompoundTag getBlockStateTag() {
        return this.blockStateTag;
    }

    @Override
    public Item toItem() {
        return null;
    }

    private BlockState getNewBlockState(BlockPropertyType.BlockPropertyValue<?, ?, ?>[] values) {
        BlockProperties blockProperties = BlockPropertiesRegistry.get(this.identifier);
        Preconditions.checkNotNull(blockProperties);
        byte bits = blockProperties.getSpecialValueBits();
        if (bits <= 64) {
            return blockProperties.getBlockStateBySpecialValue(computeSpecialValue(bits, values));
        } else {
            return blockProperties.getBlockStateByHash(HashUtils.computeBlockStateHash(this.identifier, values));
        }
    }
}
