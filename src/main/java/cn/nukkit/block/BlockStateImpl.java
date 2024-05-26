package cn.nukkit.block;

import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.CompoundTagView;
import cn.nukkit.nbt.tag.LinkedCompoundTag;
import cn.nukkit.nbt.tag.TreeMapCompoundTag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.HashUtils;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.List;

/**
 * Allay Project 12/15/2023
 *
 * @author Cool_Loong
 */
record BlockStateImpl(String identifier,
                      int blockhash,
                      short specialValue,
                      BlockPropertyType.BlockPropertyValue<?, ?, ?>[] blockPropertyValues,
                      CompoundTagView blockStateTag
) implements BlockState {
    static Int2ObjectOpenHashMap<BlockStateImpl> UNKNOWN_BLOCK_STATE_CACHE = new Int2ObjectOpenHashMap<>();

    static BlockStateImpl makeUnknownBlockState(int hash, CompoundTag blockTag) {
        return UNKNOWN_BLOCK_STATE_CACHE.computeIfAbsent(hash, h -> new BlockStateImpl(BlockID.UNKNOWN, -2, (short) 0, new BlockPropertyType.BlockPropertyValue[0], new CompoundTagView(new LinkedCompoundTag()
                .putString("name", BlockID.UNKNOWN)
                .putCompound("states", new CompoundTag())
                .putCompound("Block", blockTag)
                .putInt("version", ProtocolInfo.BLOCK_STATE_VERSION_NO_REVISION))));
    }

    private static CompoundTagView buildBlockStateTag(String identifier, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        //build block state tag
        var states = new TreeMapCompoundTag();
        for (var value : propertyValues) {
            switch (value.getPropertyType().getType()) {
                case INT -> states.putInt(value.getPropertyType().getName(), (int) value.getSerializedValue());
                case ENUM -> states.putString(value.getPropertyType().getName(), value.getSerializedValue().toString());
                case BOOLEAN -> states.putByte(value.getPropertyType().getName(), (byte) value.getSerializedValue());
            }
        }
        return new CompoundTagView(new LinkedCompoundTag()
                .putString("name", identifier)
                .putCompound("states", states)
                .putInt("version", ProtocolInfo.BLOCK_STATE_VERSION_NO_REVISION));
    }

    public BlockStateImpl(String identifier, int blockStateHash, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        this(identifier.intern(),
                blockStateHash,
                BlockState.computeSpecialValue(propertyValues),
                propertyValues,
                buildBlockStateTag(identifier, propertyValues)
        );
    }

    public BlockStateImpl(String identifier, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        this(identifier, HashUtils.computeBlockStateHash(identifier, propertyValues), propertyValues);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    @UnmodifiableView
    public List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> getBlockPropertyValues() {
        return List.of(blockPropertyValues);
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
    public CompoundTagView getBlockStateTag() {
        return blockStateTag;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> DATATYPE getPropertyValue(PROPERTY p) {
        for (var property : this.blockPropertyValues()) {
            if (property.getPropertyType() == p) {
                return (DATATYPE) property.getValue();
            }
        }
        throw new IllegalArgumentException("Property " + p + " is not supported by this block " + this.identifier);
    }

    @Override
    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> BlockState setPropertyValue(BlockProperties properties, PROPERTY property, DATATYPE value) {
        return setPropertyValue(properties, property.createValue(value));
    }

    @Override
    public BlockState setPropertyValue(BlockProperties properties, BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue) {
        final var blockPropertyValues = blockPropertyValues();
        final var newPropertyValues = new BlockPropertyType.BlockPropertyValue<?, ?, ?>[blockPropertyValues.length];
        var succeed = false;
        for (int i = 0; i < blockPropertyValues.length; i++) {
            final var v = blockPropertyValues[i];
            if (v.getPropertyType() == propertyValue.getPropertyType()) {
                succeed = true;
                newPropertyValues[i] = propertyValue;
            } else newPropertyValues[i] = v;
        }
        if (!succeed) {
            throw new IllegalArgumentException("Property " + propertyValue.getPropertyType() + " is not supported by this block " + this.identifier);
        }
        return getNewBlockState(properties, newPropertyValues);
    }

    @Override
    public BlockState setPropertyValues(BlockProperties properties, BlockPropertyType.BlockPropertyValue<?, ?, ?>... values) {
        final var newPropertyValues = new BlockPropertyType.BlockPropertyValue<?, ?, ?>[this.blockPropertyValues.length];
        final var succeed = new boolean[values.length];
        var succeedCount = 0;
        for (int i = 0; i < blockPropertyValues.length; i++) {
            int index = -1;
            for (int j = 0; j < values.length; j++) {
                if (values[j].getPropertyType() == blockPropertyValues[i].getPropertyType()) {
                    index = j;
                    succeedCount++;
                    succeed[index] = true;
                    newPropertyValues[i] = values[j];
                }
            }
            if (index == -1) {
                newPropertyValues[i] = blockPropertyValues[i];
            }
        }
        if (succeedCount != values.length) {
            var errorMsgBuilder = new StringBuilder("Properties ");
            for (int i = 0; i < values.length; i++) {
                if (!succeed[i]) {
                    errorMsgBuilder.append(values[i].getPropertyType().getName());
                    if (i != values.length - 1)
                        errorMsgBuilder.append(", ");
                }
            }
            errorMsgBuilder.append(" are not supported by this block ").append(this.identifier);
            throw new IllegalArgumentException(errorMsgBuilder.toString());
        }
        return getNewBlockState(properties, newPropertyValues);
    }

    private BlockState getNewBlockState(BlockProperties properties, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] values) {
        Preconditions.checkNotNull(properties);
        byte bits = properties.getSpecialValueBits();
        if (bits <= 16) {
            return properties.getBlockState(BlockState.computeSpecialValue(bits, values));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "BlockStateImpl{" +
                "identifier='" + identifier + '\'' +
                ", blockPropertyValues=" + Arrays.stream(blockPropertyValues).map(BlockPropertyType.BlockPropertyValue::getValue).toList() +
                '}';
    }
}
