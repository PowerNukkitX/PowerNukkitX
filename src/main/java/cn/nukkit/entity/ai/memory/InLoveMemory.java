package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;
import lombok.Setter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class InLoveMemory extends UniversalTimedMemory {

    @Getter
    @Setter
    protected boolean isInLove;

    public InLoveMemory() {
        time = null;
    }

}
