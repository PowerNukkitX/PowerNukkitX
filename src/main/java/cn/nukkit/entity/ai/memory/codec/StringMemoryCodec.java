package cn.nukkit.entity.ai.memory.codec;


public class StringMemoryCodec extends MemoryCodec<String> {
    public StringMemoryCodec(String key) {
        super(
                tag -> tag.contains(key) ? tag.getString(key) : null,
                (data, tag) -> tag.putString(key, data)
        );
    }
}
