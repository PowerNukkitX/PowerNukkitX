package org.powernukkitx.entity.ai.memory.codec;


import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.NumberTag;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public class NumberMemoryCodec<Data extends Number> extends MemoryCodec<Data> {
    public NumberMemoryCodec(String key) {
        this(key, number -> (Data) number);
    }

    public NumberMemoryCodec(String key, Function<Number, Data> converter) {
        super(
                tag -> tag.containsNumber(key) ? converter.apply(((NumberTag<?>) tag.get(key)).getData()) : null,
                (data, tag) -> newTag(data, key, tag)
        );
    }

    protected static CompoundTag newTag(Number data, String key, CompoundTag tag) {
        if (data instanceof Byte) {
            return tag.putByte(key, data.byteValue());
        } else if (data instanceof Short) {
            return tag.putShort(key, data.shortValue());
        } else if (data instanceof Integer) {
            return tag.putInt(key, data.intValue());
        } else if (data instanceof Long) {
            return tag.putLong(key, data.longValue());
        } else if (data instanceof Float) {
            return tag.putFloat(key, data.floatValue());
        } else if (data instanceof Double) {
            return tag.putDouble(key, data.doubleValue());
        } else {
            throw new IllegalArgumentException("Unknown number type: " + data.getClass().getName());
        }
    }
}
