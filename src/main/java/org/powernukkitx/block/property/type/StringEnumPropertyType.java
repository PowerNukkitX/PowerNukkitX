package org.powernukkitx.block.property.type;

import org.powernukkitx.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An enum block property whose values are plain strings rather than a Java enum.
 * <p>
 * {@link EnumPropertyType} needs an enum class to describe its values, which only works for
 * states known at compile time. A block defined in a behaviour pack declares its states in JSON
 * read at runtime, so no such class can exist for it. This type gives those states the same ENUM
 * semantics - indexed values, serialised as strings - backed by a value list instead.
 * <p>
 * Values are indexed by their position in the list, so the declaration order is part of the
 * block's state encoding and must stay stable for a given block.
 */
public final class StringEnumPropertyType extends BaseBlockPropertyType<String> {

    private final Map<String, StringEnumPropertyValue> cachedValues = new HashMap<>();

    private StringEnumPropertyType(String name, List<String> validValues, String defaultData) {
        super(name, validValues, defaultData, Utils.computeRequiredBits(0, validValues.size() - 1));
        for (int i = 0; i < validValues.size(); i++) {
            cachedValues.put(validValues.get(i), new StringEnumPropertyValue(validValues.get(i), i));
        }
    }

    /**
     * @param name        the state name, such as {@code minecraft:cardinal_direction}
     * @param validValues the allowed values; their order fixes each value's index
     * @param defaultData the value a block takes when the state is not specified
     * @throws IllegalArgumentException when there are no values, or the default is not one of them
     */
    public static StringEnumPropertyType of(String name, List<String> validValues, String defaultData) {
        if (validValues == null || validValues.isEmpty()) {
            throw new IllegalArgumentException("String enum property " + name + " must have at least one value");
        }
        List<String> values = new ArrayList<>(validValues);
        if (!values.contains(defaultData)) {
            throw new IllegalArgumentException("Default value " + defaultData + " is not a valid value of property " + name);
        }
        return new StringEnumPropertyType(name, values, defaultData);
    }

    @Override
    public Type getType() {
        return Type.ENUM;
    }

    @Override
    public StringEnumPropertyValue createValue(String value) {
        StringEnumPropertyValue cached = cachedValues.get(value);
        if (cached == null) {
            throw new IllegalArgumentException("Invalid value for string enum property " + name + ": " + value);
        }
        return cached;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Falls back to a lower-cased match, because pack JSON and the values a client sends back do
     * not always agree on case.
     */
    @Override
    public StringEnumPropertyValue tryCreateValue(Object value) {
        if (value instanceof String string) {
            StringEnumPropertyValue cached = cachedValues.get(string);
            if (cached == null) {
                cached = cachedValues.get(string.toLowerCase(Locale.ENGLISH));
            }
            if (cached != null) {
                return cached;
            }
        }
        throw new IllegalArgumentException("Invalid value for string enum property " + name + ": " + value);
    }

    public final class StringEnumPropertyValue extends BlockPropertyType.BlockPropertyValue<String, StringEnumPropertyType, String> {

        private final int index;

        StringEnumPropertyValue(String value, int index) {
            super(StringEnumPropertyType.this, value);
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public String getSerializedValue() {
            return value;
        }

        @Override
        public String toString() {
            return "StringEnumPropertyValue(name=" + name + ", value=" + value + ")";
        }
    }

    @Override
    public String toString() {
        return "StringEnumPropertyType(name=" + name + ", values=" + validValues + ")";
    }
}
