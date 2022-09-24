package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.21-r4")
public class RouteUnreachableTimeMemory extends IntegerMemory {
    public RouteUnreachableTimeMemory() {
        super(null);
    }

    public RouteUnreachableTimeMemory(Integer data) {
        super(data);
    }
}
