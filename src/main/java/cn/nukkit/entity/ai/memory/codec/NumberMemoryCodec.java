package cn.nukkit.entity.ai.memory.codec;


import org.cloudburstmc.nbt.NbtMap;

@SuppressWarnings("unchecked")
public class NumberMemoryCodec<Data extends Number> extends MemoryCodec<Data> {
    public NumberMemoryCodec(String key) {
        super(
                tag -> tag.containsKey(key) ? (Data) tag.get(key) : null,
                (data, tag) -> newTag(data, key, tag)
        );
    }

    protected static NbtMap newTag(Number data, String key, NbtMap tag) {
        if (data instanceof Byte) {
            return tag.toBuilder().putDouble(key, data.byteValue()).build();
        } else if (data instanceof Short) {
            return tag.toBuilder().putDouble(key, data.shortValue()).build();
        } else if (data instanceof Integer) {
            return tag.toBuilder().putDouble(key, data.intValue()).build();
        } else if (data instanceof Long) {
            return tag.toBuilder().putDouble(key, data.longValue()).build();
        } else if (data instanceof Float) {
            return tag.toBuilder().putDouble(key, data.floatValue()).build();
        } else if (data instanceof Double) {
            return tag.toBuilder().putDouble(key, data.doubleValue()).build();
        } else {
            throw new IllegalArgumentException("Unknown number type: " + data.getClass().getName());
        }
    }
}