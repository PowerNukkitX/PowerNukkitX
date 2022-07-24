package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class BooleanMemory implements IMemory<Boolean> {

    protected boolean data;

    public BooleanMemory(boolean data) {
        this.data = data;
    }

    @Override
    public Boolean getData() {
        return data;
    }

    @Override
    public void setData(Boolean data) {
        this.data = data;
    }
}
