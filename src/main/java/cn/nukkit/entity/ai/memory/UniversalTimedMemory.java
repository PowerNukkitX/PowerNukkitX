package cn.nukkit.entity.ai.memory;

import org.jetbrains.annotations.Nullable;

/**
 * @author LT_Name
 */
public abstract class UniversalTimedMemory implements IMemory<Integer>, TimedMemory {

    protected Integer time;

    public UniversalTimedMemory() {
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
