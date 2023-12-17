package cn.nukkit.entity.ai.memory.codec;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BooleanMemoryCodec extends MemoryCodec<Boolean> {
    public BooleanMemoryCodec(String key) {
        super(
                tag -> tag.contains(key) ? tag.getBoolean(key) : null,
                (data, tag) -> tag.putBoolean(key, data)
        );
    }
}
