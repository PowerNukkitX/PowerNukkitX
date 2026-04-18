package cn.nukkit.entity.ai.memory.codec;


public class BooleanMemoryCodec extends MemoryCodec<Boolean> {
    public BooleanMemoryCodec(String key) {
        super(
                tag -> tag.containsKey(key) ? tag.getBoolean(key) : null,
                (data, tag) -> tag.toBuilder().putBoolean(key, data).build()
        );
    }
}
