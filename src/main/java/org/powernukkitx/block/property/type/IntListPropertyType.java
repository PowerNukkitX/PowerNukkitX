package org.powernukkitx.block.property.type;

import org.powernukkitx.utils.Utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * An integer block property whose values are an explicit list, such as {@code [0, 2, 4]}, rather than a
 * contiguous range.
 * <p>
 * {@link IntPropertyType} covers every integer between its minimum and maximum. A behaviour pack may
 * declare an integer state with gaps, and the client then knows exactly the listed values; declaring the
 * range instead would register states the client does not have. Values are serialised as integers, like
 * {@link IntPropertyType}'s, and indexed by their position in the list, so the declaration order is part of
 * the block's state encoding.
 */
public final class IntListPropertyType extends BaseBlockPropertyType<Integer> {

    private final Int2ObjectOpenHashMap<IntListPropertyValue> cachedValues = new Int2ObjectOpenHashMap<>();

    private IntListPropertyType(String name, List<Integer> validValues, Integer defaultData) {
        super(name, validValues, defaultData, Utils.computeRequiredBits(0, validValues.size() - 1));
        for (int i = 0; i < validValues.size(); i++) {
            cachedValues.put(validValues.get(i).intValue(), new IntListPropertyValue(validValues.get(i), i));
        }
    }

    /**
     * @param name        the state name
     * @param validValues the allowed values, without duplicates; their order fixes each value's index
     * @param defaultData the value a block takes when the state is not specified
     * @throws IllegalArgumentException when there are no values, a value repeats, or the default is not one
     *                                  of them
     */
    public static IntListPropertyType of(String name, List<Integer> validValues, int defaultData) {
        if (validValues == null || validValues.isEmpty()) {
            throw new IllegalArgumentException("Integer list property " + name + " must have at least one value");
        }
        List<Integer> values = new ArrayList<>(validValues);
        if (values.stream().distinct().count() != values.size()) {
            throw new IllegalArgumentException("Integer list property " + name + " repeats a value");
        }
        if (!values.contains(defaultData)) {
            throw new IllegalArgumentException("Default value " + defaultData + " is not a valid value of property " + name);
        }
        return new IntListPropertyType(name, values, defaultData);
    }

    @Override
    public Type getType() {
        return Type.INT;
    }

    @Override
    public IntListPropertyValue createValue(Integer value) {
        IntListPropertyValue cached = cachedValues.get(value.intValue());
        if (cached == null) {
            throw new IllegalArgumentException("Invalid value for integer list property " + name + ": " + value);
        }
        return cached;
    }

    @Override
    public IntListPropertyValue tryCreateValue(Object value) {
        if (value instanceof Number number) {
            IntListPropertyValue cached = cachedValues.get(number.intValue());
            if (cached != null) {
                return cached;
            }
        }
        throw new IllegalArgumentException("Invalid value for integer list property " + name + ": " + value);
    }

    public final class IntListPropertyValue extends BlockPropertyType.BlockPropertyValue<Integer, IntListPropertyType, Integer> {

        private final int index;

        IntListPropertyValue(Integer value, int index) {
            super(IntListPropertyType.this, value);
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public Integer getSerializedValue() {
            return value;
        }

        @Override
        public String toString() {
            return "IntListPropertyValue(name=" + name + ", value=" + value + ")";
        }
    }

    @Override
    public String toString() {
        return "IntListPropertyType(name=" + name + ", values=" + validValues + ")";
    }
}
