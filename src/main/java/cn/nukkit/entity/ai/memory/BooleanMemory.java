package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class BooleanMemory implements IMemory<Boolean> {

    protected Boolean data;

    public BooleanMemory(Boolean data) {
        //若data==false,则将会设置data为null，稍后MemoryStorage会直接删除对应的key-value对
        if (!data)
            this.data = null;
        else
            this.data = true;
    }

    @Override
    public Boolean getData() {
        return data;
    }
}
