package cn.nukkit.block;

import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.CompoundTagView;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.HashUtils;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.TreeMap;

import static cn.nukkit.block.BlockProperties.computeSpecialValue;

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
    public static short computeSpecialValue(BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        byte specialValueBits = 0;
        for (var value : propertyValues) specialValueBits += value.getPropertyType().getBitSize();
        return computeSpecialValue(specialValueBits, propertyValues);
    }

    //todo match vanilla
    public static short computeSpecialValue(byte specialValueBits, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        short specialValue = 0;
        for (var value : propertyValues) {
            specialValue |= (short) (((short) value.getIndex()) << (specialValueBits - value.getPropertyType().getBitSize()));
            specialValueBits -= value.getPropertyType().getBitSize();
        }
        return specialValue;
    }

    private static CompoundTagView buildBlockStateTag(String identifier, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        //build block state tag
        var states = new CompoundTag("", new TreeMap<>());
        for (var value : propertyValues) {
            switch (value.getPropertyType().getType()) {
                case INT -> states.putInt(value.getPropertyType().getName(), (int) value.getSerializedValue());
                case ENUM -> states.putString(value.getPropertyType().getName(), value.getSerializedValue().toString());
                case BOOLEAN -> states.putByte(value.getPropertyType().getName(), (byte) value.getSerializedValue());
            }
        }
        return new CompoundTagView(new CompoundTag()
                .putString("name", identifier)
                .putCompound("states", states)
                .putInt("version", ProtocolInfo.BLOCK_STATE_VERSION_NO_REVISION));
    }

    public BlockStateImpl(String identifier, int blockStateHash, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        this(identifier.intern(),
                blockStateHash,
                computeSpecialValue(propertyValues),
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
        throw new IllegalArgumentException("Property " + p + " is not supported by this block");
    }

    @Override
    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> void setPropertyValue(Block block, PROPERTY property, DATATYPE value) {
        setPropertyValue(block, property.createValue(value));
    }

    @Override
    public void setPropertyValue(Block block, BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue) {
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
            throw new IllegalArgumentException("Property " + propertyValue.getPropertyType() + " is not supported by this block");
        }
        block.blockstate = getNewBlockState(block, newPropertyValues);
    }

    @Override
    public void setPropertyValues(Block block, BlockPropertyType.BlockPropertyValue<?, ?, ?>... values) {
        final var blockPropertyValues = blockPropertyValues();
        final var newPropertyValues = new BlockPropertyType.BlockPropertyValue<?, ?, ?>[blockPropertyValues.length];
        final var propertyValues = List.of(values);
        final var succeed = new boolean[propertyValues.size()];
        var succeedCount = 0;
        for (int i = 0; i < blockPropertyValues.length; i++) {
            final var v = blockPropertyValues[i];
            int index;
            if ((index = propertyValues.indexOf(v)) != -1) {
                succeedCount++;
                succeed[index] = true;
                newPropertyValues[i] = propertyValues.get(index);
            } else newPropertyValues[i] = v;
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
        block.blockstate = getNewBlockState(block, newPropertyValues);
    }

    private BlockState getNewBlockState(Block block, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] values) {
        Preconditions.checkNotNull(block.getProperties());
        byte bits = block.getProperties().getSpecialValueBits();
        if (bits <= 16) {
            return block.getProperties().getBlockState(computeSpecialValue(bits, values));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "BlockStateImpl{" +
                "identifier='" + identifier + '\'' +
                ", blockhash=" + blockhash +
                ", specialValue=" + specialValue +
                ", blockPropertyValues=" + blockPropertyValues +
                '}';
    }
}
