package cn.nukkit.entity.ai.memory.codec;

import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.ShortTag;


@SuppressWarnings("unchecked")
public class NumberMemoryCodec<Data extends Number> extends MemoryCodec<Data> {
    public NumberMemoryCodec(String key) {
        super(
                tag -> tag.contains(key) ? (new TagReader<>((NumberTag<Data>) tag.get(key))).getData() : null,
                (data, tag) -> tag.put(key, newTag(data))
        );
    }

    protected static NumberTag<?> newTag(Number data) {
        if (data instanceof Byte) {
            return new ByteTag(data.byteValue());
        } else if (data instanceof Short) {
            return new ShortTag(data.shortValue());
        } else if (data instanceof Integer) {
            return new IntTag(data.intValue());
        } else if (data instanceof Long) {
            return new LongTag(data.longValue());
        } else if (data instanceof Float) {
            return new FloatTag(data.floatValue());
        } else if (data instanceof Double) {
            return new DoubleTag(data.doubleValue());
        } else {
            throw new IllegalArgumentException("Unknown number type: " + data.getClass().getName());
        }
    }

    private static class TagReader<Data extends Number> {
        NumberTag<Data> tag;

        public TagReader(NumberTag<Data> tag) {
            this.tag = tag;
        }

        Data getData() {
            String simpleName = tag.getClass().getSimpleName();
            Number data = tag.getData();
            //hack convert byteTag and shortTag because they data storage is all int type
            if (simpleName.equals("ByteTag")) {
                return (Data) Byte.valueOf(data.byteValue());
            } else if (simpleName.equals("ShortTag")) {
                return (Data) Short.valueOf(data.shortValue());
            } else if (data instanceof Integer) {
                return tag.getData();
            } else if (data instanceof Long) {
                return tag.getData();
            } else if (data instanceof Float) {
                return tag.getData();
            } else if (data instanceof Double) {
                return tag.getData();
            } else {
                throw new IllegalArgumentException("Unknown number type: " + data.getClass().getName());
            }
        }
    }
}
