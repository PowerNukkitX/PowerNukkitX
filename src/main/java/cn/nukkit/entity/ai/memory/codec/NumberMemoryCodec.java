package cn.nukkit.entity.ai.memory.codec;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.*;

@PowerNukkitXOnly
@Since("1.19.63-r1")
public class NumberMemoryCodec<Data extends Number> extends MemoryCodec<Data> {
    public NumberMemoryCodec(String key) {
        super(
                tag -> tag.contains(key) ? ((NumberTag<Data>) tag.get(key)).getData() : null,
                (data, tag) -> tag.put(key, newTag(data))
        );
    }

    protected static NumberTag<?> newTag(Number data) {
        if (data instanceof Byte) {
            return new ByteTag((Integer) data);
        } else if (data instanceof Short) {
            return new ShortTag((Integer) data);
        } else if (data instanceof Integer) {
            return new IntTag((Integer) data);
        } else if (data instanceof Long) {
            return new LongTag((Long) data);
        } else if (data instanceof Float) {
            return new FloatTag((Float) data);
        } else if (data instanceof Double) {
            return new DoubleTag((Double) data);
        } else {
            throw new IllegalArgumentException("Unknown number type: " + data.getClass().getName());
        }
    }
}
