package org.powernukkitx.entity.ai.memory.codec;


public class BooleanMemoryCodec extends MemoryCodec<Boolean> {
    public BooleanMemoryCodec(String key) {
        super(
                tag -> tag.containsByte(key) ? tag.getBoolean(key) : null,
                (data, tag) -> tag.putBoolean(key, data)
        );
    }
}
