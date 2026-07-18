package org.powernukkitx.entity.ai.memory.codec;


public class StringMemoryCodec extends MemoryCodec<String> {
    public StringMemoryCodec(String key) {
        super(
                tag -> tag.containsString(key) ? tag.getString(key) : null,
                (data, tag) -> tag.putString(key, data)
        );
    }
}
