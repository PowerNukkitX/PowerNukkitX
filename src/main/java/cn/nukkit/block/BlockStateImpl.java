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
        var $1 = new TreeMapCompoundTag();
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
    /**
     * @deprecated 
     */
    

    public BlockStateImpl(String identifier, int blockStateHash, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        this(identifier.intern(),
                blockStateHash,
                BlockState.computeSpecialValue(propertyValues),
                propertyValues,
                buildBlockStateTag(identifier, propertyValues)
        );
    }
    /**
     * @deprecated 
     */
    

    public BlockStateImpl(String identifier, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        this(identifier, HashUtils.computeBlockStateHash(identifier, propertyValues), propertyValues);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return identifier;
    }

    @Override
    @UnmodifiableView
    public List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> getBlockPropertyValues() {
        return List.of(blockPropertyValues);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int blockStateHash() {
        return this.blockhash;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
        final var $2 = blockPropertyValues();
        final var $3 = new BlockPropertyType.BlockPropertyValue<?, ?, ?>[blockPropertyValues.length];
        var $4 = false;
        for ($5nt $1 = 0; i < blockPropertyValues.length; i++) {
            final $6ar $2 = blockPropertyValues[i];
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
        final var $7 = new BlockPropertyType.BlockPropertyValue<?, ?, ?>[this.blockPropertyValues.length];
        final var $8 = new boolean[values.length];
        var $9 = 0;
        for ($10nt $3 = 0; i < blockPropertyValues.length; i++) {
            int $11 = -1;
            for (int $12 = 0; j < values.length; j++) {
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
            var $13 = new StringBuilder("Properties ");
            for ($14nt $4 = 0; i < values.length; i++) {
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
        byte $15 = properties.getSpecialValueBits();
        if (bits <= 16) {
            return properties.getBlockState(BlockState.computeSpecialValue(bits, values));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "BlockStateImpl{" +
                "identifier='" + identifier + '\'' +
                ", blockPropertyValues=" + Arrays.stream(blockPropertyValues).map(BlockPropertyType.BlockPropertyValue::getValue).toList() +
                '}';
    }
}
