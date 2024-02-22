package cn.nukkit.block.property.type;

import cn.nukkit.utils.Utils;
import lombok.Getter;

import java.util.stream.IntStream;

/**
 * Allay Project 2023/3/19
 *
 * @author daoge_cmd
 */
public final class IntPropertyType extends BaseBlockPropertyType<Integer> {

    private final IntPropertyValue[] cachedValues;
    @Getter
    private final int min;
    @Getter
    private final int max;

    private IntPropertyType(String name, int min, int max, Integer defaultData) {
        super(name, IntStream.range(min, max + 1).boxed().toList(), defaultData, Utils.computeRequiredBits(min, max));
        this.min = min;
        this.max = max;
        cachedValues = new IntPropertyValue[max - min + 1];
        for (int i = min; i <= max; i++) {
            IntPropertyValue value = new IntPropertyValue(i);
            cachedValues[i] = value;
        }
    }

    public static IntPropertyType of(String name, int min, int max, Integer defaultData) {
        return new IntPropertyType(name, min, max, defaultData);
    }

    @Override
    public Type getType() {
        return Type.INT;
    }

    @Override
    public IntPropertyValue createValue(Integer value) {
        return cachedValues[value - min];
    }

    @Override
    public IntPropertyValue tryCreateValue(Object value) {
        if (value instanceof Number number) {
            return cachedValues[number.intValue() - min];
        } else throw new IllegalArgumentException("Invalid value for int property type: " + value);
    }

    public final class IntPropertyValue extends BlockPropertyType.BlockPropertyValue<Integer, IntPropertyType, Integer> {

        IntPropertyValue(Integer value) {
            super(IntPropertyType.this, value);
        }

        @Override
        public int getIndex() {
            return value - min;
        }

        @Override
        public Integer getSerializedValue() {
            return value;
        }

        @Override
        public String toString() {
            return "IntPropertyValue(name=" + name + ", value=" + value + ")";
        }
    }
}
