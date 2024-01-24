package cn.nukkit.block.property.type;

import java.util.List;
import java.util.Objects;

/**
 * Allay Project 2023/3/19
 *
 * @author daoge_cmd
 */
public abstract sealed class BaseBlockPropertyType<DATATYPE> implements BlockPropertyType<DATATYPE> permits BooleanPropertyType, EnumPropertyType, IntPropertyType {

    protected final String name;
    protected final List<DATATYPE> validValues;
    protected final DATATYPE defaultValue;
    protected final byte bitSize;

    protected BaseBlockPropertyType(String name, List<DATATYPE> validValues, DATATYPE defaultValue, byte bitSize) {
        Objects.requireNonNull(defaultValue);
        this.name = name;
        this.validValues = validValues;
        this.defaultValue = defaultValue;
        this.bitSize = bitSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DATATYPE getDefaultValue() {
        return defaultValue;
    }

    @Override
    public List<DATATYPE> getValidValues() {
        return validValues;
    }

    @Override
    public byte getBitSize() {
        return bitSize;
    }
}
