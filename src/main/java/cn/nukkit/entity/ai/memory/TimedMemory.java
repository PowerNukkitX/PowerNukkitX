package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface TimedMemory {
    /**
     * @return 此Memory刷新时服务端的Tick数
     */
    int getTime();
}
