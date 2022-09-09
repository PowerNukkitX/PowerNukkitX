package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BurnTimeMemory extends UniversalTimedMemory {
    public BurnTimeMemory() {
        super();
    }

    public BurnTimeMemory(int time) {
        this.time = time;
    }
}
