package cn.nukkit.entity.ai.memory.codec;


import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.NumberTag;

@SuppressWarnings("unchecked")
public class NumberMemoryCodec<Data extends Number> extends MemoryCodec<Data> {
    public NumberMemoryCodec(String key) {
        super(
                tag -> tag.containsNumber(key) ? (Data) ((NumberTag<?>) tag.get(key)).getData() : null,
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
