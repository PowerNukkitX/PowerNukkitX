package cn.nukkit.block.property.type;

import java.util.List;

/**
 * Allay Project 2023/3/19
 *
 * @author daoge_cmd
 */
public final class BooleanPropertyType extends BaseBlockPropertyType<Boolean> {

    private final BooleanPropertyValue FALSE = new BooleanPropertyValue(false);
    private final BooleanPropertyValue TRUE = new BooleanPropertyValue(true);

    private BooleanPropertyType(String name, Boolean defaultData) {
        super(name, List.of(true, false), defaultData, (byte) 1);
    }

    public static BooleanPropertyType of(String name, Boolean defaultData) {
        return new BooleanPropertyType(name, defaultData);
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    @Override
    public BooleanPropertyValue createValue(Boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public BooleanPropertyValue tryCreateValue(Object value) {
        if (value instanceof Boolean bool) {
            return bool ? TRUE : FALSE;
        } else if (value instanceof Number number) {
            var intValue = number.intValue();
            if (intValue == 0 || intValue == 1)
                return intValue == 1 ? TRUE : FALSE;
        }
        throw new IllegalArgumentException("Invalid value for boolean property type: " + value);
    }

    public final class BooleanPropertyValue extends BlockPropertyType.BlockPropertyValue<Boolean, BooleanPropertyType, Byte> {

        private final int index;

        BooleanPropertyValue(Boolean value) {
            super(BooleanPropertyType.this, value);
            index = value ? 1 : 0;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public Byte getSerializedValue() {
            return (byte) (value ? 1 : 0);
        }

        @Override
        public String toString() {
            return "BoolPropertyValue(name=" + name + ", value=" + value + ")";
        }
    }
}
