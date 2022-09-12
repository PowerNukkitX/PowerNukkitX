package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.Nullable;

@PowerNukkitXOnly
@Since("1.19.21-r4")
public abstract class IntegerMemory implements IMemory<Integer> {

    private Integer data;

    public IntegerMemory(Integer data) {
        this.data = data;
    }

    @Nullable
    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public void setData(@Nullable Integer data) {
        this.data = data;
    }
}
