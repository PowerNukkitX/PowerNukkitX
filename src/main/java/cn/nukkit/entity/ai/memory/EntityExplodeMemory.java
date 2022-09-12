package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;
import lombok.Setter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
@Setter
public class EntityExplodeMemory extends BooleanMemory {

    protected boolean cancellable = true;

    public EntityExplodeMemory() {
        super(null);
    }

    public EntityExplodeMemory(boolean data) {
        super(data);
    }
}
