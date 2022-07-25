package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.Nullable;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class InLoveMemory implements IMemory<Integer>,TimedMemory{

    protected Integer time;

    public InLoveMemory() {
        time = null;
    }

    @Override
    public int getTime() {
        return time;
    }

    @Nullable
    @Override
    public Integer getData() {
        return time;
    }

    @Override
    public void setData(@Nullable Integer data) {
        this.time = data;
    }
}
