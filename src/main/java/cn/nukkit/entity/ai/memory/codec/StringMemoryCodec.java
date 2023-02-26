package cn.nukkit.entity.ai.memory.codec;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.63-r1")
public class StringMemoryCodec extends MemoryCodec<String> {
    public StringMemoryCodec(String key) {
        super(
                tag -> tag.contains(key) ? tag.getString(key) : null,
                (data, tag) -> tag.putString(key, data)
        );
    }
}
